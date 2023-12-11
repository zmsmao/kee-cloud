package com.kee.common.core.utils.poi;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.deepoove.poi.util.PoitlIOUtils;
import com.kee.common.core.annotation.Word;
import com.kee.common.core.utils.StringUtils;
import lombok.Data;
import org.ddr.poi.html.HtmlRenderPolicy;
import org.springframework.core.io.ClassPathResource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;


/**
 * @Description : TODO
 * @author: zeng.maosen
 * @date: 2022/12/14
 * @version: 1.0
 */
@Data
public class WordUtils {
    /**
     * 此方法必须在对象属性上加入@Word
     * 支持复杂导出
     *
     * @param response     响应体
     * @param obj          对象
     * @param templateName 模板
     * @param <K>          类型
     */
    public static <K> void downloadWordByAnnotation(HttpServletResponse response, K obj, String templateName) {
        try {
            Configure config = Configure.builder().useSpringEL().build();
            ClassPathResource classPathResource = new ClassPathResource(templateName);
            //设置请求头
            Map<String, Object> dataMap = mapperSingleEntity(obj, config);
            XWPFTemplate xwpfTemplate = XWPFTemplate.compile(classPathResource.getInputStream(), config)
                    .render(dataMap);
            setResponseHeader(response);
            setResponseOutput(response,xwpfTemplate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 1.此方法必须在对象属性上加入@Word
     * 2.支持复杂导出,list不能为空，也不能为空数组
     * 3.循环模板是所解析的字段是body
     * @param response     响应体
     * @param list         列表
     * @param templateName 模板
     * @param <K>          类型
     */
    public static <K> void downloadWordByAnnotation(HttpServletResponse response, List<K> list, String templateName) {
        downloadWordByAnnotation(response,list,templateName,"");
    }

    /**
     * 1.此方法必须在对象属性上加入@Word
     * 2.支持复杂导出,list不能为空，也不能为空数组
     *
     * @param response     响应体
     * @param list         列表
     * @param templateName 模板
     * @param <K>          类型
     * @param duplicateName 循环模板是所解析的字段,默认不填是body
     */
    public static <K> void downloadWordByAnnotation(HttpServletResponse response, List<K> list, String templateName,String duplicateName) {
        if(list==null){
            throw new  RuntimeException("导出数据为空");
        }
        if(list.isEmpty()){
            throw new  RuntimeException("导出数据为空");
        }
        String name;
        if(StringUtils.isEmpty(duplicateName)){
            name = "body";
        }else{
            name = duplicateName;
        }
        try {
            Configure config = Configure.builder().useSpringEL().build();
            ClassPathResource classPathResource = new ClassPathResource(templateName);
            //设置请求头
            List<Map<String,Object>> dataS = new ArrayList<>();
            for (K obj : list) {
                Map<String, Object> map = mapperSingleEntity(obj, config);
                dataS.add(map);
            }
            //填充map
            HashMap<String,Object> dataSMap = new HashMap<>(16);
            dataSMap.put(name,dataS);
            XWPFTemplate xwpfTemplate = XWPFTemplate.compile(classPathResource.getInputStream(), config)
                    .render(dataS);
            setResponseHeader(response);
            setResponseOutput(response,xwpfTemplate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <K> Map<String, Object> mapperSingleEntity(K obj,Configure config) throws Exception{
        Map<String, Object> dataMap = new HashMap<>(16);
        Map<Field, Word> map = getAnnotationByFiled(obj);
        for (Map.Entry<Field, Word> entry : map.entrySet()) {
            Field key = entry.getKey();
            Word value = entry.getValue();
            String name = value.name();
            //动态表单
            if (value.type() == Word.TypeField.LIST) {
                LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
                Object o = key.get(obj);
                if (key.getType() == List.class || o instanceof List) {
                    if (StringUtils.isEmpty(name)) {
                        name = key.getName();
                    }
                    dataMap.put(name, o);
                    config.customPolicy(name, policy);
                }
            }
            //嵌入富文本
            else if (value.type() == Word.TypeField.HTMLTEXT) {
                HtmlRenderPolicy htmlRenderPolicy = new HtmlRenderPolicy();
                Object o = key.get(obj);
                if (key.getType() == String.class || o instanceof String) {
                    if (StringUtils.isEmpty(name)) {
                        name = key.getName();
                    }
                    dataMap.put(name, o);
                    config.customPolicy(name, htmlRenderPolicy);
                }
            }
            //默认文本
            else {
                if (StringUtils.isEmpty(name)) {
                    name = key.getName();
                }
                Object o = key.get(obj);
                dataMap.put(name, o);
            }
        }
        return dataMap;
    }

    private static <K> Map<Field, Word> getAnnotationByFiled(K obj) {
        //存储对象和接口
        Map<Field, Word> map = new HashMap<>(16);
        //获取类加载器
        if (StringUtils.isNull(obj)) {
            return map;
        }
        Class<?> clazz = obj.getClass();
        //初始化导出类型
        Word.TypeField wField = Word.TypeField.NATURE;
        //获取class
        Field[] declaredFields = clazz.getDeclaredFields();
        //获取加入注解的属性
        for (Field field : declaredFields) {
            Word word = field.getAnnotation(Word.class);
            if (StringUtils.isNotNull(word)) {
                makeAccessible(field);
                map.put(field, word);
            }
        }
        return map;
    }


    /**
     * 设置响应头
     *
     * @param response
     */
    private static void setResponseHeader(HttpServletResponse response) {
        try {
            // 设置response的编码方式
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream;charset=UTF-8");
            String s = "attachment;filename=response.docx";
            response.setHeader("Content-Disposition", s);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 整合下载
     *
     * @param response 响应体
     * @param template 模板
     */
    private static void setResponseOutput(HttpServletResponse response, XWPFTemplate template) {
        //缓冲流
        ByteArrayOutputStream bout = null;
        // 创建输出流
        ServletOutputStream out = null;
        try {
            // 输出流赋值
            out = response.getOutputStream();
            bout = new ByteArrayOutputStream();
            //输出到游览器
            template.write(bout);
            out.write(bout.toByteArray());
            PoitlIOUtils.closeQuietlyMulti(template, bout, out);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("导出word失败!");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (bout != null) {
                    bout.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * 获取属性名数组
     */
    private static String[] getFiledName(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        return fieldNames;
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

    private static boolean getLicense() {
        boolean result = false;
        try {
            ClassPathResource classPathResource = new ClassPathResource("xml\\license.xml");
            License aposeLic = new License();
            aposeLic.setLicense(classPathResource.getInputStream());
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void doc2pdf(String inPath, String outPath) {
        // 验证License 若不验证则转化出的pdf文档会有水印产生
        if (!getLicense()) {
            return;
        }
        try {
            long old = System.currentTimeMillis();
            // 新建一个空白pdf文档
            File file = new File(outPath);
            FileOutputStream os = new FileOutputStream(file);
            // Address是将要被转化的word文档
            Document doc = new Document(inPath);
            // 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            doc.save(os, SaveFormat.PDF);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("word转pdf失败");
        }
    }
}
