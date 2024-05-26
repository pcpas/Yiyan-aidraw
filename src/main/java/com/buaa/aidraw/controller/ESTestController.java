package com.buaa.aidraw.controller;

import com.buaa.aidraw.exception.BaseException;
import com.buaa.aidraw.mapper.ElementMapper;
import com.buaa.aidraw.model.domain.Element;
import com.buaa.aidraw.model.request.SearchRequest;
import com.buaa.aidraw.model.request.StringRequest;
import com.buaa.aidraw.service.ElasticSearchService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/es")
public class ESTestController {
    @Resource
    ElasticSearchService searchService;
    @Resource
    ElementMapper elementMapper;

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

    @PostMapping("/insert")
    public ResponseEntity<String> insertElement(@RequestBody StringRequest request) throws IOException {
        Element element = elementMapper.getElementById(request.getValue());
        searchService.insertElement(element);
        return ResponseEntity.ok("成功");
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteElement(@RequestBody StringRequest request) throws IOException {
        Element element = elementMapper.getElementById(request.getValue());
        searchService.deleteElement(element.getId());
        return ResponseEntity.ok("成功");
    }

    @PostMapping("/search")
    public ResponseEntity<List<Element>> searchElement(@RequestBody SearchRequest request) throws IOException {
        List<Element> elements = searchService.searchElement(request.getKeyword(), request.getPageNo(), request.getNumInPage());
        return ResponseEntity.ok(elements);
    }
}
