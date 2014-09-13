/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse.document;

import java.util.Date;
import java.util.List;

/**
 * The AppDetails class.
 */
public class AppDetails {

    private List<CategoryInfo> categories;

    private List<GenreInfo> genres;

    private Long contentRating;

    private Date releaseDate;

    private String website;

    private String forumUrl;

    private String developerEmail;
    private String developerName;
    private String developerWebsite;

    private String publisherEmail;
    private String publisherName;
    private String publisherWebsite;

    private String packageName;
    private Integer versionCode;
    private String versionString;

    private Long installationSize;

    private List<RevisionNote> revisionNotes;

    public List<CategoryInfo> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryInfo> categories) {
        this.categories = categories;
    }

    public List<GenreInfo> getGenres() {
        return genres;
    }

    public void setGenres(List<GenreInfo> genres) {
        this.genres = genres;
    }

    public Long getContentRating() {
        return contentRating;
    }

    public void setContentRating(Long contentRating) {
        this.contentRating = contentRating;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getForumUrl() {
        return forumUrl;
    }

    public void setForumUrl(String forumUrl) {
        this.forumUrl = forumUrl;
    }

    public String getDeveloperEmail() {
        return developerEmail;
    }

    public void setDeveloperEmail(String developerEmail) {
        this.developerEmail = developerEmail;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public String getDeveloperWebsite() {
        return developerWebsite;
    }

    public void setDeveloperWebsite(String developerWebsite) {
        this.developerWebsite = developerWebsite;
    }

    public String getPublisherEmail() {
        return publisherEmail;
    }

    public void setPublisherEmail(String publisherEmail) {
        this.publisherEmail = publisherEmail;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPublisherWebsite() {
        return publisherWebsite;
    }

    public void setPublisherWebsite(String publisherWebsite) {
        this.publisherWebsite = publisherWebsite;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionString() {
        return versionString;
    }

    public void setVersionString(String versionString) {
        this.versionString = versionString;
    }

    public Long getInstallationSize() {
        return installationSize;
    }

    public void setInstallationSize(Long installationSize) {
        this.installationSize = installationSize;
    }

    public List<RevisionNote> getRevisionNotes() {
        return revisionNotes;
    }

    public void setRevisionNotes(List<RevisionNote> revisionNotes) {
        this.revisionNotes = revisionNotes;
    }
}
