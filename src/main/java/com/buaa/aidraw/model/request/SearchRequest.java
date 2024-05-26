package com.buaa.aidraw.model.request;

import lombok.Data;

@Data
public class SearchRequest {
    String keyword;
    int pageNo;
    int numInPage;
}
