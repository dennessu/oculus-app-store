/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.Null;

/**
 * Base entity model.
 */
public abstract class BaseEntityModel extends BaseModel {
    // workaround fastjson de-serialize issue
    @Null
    @JsonIgnore
    @JSONField(serialize = false)
    private transient Long id;
    public abstract Long getCurrentRevisionId();
    public abstract void setCurrentRevisionId(Long currentRevisionId);
}
