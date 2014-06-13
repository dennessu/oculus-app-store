/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Item revision id.
 */
@IdResourcePath(value = "/item-revisions/{0}",
                resourceType = "item-revisions",
                regex = "/item-revisions/(?<id>[0-9A-Za-z]+)")
public class ItemRevisionId extends CloudantId {
    public ItemRevisionId(){

    }

    public ItemRevisionId(String value) {
        super(value);
    }
}
