/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse.document;

import java.util.List;

/**
 * The AppDetails class.
 */
public class AppDetails {

    private String category;
    private String appType;
    private String developerEmail;
    private String developerName;
    private String developerWebsite;
    private String packageName;
    private String numDownloads;
    private String title;
    private String recentChangesHtml;
    private List<AppPermission> appPermissions;
    private String uploadDate;
    private Integer versionCode;
    private String versionString;
    private List<FileMetadata> files;
    private Long installationSize;
    private Integer majorVersionNumber;
    private List<String> certificateHashList;
    private Long contentRating;
    private String contentRatingDescription;
    private Boolean supportIAP;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getNumDownloads() {
        return numDownloads;
    }

    public void setNumDownloads(String numDownloads) {
        this.numDownloads = numDownloads;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRecentChangesHtml() {
        return recentChangesHtml;
    }

    public void setRecentChangesHtml(String recentChangesHtml) {
        this.recentChangesHtml = recentChangesHtml;
    }

    public List<AppPermission> getAppPermissions() {
        return appPermissions;
    }

    public void setAppPermissions(List<AppPermission> appPermissions) {
        this.appPermissions = appPermissions;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
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

    public List<FileMetadata> getFiles() {
        return files;
    }

    public void setFiles(List<FileMetadata> files) {
        this.files = files;
    }

    public Long getInstallationSize() {
        return installationSize;
    }

    public void setInstallationSize(Long installationSize) {
        this.installationSize = installationSize;
    }

    public Integer getMajorVersionNumber() {
        return majorVersionNumber;
    }

    public void setMajorVersionNumber(Integer majorVersionNumber) {
        this.majorVersionNumber = majorVersionNumber;
    }

    public List<String> getCertificateHashList() {
        return certificateHashList;
    }

    public void setCertificateHashList(List<String> certificateHashList) {
        this.certificateHashList = certificateHashList;
    }

    public Long getContentRating() {
        return contentRating;
    }

    public void setContentRating(Long contentRating) {
        this.contentRating = contentRating;
    }

    public String getContentRatingDescription() {
        return contentRatingDescription;
    }

    public void setContentRatingDescription(String contentRatingDescription) {
        this.contentRatingDescription = contentRatingDescription;
    }

    public Boolean getSupportIAP() {
        return supportIAP;
    }

    public void setSupportIAP(Boolean supportIAP) {
        this.supportIAP = supportIAP;
    }
}
