/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.hibernate.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import java.util.Random;

/**
 * Created by minhao on 3/3/14.
 */
public class SchemaResolver implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        //TODO: Implement service to identify shard
        Random random = new Random();
        return "shard_" + random.nextInt(2);
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
