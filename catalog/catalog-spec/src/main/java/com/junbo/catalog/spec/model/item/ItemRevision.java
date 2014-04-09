/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseRevisionModel;
import com.junbo.common.jackson.annotation.OfferId;

import java.util.List;
import java.util.Map;

/**
 * Item revision.
 */
public class ItemRevision extends BaseRevisionModel {
    @OfferId
    @JsonProperty("item")
    private Long itemId;
    private String type;
    private String sku;
    private String artwork;
    private List<String> images;
    private List<String> videos;
    private String supportEmail;
    private String manualDocument;
    private String communityForumLink;
    private String website;
    private Long ratingId;
    private Map<String, String> displayName;
    private Map<String, String> revisionNotes;
    private Map<String, String> shortDescription;
    private Map<String, String> longDescription;
    private Map<String, Object> digitalProperties;
    private Map<String, Object> ewalletProperties;
    private Map<String, Object> physicalProperties;
    private Map<String, Object> futureProperties;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getArtwork() {
        return artwork;
    }

    public void setArtwork(String artwork) {
        this.artwork = artwork;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getVideos() {
        return videos;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    public String getManualDocument() {
        return manualDocument;
    }

    public void setManualDocument(String manualDocument) {
        this.manualDocument = manualDocument;
    }

    public String getCommunityForumLink() {
        return communityForumLink;
    }

    public void setCommunityForumLink(String communityForumLink) {
        this.communityForumLink = communityForumLink;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Map<String, String> getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Map<String, String> displayName) {
        this.displayName = displayName;
    }

    public Map<String, String> getRevisionNotes() {
        return revisionNotes;
    }

    public void setRevisionNotes(Map<String, String> revisionNotes) {
        this.revisionNotes = revisionNotes;
    }

    public Map<String, String> getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(Map<String, String> shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Map<String, String> getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(Map<String, String> longDescription) {
        this.longDescription = longDescription;
    }

    public Map<String, Object> getDigitalProperties() {
        return digitalProperties;
    }

    public void setDigitalProperties(Map<String, Object> digitalProperties) {
        this.digitalProperties = digitalProperties;
    }

    public Map<String, Object> getEwalletProperties() {
        return ewalletProperties;
    }

    public void setEwalletProperties(Map<String, Object> ewalletProperties) {
        this.ewalletProperties = ewalletProperties;
    }

    public Map<String, Object> getPhysicalProperties() {
        return physicalProperties;
    }

    public void setPhysicalProperties(Map<String, Object> physicalProperties) {
        this.physicalProperties = physicalProperties;
    }

    public Map<String, Object> getFutureProperties() {
        return futureProperties;
    }

    public void setFutureProperties(Map<String, Object> futureProperties) {
        this.futureProperties = futureProperties;
    }

    @Override
    @JsonIgnore
    public Long getEntityId() {
        return itemId;
    }
}
