/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.model;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by minhao on 2/14/14.
 */
public class Link {

    @ApiModelProperty(position = 1, required = true,
            value = "[Readonly] The URI of the resource pointed by this link.")
    private String href;

    @ApiModelProperty(position = 2, required = false,
            value = "[Readonly] The ID of the resource.")
    private String id;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
