/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.model.options;


import javax.ws.rs.QueryParam;
/**
 * Created by kevingu on 11/28/13.
 */
public class PostOptions {

    @QueryParam("body")
    private Boolean body;

    @QueryParam("expand")
    private String expand;

    public Boolean getBody() {
        return body;
    }

    public void setBody(Boolean body) {
        this.body = body;
    }

    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }
}
