/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Java doc.
 */
@IdResourcePath(value = "/entitlements/{0}",
                resourceType = "entitlements",
                regex = "/entitlements/(?<id>[0-9A-Za-z]+)")
public class EntitlementId extends CloudantId {
    public EntitlementId() {
    }

    public EntitlementId(String value) {
        super(value);
    }
}
