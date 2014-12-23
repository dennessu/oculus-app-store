/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.common.id.ItemId;

import java.util.List;

/**
 * The InitialDownloadItemsResponse class.
 */
public class InitialDownloadItemsResponse {

    /**
     * The InitialDownloadItemEntry class.
     */
    public static class InitialDownloadItemEntry {
        private String name;
        private ItemId item;
        private String packageName;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ItemId getItem() {
            return item;
        }

        public void setItem(ItemId item) {
            this.item = item;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }
    }

    private List<InitialDownloadItemEntry> items;

    public List<InitialDownloadItemEntry> getItems() {
        return items;
    }

    public void setItems(List<InitialDownloadItemEntry> items) {
        this.items = items;
    }
}
