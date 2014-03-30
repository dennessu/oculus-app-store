/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

/**
 * Entity status.
 */
public final class Status {
    //Common Status
    public static final String DESIGN = "Design";
    public static final String DELETED = "Deleted";

    //Entity(Item/Offer/Promotion) Status
    public static final String PUBLISHED = "Published";
    public static final String UNPUBLISHED = "Unpublished";

    //Revision(ItemRevision/OfferRevision/PromotionRevision) Status
    public static final String PENDING_REVIEW = "PendingReview";
    public static final String RELEASED = "Released";
    public static final String REJECTED = "Rejected";


    private Status(){ }
}
