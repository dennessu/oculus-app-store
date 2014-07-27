/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.mapper;

import com.junbo.token.spec.internal.TokenOrder;
import com.junbo.token.spec.internal.TokenSet;

/**
 * order wrapper for token set and order.
 */
public class OrderWrapper {
    private TokenSet tokenSet;
    private TokenOrder tokenOrder;

    public OrderWrapper(TokenSet set, TokenOrder order){
        this.tokenSet = set;
        this.tokenOrder = order;
    }
    public TokenOrder getTokenOrder() {
        return tokenOrder;
    }

    public void setTokenOrder(TokenOrder tokenOrder) {
        this.tokenOrder = tokenOrder;
    }

    public TokenSet getTokenSet() {
        return tokenSet;
    }

    public void setTokenSet(TokenSet tokenSet) {
        this.tokenSet = tokenSet;
    }
}
