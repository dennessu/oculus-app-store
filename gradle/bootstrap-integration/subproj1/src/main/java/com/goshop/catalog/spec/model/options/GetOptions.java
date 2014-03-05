package com.goshop.catalog.spec.model.options;


import javax.ws.rs.QueryParam;
import java.util.Date;

public class GetOptions {

    @QueryParam("page")
    private Integer page;

    @QueryParam("size")
    private Integer size;

    @QueryParam("status")
    private String status;

    @QueryParam("revision")
    private Long revision;

    @QueryParam("fields")
    private String fields;

    @QueryParam("order")
    private String order;

    @QueryParam("expand")
    private String expand;

    @QueryParam("locale")
    private String locale;

    @QueryParam("country")
    private String country;

    @QueryParam("currency")
    private String currency;

    @QueryParam("modifiedSince")
    private Date modifiedSince;

    @QueryParam("modifiedUntil")
    private Date modifiedUntil;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRevision() { return revision;    }

    public void setRevision(Long revision) { this.revision = revision;}

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getModifiedSince() {
        return modifiedSince;
    }

    public void setModifiedSince(Date modifiedSince) {
        this.modifiedSince = modifiedSince;
    }

    public Date getModifiedUntil() {
        return modifiedUntil;
    }

    public void setModifiedUntil(Date modifiedUntil) {
        this.modifiedUntil = modifiedUntil;
    }
}
