/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.sharding.IdGenerator
import org.springframework.beans.factory.annotation.Required

/**
 * EmailBaseRepository Class.
 */
abstract class EmailBaseRepository<T extends CloudantEntity> extends CloudantClient<T> {
    protected IdGenerator idGenerator

    @Required
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    private Long getId(Long userId) {
        if(userId != null) {
            return idGenerator.nextId(userId)
        }
        return idGenerator.nextId()
    }
}
