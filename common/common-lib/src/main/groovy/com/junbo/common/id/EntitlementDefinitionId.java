/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Java doc.
 */
@IdResourcePath(value = "/entitlement-definitions/{0}", regex = "/entitlement-definitions/(?<id>[0-9A-Z]+)")
public class EntitlementDefinitionId extends Id {

    public EntitlementDefinitionId() {}

    public EntitlementDefinitionId(long value) {
        super(value);
    }
}
