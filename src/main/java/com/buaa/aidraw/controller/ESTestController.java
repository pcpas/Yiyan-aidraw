package com.buaa.aidraw.controller;

import com.buaa.aidraw.exception.BaseException;
import com.buaa.aidraw.service.ElasticSearchService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/es")
public class ESTestController {
    @Resource
    ElasticSearchService searchService;

    @PostMapping("/creat")
    public ResponseEntity<String> creatIndex(@RequestBody String indexName) throws IOException {
        switch (indexName){
            case "element":
                searchService.CreateIndex(indexName);
                break;
            default:
                throw new BaseException("未知ES index 名称");
        }
        return ResponseEntity.ok("成功");
    }

    @GetMapping("/get")
    public ResponseEntity<String> getIndexProperties(@RequestBody String indexName) throws IOException {
        switch (indexName){
            case "element":
                searchService.getIndexProperties(indexName);
                break;
            default:
                throw new BaseException("未知ES index 名称");
        }
        return ResponseEntity.ok("成功");
    }

}
