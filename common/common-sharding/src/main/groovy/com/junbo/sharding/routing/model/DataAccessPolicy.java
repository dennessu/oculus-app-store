/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.routing.model;

/**
 * The policy for data access.
 */
public enum DataAccessPolicy {
    CLOUDANT_ONLY,
    CLOUDANT_FIRST,
    SQL_FIRST,
    SQL_ONLY
}
