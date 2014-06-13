/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.spec.fusion;

import java.util.List;

/**
 * Item.
 */
public class Item {
    private String itemId;
    private String sku;
    private List<EntitlementMeta> entitlementMetas;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public List<EntitlementMeta> getEntitlementMetas() {
        return entitlementMetas;
    }

    public void setEntitlementMetas(List<EntitlementMeta> entitlementMetas) {
        this.entitlementMetas = entitlementMetas;
    }
}
