/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.facebook;

import javax.ws.rs.NotSupportedException;

/**
 * Facebook Payment Action Type.
 */
public enum FacebookPaymentActionType {
    authorize("authorize"),
    capture("capture"),
    charge("charge"),
    settle("settle"),
    cancel("void"),
    refund("refund");

    private final String name;

    FacebookPaymentActionType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(Short id) {
        throw new NotSupportedException("enum FacebookPaymentActionType not settable");
    }

    @Override
    public String toString(){
        return this.name;
    }
}
