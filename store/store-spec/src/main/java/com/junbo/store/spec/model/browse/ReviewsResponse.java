/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.common.id.ItemId;
import com.junbo.store.spec.model.browse.document.Review;

import java.util.List;

/**
 * The ReviewsResponse class.
 */
public class ReviewsResponse {

    /**
     * Java doc.
     */
    public static class NextOption extends PageMeta {

        private ItemId itemId;

        public ItemId getItemId() {
            return itemId;
        }

        public void setItemId(ItemId itemId) {
            this.itemId = itemId;
        }
    }

    private List<Review> reviews;

    private NextOption next;

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public NextOption getNext() {
        return next;
    }

    public void setNext(NextOption next) {
        this.next = next;
    }
}
