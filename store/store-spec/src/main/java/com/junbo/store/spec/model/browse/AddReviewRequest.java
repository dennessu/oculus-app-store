/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.ItemId;

import java.util.Map;

/**
 * The AddReviewRequest class.
 */
public class AddReviewRequest {

    @JsonProperty("item")
    private ItemId itemId;

    private String title;

    private String content;

    private String deviceName;

    private Map<String, Integer> starRatings;

    public ItemId getItemId() {
        return itemId;
    }

    public void setItemId(ItemId itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Map<String, Integer> getStarRatings() {
        return starRatings;
    }

    public void setStarRatings(Map<String, Integer> starRatings) {
        this.starRatings = starRatings;
    }
}
