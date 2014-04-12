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
    public static final String DRAFT = "DRAFT";
    public static final String DELETED = "Deleted";

    //Entity(Item/Offer/Promotion) Status
    public static final String PUBLISHED = "PUBLISHED";
    public static final String UNPUBLISHED = "UNPUBLISHED";

    //Revision(ItemRevision/OfferRevision/PromotionRevision) Status
    public static final String PENDING_REVIEW = "PENDING_REVIEW";
    public static final String RELEASED = "RELEASED";
    public static final String REJECTED = "REJECTED";


    private Status(){ }
}
