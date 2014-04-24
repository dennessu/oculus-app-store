/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by fzhang on 4/24/2014.
 */
@IdResourcePath("/coupons/{0}")
public class CouponId extends Id {
    public CouponId(){
    }

    public CouponId(Long value) {
        super(value);
    }
}
