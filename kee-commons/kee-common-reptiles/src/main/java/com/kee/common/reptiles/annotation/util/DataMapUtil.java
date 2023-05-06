package com.kee.common.reptiles.annotation.util;

import com.kee.common.core.utils.StringUtils;
import com.kee.common.reptiles.annotation.DataMap;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

/**
 * @Description : @DataMap
 * @author: zeng.maosen
 */
public class DataMapUtil {


    /**
     * 填充数据表
     *
     * @param driver
     * @param o
     */
    public static void dataPadding(WebDriver driver, Object o) throws Exception {
        dataPadding(driver, o, new ArrayList<>());
    }

    /**
     * 填充数据表
     *
     * @param driver
     * @param o
     * @param fileList
     */
    public static void dataPadding(WebDriver driver, Object o, List<String> fileList) throws Exception {
        //设置查询元素等待时间
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(60))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(NoSuchElementException.class);
        Map<Field, DataMap> fieldAnnotationMap = getFieldAnnotationMap(o);
        TreeMap<DataMap, Field> orderSelectMap = new TreeMap<>(Comparator.comparingInt(DataMap::order));
        for (Map.Entry<Field, DataMap> entry : fieldAnnotationMap.entrySet()) {
            Field field = entry.getKey();
            DataMap dataMap = entry.getValue();
            Object obj = field.get(o);
            if (dataMap.dataType() == DataMap.DataType.DATA_BOX) {
                WebElement element = getWebElement(dataMap, wait);
                String value = "";
                String key = "document.querySelector(\"#" + dataMap.id() + "\").removeAttribute(\"readonly\")";
                JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
                if (field.getType() == Date.class && obj instanceof Date) {
                    SimpleDateFormat format = new SimpleDateFormat(dataMap.format());
                    Date date = (Date) obj;
                    value = format.format(date);
                }
                if (field.getType() == String.class && obj instanceof String) {
                    value = (String) obj;
                }
                javascriptExecutor.executeScript(key, element);
                if (!element.getAttribute("value").isEmpty()) {
                    element.clear();
                }
                element.sendKeys(value);
            } else if (dataMap.dataType() == DataMap.DataType.SELECTION_BOX) {
                String[] xpathS = dataMap.xpathS();
                if (xpathS.length != 0) {
                    for (int i = 0; i < xpathS.length; i++) {
                        Thread.sleep(500);
                        driver.getWindowHandle();
                        Thread.sleep(500);
                        setSelectBox(field, getWebElement(dataMap.xpathS()[i], wait), o);
                    }
                } else {
                    orderSelectMap.put(dataMap, field);
                }
            } else if (dataMap.dataType() == DataMap.DataType.UL_BOX) {
                String value = "";
                if (field.getType() == String.class && obj instanceof String) {
                    value = (String) obj;
                }
                String[] xpathS = dataMap.xpathS();
                if (xpathS.length < 2) {
                    continue;
                }
                int k = 0;
                for (String xpath : xpathS) {
                    webElementClick(xpath, wait);
                    k++;
                    if (k == xpathS.length) {
                        WebElement ul = driver.findElement(By.xpath(xpath));
                        List<WebElement> li = ul.findElements(By.xpath("li"));
                        for (WebElement webElement : li) {
                            WebElement div = webElement.findElement(By.xpath("div"));
                            if (div.getText().equals(value)) {
                                div.click();
                                break;
                            }
                        }
                        break;
                    }
                }
            } else if (dataMap.dataType() == DataMap.DataType.FILE_BOX) {
                WebElement webElement;
                for (String s : fileList) {
                    webElement = getWebElement(dataMap.xpath(), wait);
                    webElement.sendKeys(s);
                }
            } else {
                WebElement element = getWebElement(dataMap, wait);
                String value = "";
                if (field.getType() == String.class && obj instanceof String) {
                    value = (String) obj;
                    element.sendKeys(value);
                }
            }
        }
        orderSelectMap.forEach((key, field) -> setSelectBox(field, getWebElement(key, wait), o));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    /**
     * 获取对象注解
     *
     * @param o
     * @return
     */
    private static Map<Field, DataMap> getFieldAnnotationMap(Object o) {
        Map<Field, DataMap> map = new HashMap<>(16);
        try {
            if (o == null) {
                return map;
            }
            Class<?> aClass = o.getClass();
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                DataMap dataMap = field.getAnnotation(DataMap.class);
                if (dataMap != null) {
                    makeAccessible(field);
                    if (field.get(o) != null && field.get(o) != "") {
                        map.put(field, dataMap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 私有属性变可用
     *
     * @param field
     */
    private static void makeAccessible(Field field) {
        if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
                || Modifier.isFinal(field.getModifiers()) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 获取元素
     *
     * @param dataMap
     * @return
     */
    private static WebElement getWebElement(DataMap dataMap, Wait<WebDriver> wait) {
        WebElement element;
        if (dataMap.waitTime() >= 0) {
            try {
                Thread.sleep(dataMap.waitTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.isNotEmpty(dataMap.id())) {
            element = wait.until(one -> one.findElement(By.id(dataMap.id())));
        } else {
            element = wait.until(one -> one.findElement(By.xpath(dataMap.xpath())));
        }
        return element;
    }

    private static WebElement getWebElement(String s, Wait<WebDriver> wait) throws Exception {
        Thread.sleep(1000);
        return wait.until(one -> one.findElement(By.xpath(s)));
    }

    /**
     * 元素点击事件
     *
     * @param s
     */
    private static void webElementClick(String s, Wait<WebDriver> wait) {
        wait.until(one -> one.findElement(By.xpath(s))).click();
    }

    private static void setSelectBox(Field field, WebElement element, Object o) {
        try {
            String value = "";
            Object obj = field.get(o);
            if (field.getType() == String.class && obj instanceof String) {
                value = (String) obj;
            }
            Select open = new Select(element);
            if (StringUtils.isNotEmpty(value)) {
                for (WebElement option : open.getOptions()) {
                    String text = option.getText();
                    if (value.contains(option.getText())) {
                        open.selectByVisibleText(text);
                        break;
                    }
                }
            } else {
                open.selectByIndex(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
