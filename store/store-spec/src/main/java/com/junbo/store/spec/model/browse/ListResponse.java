/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.store.spec.model.browse.document.Item;

import java.util.List;

/**
 * The ListResponse class.
 */
public class ListResponse {

    /**
     * Java doc.
     */
    public static class NextOption extends PageMeta {

        private String category;

        private String criteria;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getCriteria() {
            return criteria;
        }

        public void setCriteria(String criteria) {
            this.criteria = criteria;
        }
    }

    private List<Item> items;
    private NextOption next;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public NextOption getNext() {
        return next;
    }

    public void setNext(NextOption next) {
        this.next = next;
    }
}
