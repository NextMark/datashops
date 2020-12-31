package com.bigdata.datashops.api.common;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by qinshiwei on 2017/9/5.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Pagination {
    private long count;
    private int pageSize;
    private int pageNum;
    private int totalPages;
    private List<?> content;

    public Pagination(long count, int pageNum, int pageSize, List<?> contentList) {
        this.count = count;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.content = contentList;
        this.totalPages = (int) (count / pageSize);
    }

    public Pagination(Page page) {
        this.count = page.getTotalElements();
        this.pageSize = page.getSize();
        this.pageNum = page.getNumber() + 1;
        this.totalPages = page.getTotalPages();
        this.content = page.getContent();
    }
}
