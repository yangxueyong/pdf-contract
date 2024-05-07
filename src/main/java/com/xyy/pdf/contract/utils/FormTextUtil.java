package com.xyy.pdf.contract.utils;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PRAcroForm;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 表单文本框替换
 */
public class FormTextUtil {
    public static void main1(String[] args) {
        String inputFileName = "D:\\tmp\\tmp1\\借条_原始.pdf";
        String outputFileName = "D:\\tmp\\tmp1\\借条_new.pdf";
        OutputStream os = null;
        PdfStamper ps = null;
        PdfReader reader = null;
        try {
            os = new FileOutputStream(new File(outputFileName));
            // 读入pdf表单
            reader = new PdfReader(inputFileName);
            // 根据表单生成一个新的pdf
            ps = new PdfStamper(reader, os);
            // 获取pdf表单
            AcroFields form = ps.getAcroFields();
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("jkrxm", "张三");
            data.put("jkrsfz", "123123123123123133");
            data.put("cjrxm", "李四");
            data.put("dbrxm", "王五");

            // 遍历data 给pdf表单表格赋值
            for (String key : data.keySet()) {
                form.setField(key, data.get(key).toString());
            }
            //是否禁用表单可写 （禁用之后，表单将不再可写）
//            ps.setFormFlattening(true);
            System.out.println("===============PDF导出成功=============");
        } catch (Exception e) {
            System.out.println("===============PDF导出失败=============");
            e.printStackTrace();
        } finally {
            try {
                ps.close();
                reader.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String inputFileName = "/Users/yxy/work/java/github/pdf-contract/data/借条_new.pdf";
        OutputStream os = null;
        PdfStamper ps = null;
        PdfReader reader = null;
        try {
            // 读入pdf表单
            reader = new PdfReader(inputFileName);
            String jkrsfz1 = reader.getAcroFields().getField("jkrsfz");
            System.out.println("jkrsfz1->" + jkrsfz1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
