/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.spec.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.junbo.cart.common.validate.Group;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by fzhang@wan-san.com on 14-2-18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Coupon {

    public Coupon() {
    }

    public Coupon(String id) {
        this.id = id;
    }

    @NotNull(groups = {Group.CartItem.class})
    @Size(min = 1, groups = {Group.CartItem.class})
    private String id;

    private String href;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
