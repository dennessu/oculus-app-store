/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.wordnik.swagger.annotations.ApiModelProperty;

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
