package com.buaa.aidraw.config;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.auth.*;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;


@Configuration
@Component
public class OSSConfig {
    @Value("${aliyun.cloudName}")
    private String cloudName;
    @Value("${aliyun.endpoint}")
    private String endpoint;
    @Value("${aliyun.bucketName}")
    private String bucketName;
    @Value("${aliyun.accessKeyId}")
    private static String accessKeyId;
    @Value("${aliyun.accessKeySecret}")
    private static String accessKeySecret;

    private volatile static OSSClientBuilder ossClientBuilder;

    public static OSSClientBuilder initOSSClientBuilder() {
        if (ossClientBuilder == null) {
            synchronized (OSSConfig.class) {
                if (ossClientBuilder == null) {
                    ossClientBuilder = new OSSClientBuilder();
                }
            }
        }
        return ossClientBuilder;
    }

    public String upload(MultipartFile file, String type, String name) throws IOException {
        OSS ossClient = initOSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        File convFile = convertMultipartFileToFile(file);
        try {
            int lastDotIndex = name.lastIndexOf('.');
            if (lastDotIndex != -1) {
                String beforeDot = name.substring(0, lastDotIndex);
                String afterDot = name.substring(lastDotIndex);
                String uuid = UUID.randomUUID().toString();
                name = beforeDot + "_" + uuid + afterDot;
            }
            String objectName = type + "/" + name;
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, convFile);
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            if (result != null) {
                return cloudName + objectName;
            } else {
                return null;
            }
        } finally {
            // 删除临时文件
            if (convFile.exists()) {
                convFile.delete();
            }
        }
    }

    public String copy(String sourceType, String type, String sourceKey)  {
        OSS ossClient = initOSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        int lastDotIndex = sourceKey.lastIndexOf('/');
        String afterDot = sourceKey.substring(lastDotIndex + 1);
        String before = sourceType + "/" + afterDot;
        String objectName = type + "/" + afterDot;
        CopyObjectResult result = ossClient.copyObject(bucketName, before, bucketName, afterDot);
        return cloudName + objectName;
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public OSSConfig() throws ClientException {
    }
}
