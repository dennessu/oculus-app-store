/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.spec.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.fulfilment.spec.fusion.LinkedEntry;

import javax.validation.constraints.Null;
import java.util.List;
import java.util.Map;

/**
 * FulfilmentAction.
 */
@JsonAutoDetect(
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class FulfilmentAction {
    @Null
    private Long actionId;

    @JsonProperty
    private String type;

    @JsonProperty
    private String status;

    @JsonProperty
    private String result;

    private Map<String, String> properties;

    private List<LinkedEntry> items;

    private Integer copyCount;

    @JsonIgnore
    private Long fulfilmentId;

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public List<LinkedEntry> getItems() {
        return items;
    }

    public void setItems(List<LinkedEntry> items) {
        this.items = items;
    }

    public Integer getCopyCount() {
        return copyCount;
    }

    public void setCopyCount(Integer copyCount) {
        this.copyCount = copyCount;
    }

    public Long getFulfilmentId() {
        return fulfilmentId;
    }

    public void setFulfilmentId(Long fulfilmentId) {
        this.fulfilmentId = fulfilmentId;
    }
}
