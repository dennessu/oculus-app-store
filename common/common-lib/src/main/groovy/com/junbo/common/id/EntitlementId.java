/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Java doc.
 */
@IdResourcePath(value = "/entitlements/{0}", regex = "/entitlements/(?<id>[0-9A-Z]+)")
public class EntitlementId extends Id {
    public EntitlementId() {
    }

    public EntitlementId(long value) {
        super(value);
    }
}
