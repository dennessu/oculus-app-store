/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse.document;

import com.junbo.catalog.spec.model.item.SupportedLocale;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.ItemRevisionId;
import com.junbo.store.spec.model.browse.Images;
import com.junbo.store.spec.model.browse.ReviewsResponse;

import java.util.Map;

/**
 * The Item class.
 */
public class Item {

    private ItemId self;

    private String itemType; // APP

    private String title;

    private String descriptionHtml;

    private Double rank;

    private Map<String, AggregatedRatings> aggregatedRatings;

    private String creator;

    private Images images;

    private IAPDetails iapDetails;

    private AppDetails appDetails;

    private ReviewsResponse reviews;

    private Offer offer;

    private Integer useCount;

    private Boolean ownedByCurrentUser;

    private String payload;

    private String signature;

    private Review currentUserReview; // only appears in getLibrary response, or in getDetails response.

    private Map<String, SupportedLocale> supportedLocales;

    private ItemRevisionId currentRevision;

    public ItemId getSelf() {
        return self;
    }

    public void setSelf(ItemId self) {
        this.self = self;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescriptionHtml() {
        return descriptionHtml;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public Double getRank() {
        return rank;
    }

    public void setRank(Double rank) {
        this.rank = rank;
    }

    public Map<String, AggregatedRatings>  getAggregatedRatings() {
        return aggregatedRatings;
    }

    public void setAggregatedRatings(Map<String, AggregatedRatings>  aggregatedRatings) {
        this.aggregatedRatings = aggregatedRatings;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public IAPDetails getIapDetails() {
        return iapDetails;
    }

    public void setIapDetails(IAPDetails iapDetails) {
        this.iapDetails = iapDetails;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public AppDetails getAppDetails() {
        return appDetails;
    }

    public void setAppDetails(AppDetails appDetails) {
        this.appDetails = appDetails;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Review getCurrentUserReview() {
        return currentUserReview;
    }

    public void setCurrentUserReview(Review currentUserReview) {
        this.currentUserReview = currentUserReview;
    }

    public ReviewsResponse getReviews() {
        return reviews;
    }

    public void setReviews(ReviewsResponse reviews) {
        this.reviews = reviews;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Boolean getOwnedByCurrentUser() {
        return ownedByCurrentUser;
    }

    public void setOwnedByCurrentUser(Boolean ownedByCurrentUser) {
        this.ownedByCurrentUser = ownedByCurrentUser;
    }

    public Map<String, SupportedLocale> getSupportedLocales() {
        return supportedLocales;
    }

    public void setSupportedLocales(Map<String, SupportedLocale> supportedLocales) {
        this.supportedLocales = supportedLocales;
    }

    public ItemRevisionId getCurrentRevision() {
        return currentRevision;
    }

    public void setCurrentRevision(ItemRevisionId currentRevision) {
        this.currentRevision = currentRevision;
    }
}
