package com.bigdata.datashops.model.dto;

import lombok.Data;

@Data
public class DtoPageQuery {
    private int pageSize = 20;
    private int pageNum = 1;

    private String name;

    private String owner;
}
