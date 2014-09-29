/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Java doc for SubledgerItemId.
 */
@IdResourcePath(value = "/subledger-items/{0}",
                resourceType = "subledger-items",
                regex = "/subledger-items/(?<id>[0-9A-Za-z]+)")
public class SubledgerItemId extends Id {

    public SubledgerItemId() {}

    public SubledgerItemId(Long value) {
        super(value);
    }
}
