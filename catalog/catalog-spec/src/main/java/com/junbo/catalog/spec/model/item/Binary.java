/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.fasterxml.jackson.databind.JsonNode;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Binary.
 */
public class Binary {
    @ApiModelProperty(position = 1, required = true, value = "Where to get the binaries")
    private String href;
    @ApiModelProperty(position = 2, required = true, value = "Size in bytes")
    private Long size;
    @ApiModelProperty(position = 3, required = true, value = "Item version")
    private String version;
    @ApiModelProperty(position = 4, required = true, value = "The MD5 signature/hash of the binary - 32 lowercase hex chars")
    private String md5;
    @ApiModelProperty(position = 5, required = true, value = "Metadata, no server validations on this property.")
    private JsonNode metadata;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public JsonNode getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonNode metadata) {
        this.metadata = metadata;
    }
}
