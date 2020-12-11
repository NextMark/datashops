package com.bigdata.datashops.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.Data;

/**
 * Created by qinshiwei on 2017/9/5.
 */
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY)
@Data
public class Pagination {
    private long count;
    private int pageSize;
    private int pageNum;
    private List<?> contentList;

    public Pagination(long count, int page, int pageSize, List<?> contentList) {
        this.count = count;
        this.pageSize = pageSize;
        this.pageNum = page;
        this.contentList = contentList;
    }

}
