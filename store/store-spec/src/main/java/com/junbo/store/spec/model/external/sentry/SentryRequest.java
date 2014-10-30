/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sentry;

/**
 * Created by liangfu on 10/23/14.
 */
public class SentryRequest {
    private String category;
    private String targetId;
    private String textJson;
    private String otherJson;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTextJson() {
        return textJson;
    }

    public void setTextJson(String textJson) {
        this.textJson = textJson;
    }

    public String getOtherJson() {
        return otherJson;
    }

    public void setOtherJson(String otherJson) {
        this.otherJson = otherJson;
    }
}

