/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.spec.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.fulfilment.spec.fusion.LinkedEntry;

import java.util.List;
import java.util.Map;

/**
 * FulfilmentAction.
 */
@JsonAutoDetect(
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class FulfilmentAction {
    private Long actionId;

    @JsonProperty
    private String type;

    @JsonProperty
    private String status;

    @JsonProperty
    private FulfilmentResult result;

    private Long fulfilmentId;

    private Map<String, Object> properties;

    private String itemId;

    private List<LinkedEntry> items;

    private Integer copyCount;

    private Long timestamp;

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public FulfilmentResult getResult() {
        return result;
    }

    public void setResult(FulfilmentResult result) {
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

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public List<LinkedEntry> getItems() {
        return items;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
