/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.logappender;

/**
 * Created by liangfu on 6/25/14.
 */
public class NewRelicEvent {
    private String apiName;
    private String apiDuration;

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiDuration() {
        return apiDuration;
    }

    public void setApiDuration(String apiDuration) {
        this.apiDuration = apiDuration;
    }
}
