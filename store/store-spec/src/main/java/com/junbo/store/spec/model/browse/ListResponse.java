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

    private List<Item> items;
    private PageMeta next;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public PageMeta getNext() {
        return next;
    }

    public void setNext(PageMeta next) {
        this.next = next;
    }
}
