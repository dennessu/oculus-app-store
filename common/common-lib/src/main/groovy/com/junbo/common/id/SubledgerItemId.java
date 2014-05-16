/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Java doc for SubledgerItemId.
 */
@IdResourcePath(value = "/subledger-items/{0}", regex = "/subledger-items/(?<id>[0-9A-Z]+)")
public class SubledgerItemId extends Id {

    public SubledgerItemId() {}

    public SubledgerItemId(long value) {
        super(value);
    }
}
