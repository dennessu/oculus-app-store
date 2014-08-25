/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse.document;

import com.junbo.common.id.ItemId;
import com.junbo.store.spec.model.browse.ReviewsResponse;

import java.util.List;

/**
 * The Item class.
 */
public class Item {

    private ItemId self;

    private String itemType; // APP

    private String title;

    private String descriptionHtml;

    private AggregatedRatings aggregatedRatings;

    private String creator;

    private List<Image> images;

    private AppDetails appDetails;

    private String detailsUrl;

    private ReviewsResponse reviews;

    private String shareUrl;

    private List<Offer> offers;

    private Boolean ownedByCurrentUser;

    private Review currentUserReview; // only appears in getLibrary response, or in getDetails response.

    private String deliveryUrl;

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

    public AggregatedRatings getAggregatedRatings() {
        return aggregatedRatings;
    }

    public void setAggregatedRatings(AggregatedRatings aggregatedRatings) {
        this.aggregatedRatings = aggregatedRatings;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public AppDetails getAppDetails() {
        return appDetails;
    }

    public void setAppDetails(AppDetails appDetails) {
        this.appDetails = appDetails;
    }

    public Review getCurrentUserReview() {
        return currentUserReview;
    }

    public void setCurrentUserReview(Review currentUserReview) {
        this.currentUserReview = currentUserReview;
    }

    public String getDetailsUrl() {
        return detailsUrl;
    }

    public void setDetailsUrl(String detailsUrl) {
        this.detailsUrl = detailsUrl;
    }

    public ReviewsResponse getReviews() {
        return reviews;
    }

    public void setReviews(ReviewsResponse reviews) {
        this.reviews = reviews;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public Boolean getOwnedByCurrentUser() {
        return ownedByCurrentUser;
    }

    public void setOwnedByCurrentUser(Boolean ownedByCurrentUser) {
        this.ownedByCurrentUser = ownedByCurrentUser;
    }

    public String getDeliveryUrl() {
        return deliveryUrl;
    }

    public void setDeliveryUrl(String deliveryUrl) {
        this.deliveryUrl = deliveryUrl;
    }
}
