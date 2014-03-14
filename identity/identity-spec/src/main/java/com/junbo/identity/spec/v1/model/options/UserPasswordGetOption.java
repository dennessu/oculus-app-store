/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.options;

import javax.ws.rs.QueryParam;
import java.util.Date;

/**
 * Created by liangfu on 3/14/14.
 */
public class UserPasswordGetOption extends PagingGetOption {
    @QueryParam("expiresBy")
    private Date expiresBy;

    public Date getExpiresBy() {
        return expiresBy;
    }

    public void setExpiresBy(Date expiresBy) {
        this.expiresBy = expiresBy;
    }
}
