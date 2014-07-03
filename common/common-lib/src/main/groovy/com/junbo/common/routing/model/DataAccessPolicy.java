/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.routing.model;

/**
 * The policy for data access.
 */
public enum DataAccessPolicy {
    CLOUDANT_ROUTED,
    CLOUDANT_ONLY,
    SQL_FIRST,
    SQL_ONLY
}
