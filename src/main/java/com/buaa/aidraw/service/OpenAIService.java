package com.buaa.aidraw.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.concurrent.TimeUnit;

@Service
public class OpenAIService {

    @Value("${openai.url}")
    private String url;
    @Value("${openai.api}")
    private String api;
    @Value("${openai.model}")
    private String model;

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient()
            .newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS) // 设置连接超时时间为 30 秒
            .readTimeout(60, TimeUnit.SECONDS)    // 设置读取超时时间为 60 秒
            .build();


    public String getImagePrompt(String imageUrl) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        String text = "请你用中文描述这幅图，长度在30字以内。";
        int maxTokens = 300;
        String bodyString = "{"
                + "\"model\": \"" + model + "\", "
                + "\"messages\": ["
                + "  {"
                + "    \"role\": \"user\", "
                + "    \"content\": ["
                + "      {\"type\": \"text\", \"text\": \"" + text + "\"}, "
                + "      {\"type\": \"image_url\", \"image_url\": {\"url\": \"" + imageUrl + "\"}}"
                + "    ]"
                + "  }"
                + "], "
                + "\"max_tokens\": " + maxTokens
                + "}";
        RequestBody body = RequestBody.create(mediaType, bodyString);
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + api)
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        assert response.body() != null;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.body().string());
        System.out.println(rootNode.path("choices"));
        return rootNode
                .path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
    }
}
