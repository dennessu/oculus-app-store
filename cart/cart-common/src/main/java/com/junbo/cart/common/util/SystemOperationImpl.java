/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.common.util;

import java.util.Date;

/**
 * Created by fzhang@wan-san.com on 14-1-23.
 */
public class SystemOperationImpl implements SystemOperation {
    @Override
    public Date currentTime() {
        return new Date();
    }
}
