package com.kee.common.core.utils.poi;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.AttachmentRenderData;
import com.deepoove.poi.data.AttachmentType;
import com.deepoove.poi.data.Attachments;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.deepoove.poi.policy.AttachmentRenderPolicy;
import com.deepoove.poi.util.PoitlIOUtils;

import com.kee.common.core.annotation.Word;
import com.kee.common.core.utils.StringUtils;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import lombok.Data;
import org.ddr.poi.html.HtmlRenderPolicy;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 具体word模板格式参考：http://deepoove.com/poi-tl/
 * @Description : TODO
 * @author zms
 */
@Data
public class WordUtils {

    /**
     * @param response     请求响应
     * @param objectName   映射对象名称
     * @param object       映射对象
     * @param templateName 模板路径名称
     */
    public static void downloadWord(HttpServletResponse response, String objectName, Object object, String templateName) {
        try {
            IXDocReport report = report(templateName);
            IContext context = report.createContext();
            //替换word模板中创建的域的变量
            context.put(objectName, object);
            //设置请求头
            setResponseHeader(response);
            //游览器输出
            setResponseOutput(response, report, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param response     请求响应
     * @param objectName   映射对象名称
     * @param object       映射对象
     * @param listName     支持list名称
     * @param list         单独支持list
     * @param templateName 模板路径名称
     */
    public static <K> void downloadWord(HttpServletResponse response, String objectName, Object object, String listName, List<K> list, String templateName) {
        try {
            IXDocReport report = report(templateName);
            IContext context = report.createContext();
            //替换word模板中创建的域的变量
            context.put(objectName, object);
            //设置list
            setListFieldsMetadata(listName, list, report, context);
            //设置请求头
            setResponseHeader(response);
            //游览器输出
            setResponseOutput(response, report, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
                    .render(dataSMap);
            setResponseHeader(response);
            setResponseOutput(response,xwpfTemplate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <K> Map<String, Object> mapperSingleEntity(K obj,Configure config) throws Exception{
        Map<String, Object> dataMap = new HashMap<>(16);
        Map<Field, Word> map = getAnnotationByFiled(obj,Word.class);
        for (Map.Entry<Field, Word> entry : map.entrySet()) {
            Field key = entry.getKey();
            Word value = entry.getValue();
            String name = value.name();
            Object o = key.get(obj);
            //动态表单
            if (value.type() == Word.TypeField.LIST) {
                LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
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
                if (key.getType() == String.class || o instanceof String) {
                    if (StringUtils.isEmpty(name)) {
                        name = key.getName();
                    }
                    dataMap.put(name, o);
                    config.customPolicy(name, htmlRenderPolicy);
                }
            }
            //附件
            else if (value.type() == Word.TypeField.ATTACHMENT) {
                AttachmentRenderPolicy attachmentRenderPolicy =  new AttachmentRenderPolicy();
                if (key.getType() == String.class || o instanceof String) {
                    if (StringUtils.isEmpty(name)) {
                        name = key.getName();
                    }
                    String url = (String) o;
                    AttachmentRenderData attach;
                    if("xlsx".equals(url.substring(url.length()-4).toUpperCase())) {
                        attach = Attachments.ofLocal((String) o, AttachmentType.XLSX).create();
                    }
                    else{
                        attach = Attachments.ofLocal((String) o, AttachmentType.DOCX).create();
                    }
                    dataMap.put(name,attach);
                    config.customPolicy(name,attachmentRenderPolicy);
                }
            }
            //默认文本
            else {
                if (StringUtils.isEmpty(name)) {
                    name = key.getName();
                }
                dataMap.put(name, o);
            }
        }
        return dataMap;
    }

    private static <T extends Annotation> Map<Field, T> getAnnotationByFiled(Object obj, Class<T> annotationClass) {
        //存储对象和接口
        Map<Field, T> map = new HashMap<>(16);
        //获取类加载器
        if (StringUtils.isNull(obj)) {
            return map;
        }
        Class<?> clazz = obj.getClass();
        //获取class
        Field[] declaredFields = clazz.getDeclaredFields();
        //获取加入注解的属性
        for (Field field : declaredFields) {
            T word = field.getAnnotation(annotationClass);
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
    public static void setResponseHeader(HttpServletResponse response) {
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
     * 传入模板路径，生成FreeMarker模板word
     *
     * @param templateName
     * @return
     */
    private static IXDocReport report(String templateName) {
        InputStream resourceAsStream = null;
        IXDocReport report = null;
        try {
            ClassPathResource classPathResource = new ClassPathResource(templateName);
            resourceAsStream = classPathResource.getInputStream();
            report = XDocReportRegistry.getRegistry().loadReport(resourceAsStream, TemplateEngineKind.Freemarker);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (resourceAsStream != null) {
                    resourceAsStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return report;
    }

    /**
     * 整合下载
     *
     * @param response 响应体
     * @param template 模板
     */
    public static void setResponseOutput(HttpServletResponse response, XWPFTemplate template) {
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
     * 整合下载
     *
     * @param response 响应体
     * @param report   word模板解析
     * @param context  word模板解析内容
     */
    private static void setResponseOutput(HttpServletResponse response, IXDocReport report, IContext context) {
        //缓冲流
        ByteArrayOutputStream bout = null;
        // 创建输出流
        ServletOutputStream out = null;
        try {
            // 输出流赋值
            out = response.getOutputStream();
            bout = new ByteArrayOutputStream();
            //输出到游览器
            report.process(context, bout);
            out.write(bout.toByteArray());
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
     * 本地测试
     *
     * @param report  word模板解析
     * @param context word模板解析内容
     */
    private static void setLocalFileOutPut(IXDocReport report, IContext context) {
        //缓冲流
        ByteArrayOutputStream bout = null;
        // 创建输出流
        FileOutputStream out = null;
        try {
            // 输出流赋值
            out = new FileOutputStream(new File("D:\\template.docx"));
            bout = new ByteArrayOutputStream();
            //输出到本地
            report.process(context, bout);
            out.write(bout.toByteArray());
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
     * 设定list
     */
    private static <K> void setListFieldsMetadata(String listName, List<K> list, IXDocReport report, IContext context) {
        if (!list.isEmpty()) {
            K obj = list.get(0);
            String[] filedName = getFiledName(obj);
            //设定list对应表格参数
            FieldsMetadata metadata = new FieldsMetadata();
            for (String field : filedName) {
                metadata.addFieldAsList(listName + "." + field);
            }
            report.setFieldsMetadata(metadata);
            //替换word模板中创建的list变量
            context.put(listName, list);
        }
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
}
