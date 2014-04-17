/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import java.util.List;

/**
 * Locale properties for item revision.
 */
public class ItemRevisionLocaleProperties {
    String name;
    String gameModes;
    String revisionNotes;
    String longDescription;
    String shortDescription;
    String minimumSystemRequirements;
    String recommendedSystemRequirements;
    String legalInformation;
    List<String> supportedInputDevices;
    List<String> platforms;
    String credits;
    String copyright;
    String knownBugs;
    String color;
    String size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGameModes() {
        return gameModes;
    }

    public void setGameModes(String gameModes) {
        this.gameModes = gameModes;
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

    public List<String> getSupportedInputDevices() {
        return supportedInputDevices;
    }

    public void setSupportedInputDevices(List<String> supportedInputDevices) {
        this.supportedInputDevices = supportedInputDevices;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
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
