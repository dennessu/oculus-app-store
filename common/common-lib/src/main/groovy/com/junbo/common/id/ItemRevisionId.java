/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Item revision id.
 */
@IdResourcePath(value = "/item-revisions/{0}", regex = "/item-revisions/(?<id>[0-9A-Z]+)")
public class ItemRevisionId extends Id {
    public ItemRevisionId(){

    }

    public ItemRevisionId(Long value) {
        super(value);
    }
}
