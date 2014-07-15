/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding;

import com.junbo.common.id.Id;

/**
 * Created by xiali_000 on 3/10/14.
 */
public interface IdGeneratorFacade {
    <T extends Id> long nextId(Class<T> cls);
    <T extends Id> long nextId(Class<T> cls, long id);
    }
