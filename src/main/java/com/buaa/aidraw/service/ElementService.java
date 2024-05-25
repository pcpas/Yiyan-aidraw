package com.buaa.aidraw.service;

import com.buaa.aidraw.mapper.ElementMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ElementService {

    @Resource
    ElementMapper elementMapper;


}
