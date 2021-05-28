package com.bigdata.datashops.api.common;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.Data;

/**
 * Created by qinshiwei on 2017/9/5.
 */
@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Pagination {
    private long total;
    private int pageSize;
    private int pageNum;
    private int totalPages;
    private List<?> content;

    public Pagination(long total, int pageNum, int pageSize, List<?> contentList) {
        this.total = total;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.content = contentList;
        this.totalPages = (int) (total / pageSize);
    }

    public Pagination(Page page) {
        this.total = page.getTotalElements();
        this.pageSize = page.getSize();
        this.pageNum = page.getNumber() + 1;
        this.totalPages = page.getTotalPages();
        this.content = page.getContent();
    }
}
