/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.junbo.common.enumid.PITypeId;
import com.junbo.common.model.ResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by haomin on 14-4-25.
 */
public class PIType extends ResourceMeta implements Identifiable<PITypeId> {
    @ApiModelProperty(position = 1, required = true, value = "")
    private PITypeId id;

    public PITypeId getId() {
        return id;
    }

    public void setId(PITypeId id) {
        this.id = id;
    }
}
