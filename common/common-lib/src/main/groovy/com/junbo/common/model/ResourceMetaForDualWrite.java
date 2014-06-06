/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.junbo.common.cloudant.json.annotations.CloudantProperty;
import com.junbo.common.jackson.deserializer.IntFromStringDeserializer;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * The base class for all resource with system properties.
 */
public abstract class ResourceMetaForDualWrite<K> extends ResourceMetaBase<K> {

    @ApiModelProperty(position = 1000, required = true,
            value = "[Client Immutable] The revision of the resource. Used for optimistic locking.")
    @JsonProperty("rev")
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = IntFromStringDeserializer.class)
    private Integer resourceAge;

    @JsonIgnore
    @CloudantProperty("_rev")
    private String cloudantRev;

    public Integer getResourceAge() {
        return resourceAge;
    }

    public void setResourceAge(Integer resourceAge) {
        this.resourceAge = resourceAge;
    }

    public String getCloudantRev() {
        return cloudantRev;
    }

    public void setCloudantRev(String cloudantRev) {
        this.cloudantRev = cloudantRev;
    }

}
