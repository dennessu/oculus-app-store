/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.model.store;

import com.junbo.langur.processor.model.Model;
import com.junbo.langur.processor.model.attribute.Attribute;

import java.util.List;
/**
 * Created by kevingu on 11/21/13.
 */
public class Store extends Model {

    private String name;

    private List<Attribute> attributes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

}
