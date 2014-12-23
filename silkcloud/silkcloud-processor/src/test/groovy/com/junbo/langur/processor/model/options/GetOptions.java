/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.model.options;


import javax.ws.rs.QueryParam;
import java.util.Date;
/**
 * Created by kevingu on 11/28/13.
 */
public class GetOptions {

    private Integer page;

    private Integer size;

    @QueryParam("status")
    private String status;

    private Long revision;

    private String fields;

    private String order;

    private String expand;

    private String locale;

    private String country;

    private String currency;

    private Date modifiedSince;

    private Date modifiedUntil;

    @QueryParam("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @QueryParam("revision")
    public Long getRevision() { return revision;    }

    public void setRevision(Long revision) { this.revision = revision;}

    @QueryParam("page")
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    @QueryParam("size")
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @QueryParam("fields")
    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    @QueryParam("order")
    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    @QueryParam("expand")
    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }

    @QueryParam("locale")
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @QueryParam("country")
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @QueryParam("currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @QueryParam("modifiedSince")
    public Date getModifiedSince() {
        return modifiedSince;
    }

    public void setModifiedSince(Date modifiedSince) {
        this.modifiedSince = modifiedSince;
    }

    @QueryParam("modifiedUntil")
    public Date getModifiedUntil() {
        return modifiedUntil;
    }

    public void setModifiedUntil(Date modifiedUntil) {
        this.modifiedUntil = modifiedUntil;
    }
}
