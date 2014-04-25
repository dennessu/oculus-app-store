/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.junbo.catalog.spec.model.common.Images;
import com.junbo.catalog.spec.model.common.Video;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Locale properties for item revision.
 */
public class ItemRevisionLocaleProperties {
    @ApiModelProperty(position = 1, required = true, value = "item revision name")
    String name;
    @ApiModelProperty(position = 3, required = true, value = "revision notes")
    String revisionNotes;
    @ApiModelProperty(position = 4, required = true, value = "long description")
    String longDescription;
    @ApiModelProperty(position = 5, required = true, value = "short description")
    String shortDescription;
    @ApiModelProperty(position = 6, required = true, value = "minimum system requirements")
    String minimumSystemRequirements;
    @ApiModelProperty(position = 7, required = true, value = "recommended system requirements")
    String recommendedSystemRequirements;
    @ApiModelProperty(position = 8, required = true, value = "legal information")
    String legalInformation;
    @ApiModelProperty(position = 11, required = true, value = "credits")
    String credits;
    @ApiModelProperty(position = 12, required = true, value = "copyright")
    String copyright;
    @ApiModelProperty(position = 13, required = true, value = "known bugs")
    String knownBugs;
    @ApiModelProperty(position = 27, required = false, value = "Support email")
    private String supportEmail;
    @ApiModelProperty(position = 31, required = false, value = "Manual document for the item revision resource")
    private String manualDocument;
    @ApiModelProperty(position = 32, required = false, value = "Community forum link of the item revision resource")
    private String communityForumLink;
    @ApiModelProperty(position = 29, required = false, value = "Images to describe the item revision resource")
    private Images images;
    @ApiModelProperty(position = 30, required = false, value = "Videos to describe the item revision resource")
    private List<Video> videos;
    @ApiModelProperty(position = 14, required = true, value = "color")
    String color;
    @ApiModelProperty(position = 15, required = true, value = "size")
    String size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRevisionNotes() {
        return revisionNotes;
    }

    public void setRevisionNotes(String revisionNotes) {
        this.revisionNotes = revisionNotes;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getMinimumSystemRequirements() {
        return minimumSystemRequirements;
    }

    public void setMinimumSystemRequirements(String minimumSystemRequirements) {
        this.minimumSystemRequirements = minimumSystemRequirements;
    }

    public String getRecommendedSystemRequirements() {
        return recommendedSystemRequirements;
    }

    public void setRecommendedSystemRequirements(String recommendedSystemRequirements) {
        this.recommendedSystemRequirements = recommendedSystemRequirements;
    }

    public String getLegalInformation() {
        return legalInformation;
    }

    public void setLegalInformation(String legalInformation) {
        this.legalInformation = legalInformation;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getKnownBugs() {
        return knownBugs;
    }

    public void setKnownBugs(String knownBugs) {
        this.knownBugs = knownBugs;
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

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
