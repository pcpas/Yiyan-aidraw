package com.buaa.aidraw.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class OpenAIService {

    @Value("${openai.url}")
    private String url;
    @Value("${openai.api}")
    private String api;
    @Value("${openai.model}")
    private String model;
    private final String text = "请你用中文描述这幅图，长度在30字以内。";
    private final Integer maxTokens = 300;

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();


    public String getImagePrompt(String imageUrl) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
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
        return rootNode
                .path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
    }
}
