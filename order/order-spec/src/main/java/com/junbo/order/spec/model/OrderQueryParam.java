/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import javax.ws.rs.QueryParam;

/**
 * Created by fzhang on 4/1/2014.
 */
public class OrderQueryParam {

    @QueryParam("tentative")
    private Boolean tentative;

    public Boolean getTentative() {
        return tentative == null ? false : tentative;
    }

    public void setTentative(Boolean tentative) {
        this.tentative = tentative;
    }
}
