package com.goshop.catalog.spec.model.options.category;

import com.goshop.catalog.spec.model.options.GetOptions;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(String parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
