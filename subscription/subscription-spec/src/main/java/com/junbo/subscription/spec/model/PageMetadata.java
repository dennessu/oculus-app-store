package com.junbo.subscription.spec.model;

import javax.ws.rs.QueryParam;

public class PageMetadata {
    @QueryParam("page")
    private Long number;

    @QueryParam("size")
    private Long size;

    private Long totalElements;
    private Long totalPages;

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }
}
