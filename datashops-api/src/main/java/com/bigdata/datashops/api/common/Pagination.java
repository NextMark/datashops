package com.bigdata.datashops.api.common;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.Data;

/**
 * Created by qinshiwei on 2017/9/5.
 */
@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Pagination {
    private long total;
    private long pageSize;
    private long pageNum;
    private long totalPages;
    private List<?> content;

    public Pagination(long total, long pageNum, long pageSize, List<?> contentList) {
        this.total = total;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.content = contentList;
        this.totalPages = (int) (total / pageSize);
    }

    public Pagination(IPage<?> iPage) {
        this.total = iPage.getTotal();
        this.pageSize = iPage.getSize();
        this.pageNum = iPage.getCurrent();
        this.content = iPage.getRecords();
        this.totalPages = iPage.getPages();
    }
}
