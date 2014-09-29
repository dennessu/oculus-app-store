/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Promotion revision id.
 */
@IdResourcePath(value = "/promotion-revisions/{0}",
                resourceType = "promotion-revisions",
                regex = "/promotion-revisions/(?<id>[0-9A-Za-z]+)")
public class PromotionRevisionId extends CloudantId {
    public PromotionRevisionId(){

    }

    public PromotionRevisionId(String value) {
        super(value);
    }
}
