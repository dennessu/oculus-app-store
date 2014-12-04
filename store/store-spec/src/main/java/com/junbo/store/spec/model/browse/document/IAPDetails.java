/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse.document;

import com.junbo.common.id.ItemId;

/**
 * The IAPDetails class.
 */
public class IAPDetails {

    private String sku;

    private String packageName;

    private ItemId hostItem;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public ItemId getHostItem() {
        return hostItem;
    }

    public void setHostItem(ItemId hostItem) {
        this.hostItem = hostItem;
    }
}
