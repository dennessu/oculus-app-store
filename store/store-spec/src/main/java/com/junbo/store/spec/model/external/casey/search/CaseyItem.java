/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.casey.search;

import com.junbo.catalog.spec.model.common.Images;
import com.junbo.catalog.spec.model.common.RevisionNotes;
import com.junbo.catalog.spec.model.item.Binary;
import com.junbo.catalog.spec.model.item.SupportedLocale;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.ItemRevisionId;
import com.junbo.common.id.OrganizationId;

import java.util.List;
import java.util.Map;

/**
 * The Item class.
 */
public class CaseyItem {

    private ItemId self;
    private String type;
    private OrganizationId developer;
    private String packageName;
    private Map<String, Binary> binaries;
    private Map<String, SupportedLocale> supportedLocales;
    private List<CatalogAttribute> genres;
    private RevisionNotes releaseNotes;
    private CaseyRating qualityRating;
    private CaseyRating comfortRating;
    private String name;
    private String longDescription;
    private String supportEmail;
    private String website;
    private String communityForumLink;
    private Images images;
    private ItemRevisionId currentRevision;

    public ItemId getSelf() {
        return self;
    }

    public void setSelf(ItemId self) {
        this.self = self;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OrganizationId getDeveloper() {
        return developer;
    }

    public void setDeveloper(OrganizationId developer) {
        this.developer = developer;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Map<String, Binary> getBinaries() {
        return binaries;
    }

    public void setBinaries(Map<String, Binary> binaries) {
        this.binaries = binaries;
    }

    public Map<String, SupportedLocale> getSupportedLocales() {
        return supportedLocales;
    }

    public void setSupportedLocales(Map<String, SupportedLocale> supportedLocales) {
        this.supportedLocales = supportedLocales;
    }

    public List<CatalogAttribute> getGenres() {
        return genres;
    }

    public void setGenres(List<CatalogAttribute> genres) {
        this.genres = genres;
    }

    public RevisionNotes getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(RevisionNotes releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public CaseyRating getQualityRating() {
        return qualityRating;
    }

    public void setQualityRating(CaseyRating qualityRating) {
        this.qualityRating = qualityRating;
    }

    public CaseyRating getComfortRating() {
        return comfortRating;
    }

    public void setComfortRating(CaseyRating comfortRating) {
        this.comfortRating = comfortRating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
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

    public ItemRevisionId getCurrentRevision() {
        return currentRevision;
    }

    public void setCurrentRevision(ItemRevisionId currentRevision) {
        this.currentRevision = currentRevision;
    }
}
