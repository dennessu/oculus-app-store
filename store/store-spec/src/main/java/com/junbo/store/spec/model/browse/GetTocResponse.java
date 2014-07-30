/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

/**
 * The GetTocResponse class.
 */
public class GetTocResponse {

    private String homeSectionUrl;
    private CorpusMetadata[] corpusMetadatas;
    public Boolean requiresUploadDeviceConfig;

    public String getHomeSectionUrl() {
        return homeSectionUrl;
    }

    public void setHomeSectionUrl(String homeSectionUrl) {
        this.homeSectionUrl = homeSectionUrl;
    }

    public CorpusMetadata[] getCorpusMetadatas() {
        return corpusMetadatas;
    }

    public void setCorpusMetadatas(CorpusMetadata[] corpusMetadatas) {
        this.corpusMetadatas = corpusMetadatas;
    }

    public Boolean getRequiresUploadDeviceConfig() {
        return requiresUploadDeviceConfig;
    }

    public void setRequiresUploadDeviceConfig(Boolean requiresUploadDeviceConfig) {
        this.requiresUploadDeviceConfig = requiresUploadDeviceConfig;
    }
}
