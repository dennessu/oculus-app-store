/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.TosId;
import com.junbo.common.util.Identifiable;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/3/14.
 */
public class Tos extends ResourceMeta implements Identifiable<TosId> {

    @ApiModelProperty(position = 1, required = true, value = "The id of the tos resource.")
    @JsonProperty("self")
    private TosId id;

    @ApiModelProperty(position = 2, required = true, value = "The locale of the tos resource.")
    private String locale;

    @ApiModelProperty(position = 3, required = true, value = "The title of the tos resource.")
    private String title;

    @ApiModelProperty(position = 4, required = true, value = "The content of the tos resource.")
    private String content;

    @Override
    public TosId getId() {
        return id;
    }

    public void setId(TosId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
        support.setPropertyAssigned("locale");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        support.setPropertyAssigned("title");
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        support.setPropertyAssigned("content");
    }
}
