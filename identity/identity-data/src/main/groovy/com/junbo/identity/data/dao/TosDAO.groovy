/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.TosEntity
import com.junbo.identity.spec.v1.option.list.TosListOptions
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/9/14.
 */
@CompileStatic
interface TosDAO {
    TosEntity get(Long tosId)
    TosEntity create(TosEntity tos)
    void delete(Long tosId)
    List<TosEntity> search(TosListOptions options)
}
