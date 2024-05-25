package com.buaa.aidraw.service;

import com.buaa.aidraw.exception.BaseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class BaiduAIService {

    @Value("${baidu.api}")
    private String API_KEY;
    @Value("${baidu.secret}")
    private String SECRET_KEY;

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    public String generateImage(String prompt,Integer width, Integer height, String style) {
        try {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"text\":\""+prompt
                    +"\",\"resolution\":"+"\""+height+"*"+width+"\""
                    +",\"style\":"+"\""+style+"\"}");
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rpc/2.0/ernievilg/v1/txt2img?access_token=" + getAccessToken())
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();
            Response response = HTTP_CLIENT.newCall(request).execute();
            String res = response.body().string();
            System.out.println(res);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(res);
            return rootNode.path("data").path("taskId").asText();
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException("提交生成图片任务失败!");
        }
    }

    public String getImage(String taskId) {
        try{
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"taskId\":"+taskId+"}");
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rpc/2.0/ernievilg/v1/getImg?access_token=" + getAccessToken())
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();
            Response response = HTTP_CLIENT.newCall(request).execute();
            return response.body().string();
        }
        catch (Exception e){
            e.printStackTrace();
            throw new BaseException("获取图片进度失败！");
        }
    }

    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    private String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new JSONObject(response.body().string()).getString("access_token");
    }


}
