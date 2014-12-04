/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.iap;

import javax.ws.rs.QueryParam;
import java.util.Set;

/**
 * Created by fzhang on 12/5/2014.
 */
public class IAPGetItemsParam {

    @QueryParam("sku")
    private Set<String> skus;

    public Set<String> getSkus() {
        return skus;
    }

    public void setSkus(Set<String> skus) {
        this.skus = skus;
    }
}
