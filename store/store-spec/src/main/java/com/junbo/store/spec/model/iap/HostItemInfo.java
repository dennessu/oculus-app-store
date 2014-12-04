/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.iap;

import com.junbo.common.id.ItemId;

/**
 * Created by fzhang on 12/4/2014.
 */
public class HostItemInfo {

    private String packageName;

    private ItemId hostItemId;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public ItemId getHostItemId() {
        return hostItemId;
    }

    public void setHostItemId(ItemId hostItemId) {
        this.hostItemId = hostItemId;
    }
}
