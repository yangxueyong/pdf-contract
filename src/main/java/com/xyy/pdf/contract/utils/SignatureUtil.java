package com.xyy.pdf.contract.utils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.*;
import com.xyy.pdf.contract.entity.SignatureInfo;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

/**
 * 替换表单签名
 */
public class SignatureUtil {
    public static final char[] PASSWORD = "123456".toCharArray();//keystory密码

    public static void main(String[] args) {
        try {
            //将证书文件放入指定路径，并读取keystore ，获得私钥和证书链
            //这里我是自己签的一个证书，密码为123456
            /**
             *
             * 正式环境可以用如下命令签署一个p12证书 （前提是确保当前环境已安装jdk，并配置了环境变量）
             * keytool -genkeypair -alias serverkey -keyalg RSA -keysize 2048 -validity 3650 -keystore D:\tmp\p12test.keystore
             *
             * keytool -exportcert -keystore  D:\tmp\p12test.keystore -file D:\tmp\p12test.cer -alias serverkey
             *
             * keytool -importkeystore -srckeystore D:\tmp\p12test.keystore -destkeystore D:\tmp\p12test.p12 -srcalias serverkey -destalias serverkey -srcstoretype jks -deststoretype pkcs12 -noprompt
             *
             *
             */
            //            String pkPath = app.getClass().getResource("/template/zhengshu.p12").getPath();
            String pkPath ="D:\\tmp\\p12test.p12";
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream(pkPath), PASSWORD);
            String alias = ks.aliases().nextElement();
            PrivateKey pk = (PrivateKey) ks.getKey(alias, PASSWORD);
            Certificate[] chain = ks.getCertificateChain(alias);

            //            String src = app.getClass().getResource("D:\\tmp\\check.pdf").getPath();
            String src = "D:\\tmp\\tmp1\\借条_new.pdf";
            String target = "D:\\tmp\\tmp1\\借条_new2.pdf";


            //封装签章信息
            SignatureInfo info = new SignatureInfo();
            info.setReason("随便写个理由");
            info.setLocation("用户签名");
            info.setPk(pk);
            info.setChain(chain);
            info.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);
            info.setDigestAlgorithm(DigestAlgorithms.SHA1);
            info.setFieldName("jkrqz");
            info.setImagePath("D:\\tmp\\tmp1\\qianming.png");
            info.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);

//            SignatureInfo info1 = new SignatureInfo();
//            info1.setReason("理由1");
//            info1.setLocation("位置1");
//            info1.setPk(pk);
//            info1.setChain(chain);
//            info1.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);
//            info1.setDigestAlgorithm(DigestAlgorithms.SHA1);
//            info1.setFieldName("sig2");
//            info1.setImagePath("D:\\tmp\\60b3781d44efbcf7a2fdd2f18650797c.png");
//            info1.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);

            SignatureUtil.sign(src, target, info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 单多次签章通用
     * @param src
     * @param target
     * @param signatureInfos
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws DocumentException
     */
    public static void sign(String src, String target, SignatureInfo... signatureInfos){
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            inputStream = new FileInputStream(src);
            for (SignatureInfo signatureInfo : signatureInfos) {
                ByteArrayOutputStream tempArrayOutputStream = new ByteArrayOutputStream();
                PdfReader reader = new PdfReader(inputStream);
                //创建签章工具PdfStamper ，最后一个boolean参数是否允许被追加签名
                PdfStamper stamper = PdfStamper.createSignature(reader, tempArrayOutputStream, '\0', null, true);
                // 获取数字签章属性对象
                PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
                appearance.setReason(signatureInfo.getReason());
                appearance.setLocation(signatureInfo.getLocation());
                //设置签名的签名域名称，多次追加签名的时候，签名预名称不能一样，图片大小受表单域大小影响（过小导致压缩）
                appearance.setVisibleSignature(signatureInfo.getFieldName());
                //读取图章图片
                Image image = Image.getInstance(signatureInfo.getImagePath());
                appearance.setSignatureGraphic(image);
                appearance.setCertificationLevel(signatureInfo.getCertificationLevel());
                //设置图章的显示方式，如下选择的是只显示图章（还有其他的模式，可以图章和签名描述一同显示）
                appearance.setRenderingMode(signatureInfo.getRenderingMode());
                // 摘要算法
                ExternalDigest digest = new BouncyCastleDigest();
                // 签名算法
                ExternalSignature signature = new PrivateKeySignature(signatureInfo.getPk(), signatureInfo.getDigestAlgorithm(), null);
                // 调用itext签名方法完成pdf签章
                MakeSignature.signDetached(appearance, digest, signature, signatureInfo.getChain(), null, null, null, 0, signatureInfo.getSubfilter());
                //定义输入流为生成的输出流内容，以完成多次签章的过程
                inputStream = new ByteArrayInputStream(tempArrayOutputStream.toByteArray());
                result = tempArrayOutputStream;
            }
            outputStream = new FileOutputStream(new File(target));
            outputStream.write(result.toByteArray());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(null!=outputStream){
                    outputStream.close();
                }
                if(null!=inputStream){
                    inputStream.close();
                }
                if(null!=result){
                    result.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
