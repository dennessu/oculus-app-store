/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.model.options.category;

import com.junbo.langur.processor.model.options.GetOptions;

import javax.ws.rs.QueryParam;

/**
 * Created by kevingu on 11/28/13.
 */
public class CategoryGetOptions extends GetOptions {

    @QueryParam("name")
    private String name;

    @QueryParam("type")
    private String type;

    @QueryParam("parentCategoryId")
    private String parentCategoryId;

    @QueryParam("categoryId")
    private String categoryId;

    @QueryParam("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @QueryParam("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @QueryParam("parentCategoryId")
    public String getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(String parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    @QueryParam("categoryId")
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
