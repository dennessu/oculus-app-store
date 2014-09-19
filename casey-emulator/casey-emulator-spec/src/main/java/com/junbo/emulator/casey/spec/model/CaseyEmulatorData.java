/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.emulator.casey.spec.model;

import com.junbo.common.id.OfferId;
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating;
import com.junbo.store.spec.model.external.casey.CaseyReview;
import com.junbo.store.spec.model.external.casey.cms.CmsCampaign;
import com.junbo.store.spec.model.external.casey.cms.CmsPage;

import java.util.List;
import java.util.Map;

/**
 * The CaseyEmulateData class.
 */
public class CaseyEmulatorData {

    private List<CaseyAggregateRating> caseyAggregateRatings;

    private List<CaseyReview> caseyReviews;

    private List<CmsPage> cmsPages;

    private List<CmsCampaign> cmsCampaigns;

    private Map<String, List<OfferId>> cmsPageOffers; // ($pageName + "-" + $slotName) -> list of OfferId

    public List<CaseyAggregateRating> getCaseyAggregateRatings() {
        return caseyAggregateRatings;
    }

    public void setCaseyAggregateRatings(List<CaseyAggregateRating> caseyAggregateRatings) {
        this.caseyAggregateRatings = caseyAggregateRatings;
    }

    public List<CaseyReview> getCaseyReviews() {
        return caseyReviews;
    }

    public void setCaseyReviews(List<CaseyReview> caseyReviews) {
        this.caseyReviews = caseyReviews;
    }

    public List<CmsPage> getCmsPages() {
        return cmsPages;
    }

    public void setCmsPages(List<CmsPage> cmsPages) {
        this.cmsPages = cmsPages;
    }

    public Map<String, List<OfferId>> getCmsPageOffers() {
        return cmsPageOffers;
    }

    public void setCmsPageOffers(Map<String, List<OfferId>> cmsPageOffers) {
        this.cmsPageOffers = cmsPageOffers;
    }

    public List<CmsCampaign> getCmsCampaigns() {
        return cmsCampaigns;
    }

    public void setCmsCampaigns(List<CmsCampaign> cmsCampaigns) {
        this.cmsCampaigns = cmsCampaigns;
    }
}
