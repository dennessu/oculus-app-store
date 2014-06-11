/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo.impl.sql

import com.junbo.email.db.mapper.EmailMapper
import com.junbo.sharding.IdGenerator
import org.springframework.beans.factory.annotation.Required

/**
 * EmailBaseRepository Class.
 */
abstract class EmailBaseRepository {
    protected EmailMapper emailMapper

    protected IdGenerator idGenerator

    @Required
    public void setEmailMapper(EmailMapper emailMapper) {
        this.emailMapper = emailMapper
    }

    @Required
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    protected Long getId(Long userId) {
        if(userId != null) {
            return idGenerator.nextId(userId)
        }
        return idGenerator.nextId()
    }
}
