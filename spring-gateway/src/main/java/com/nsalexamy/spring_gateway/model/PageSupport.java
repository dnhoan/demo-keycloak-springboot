package com.nsalexamy.spring_gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;

import java.util.List;

@Value
@AllArgsConstructor
public class PageSupport<T> {


    private static final String FIRST_PAGE_NUM = "0";

    private static final String DEFAULT_PAGE_SIZE = "10";

    List<T> content;
    int pageNumber;
    int pageSize;
    long totalElements;

    public PageSupport(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumberOfElements();
        this.pageSize = page.getTotalPages();
        this.totalElements = page.getTotalElements();
    }

    public PageSupport(List<T> content, Page page) {
        this.content = content;
        this.pageNumber = page.getNumberOfElements();
        this.pageSize = page.getTotalPages();
        this.totalElements = page.getTotalElements();
    }

    @JsonProperty
    public long totalPages() {
        return pageSize > 0 ? (totalElements - 1) / pageSize + 1: 0;
    }

    @JsonProperty
    public boolean first() {
        return pageNumber == Integer.parseInt(FIRST_PAGE_NUM);
    }

    @JsonProperty
    public boolean last() {
        return (long) (pageNumber + 1) * pageSize >= totalElements ;
    }
}
