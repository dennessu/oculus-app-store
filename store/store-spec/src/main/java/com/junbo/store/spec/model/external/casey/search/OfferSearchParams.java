/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.casey.search;

import com.junbo.store.spec.model.external.casey.CaseySearchParams;

import javax.ws.rs.QueryParam;

/**
 * The OfferSearchParams class.
 */
public class OfferSearchParams extends CaseySearchParams {

    @QueryParam("country")
    private String country;

    @QueryParam("category")
    private String category;

    @QueryParam("platform")
    private String platform;

    @QueryParam("sortBy")
    private String sortBy;

    @QueryParam("offerId")
    private String offerId;

    @QueryParam("expand")
    private String expand;

    @QueryParam("cmsPage")
    private String cmsPage;

    @QueryParam("cmsSlot")
    private String cmsSlot;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }

    public String getCmsSlot() {
        return cmsSlot;
    }

    public void setCmsSlot(String cmsSlot) {
        this.cmsSlot = cmsSlot;
    }

    public String getCmsPage() {
        return cmsPage;
    }

    public void setCmsPage(String cmsPage) {
        this.cmsPage = cmsPage;
    }
}
