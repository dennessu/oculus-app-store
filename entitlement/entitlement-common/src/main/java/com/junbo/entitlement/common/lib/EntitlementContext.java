/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.common.lib;

import java.util.Date;

/**
 * A wrapper of ThreadLocal to save common values.
 */
public class EntitlementContext {
    private Date now;
    private String requestorId;

    private static ThreadLocal<EntitlementContext> current = new ThreadLocal<EntitlementContext>();

    public static EntitlementContext current() {
        EntitlementContext context = current.get();
        if (context == null) {
            current.set(new EntitlementContext());
        }
        return current.get();
    }

    public Date getNow() {
        return now == null ? new Date() : now;
    }

    public void setNow(Date now) {
        this.now = now;
    }

    public String getRequestorId() {
        return requestorId;
    }

    public void setRequestorId(String requestorId) {
        this.requestorId = requestorId;
    }
}
