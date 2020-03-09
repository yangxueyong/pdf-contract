package com.xyy.pdf.contract.entity;

import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.security.MakeSignature;
import lombok.Data;

import java.security.PrivateKey;
import java.security.cert.Certificate;


@Data
public class SignatureInfo {
    private String reason; //理由
    private String location;//位置
    private String digestAlgorithm;//摘要类型
    private String imagePath;//图章路径
    private String fieldName;//表单域名称
    private Certificate[] chain;//证书链
    private PrivateKey pk;//私钥
    private int certificationLevel = 0; //批准签章
    private PdfSignatureAppearance.RenderingMode renderingMode;//表现形式：仅描述，仅图片，图片和描述，签章者和描述
    private MakeSignature.CryptoStandard subfilter;//支持标准，CMS,CADES
}