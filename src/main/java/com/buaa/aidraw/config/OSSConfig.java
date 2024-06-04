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
    // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
    private String cloudName = "https://yiyan-aidrawing.oss-cn-beijing.aliyuncs.com/";
    String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
    // 填写Bucket名称，例如examplebucket。
    private String bucketName = "yiyan-aidrawing";
    // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
    // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
    // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。



    public String upload(MultipartFile file, String type, String name) throws IOException {
        OSS ossClient = null;
        try{
            EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
             ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);
        }catch (ClientException e){
            e.printStackTrace();
        }
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
        OSS ossClient = null;
        try{
            EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
            ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);
        }catch (ClientException e){
            e.printStackTrace();
        }
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
