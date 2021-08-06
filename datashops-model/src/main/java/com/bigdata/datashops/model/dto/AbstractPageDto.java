package com.bigdata.datashops.model.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public abstract class AbstractPageDto implements Serializable {

    public int pageNum = 1;
    public int pageSize = 20;
    public int limit = 20;

    public String orderField = "time";
    //public Sort.Direction orderType = Sort.Direction.DESC;

    public abstract String validate();

}
