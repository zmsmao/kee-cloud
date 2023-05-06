package com.kee.common.reptiles.utils;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * @author Admin
 * @Description : TODO
 */
public class WebDriverAcquisitionUtil {

    public static WebDriver chromeDriver(String path){
        System.setProperty("webdriver.chrome.driver",path);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        return new ChromeDriver(options);
    }

    public static WebDriver chromeDriver(String path, ChromeOptions options){
        System.setProperty("webdriver.chrome.driver",path);
        options.addArguments("--remote-allow-origins=*");
        return new ChromeDriver(options);
    }

    public static WebDriver chromeDriver(String path, ChromeOptions options, ChromeDriverService service){
        System.setProperty("webdriver.chrome.driver",path);
        options.addArguments("--remote-allow-origins=*");
        return new ChromeDriver(service,options);
    }

    public static WebDriver chromeDriver(String path, ChromeDriverService service){
        System.setProperty("webdriver.chrome.driver",path);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        return new ChromeDriver(service,options);
    }
}
