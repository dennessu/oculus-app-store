/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.TosId;
import com.junbo.common.util.Identifiable;
import com.junbo.identity.spec.model.users.ResourceMeta;

/**
 * Created by liangfu on 4/3/14.
 */
public class Tos extends ResourceMeta implements Identifiable<TosId> {

    @JsonProperty("self")
    private TosId id;

    // todo:    Liangfu:    Need to investigate the tos
    private String text;

    @Override
    public TosId getId() {
        return id;
    }

    public void setId(TosId id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
