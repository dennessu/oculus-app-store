/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo;

import com.junbo.email.db.mapper.EmailMapper;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by Wei on 4/15/14.
 */
public abstract class EmailBaseRepository {
    @Autowired
    protected EmailMapper emailMapper;

    @Autowired
    @Qualifier("oculus48IdGenerator")
    protected IdGenerator idGenerator;
}
