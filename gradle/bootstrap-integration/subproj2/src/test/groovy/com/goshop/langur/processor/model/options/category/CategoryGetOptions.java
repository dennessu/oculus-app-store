package com.goshop.langur.processor.model.options.category;

import com.goshop.langur.processor.model.options.GetOptions;

import javax.ws.rs.QueryParam;

/**
 * Created by kevingu on 11/28/13.
 */
public class CategoryGetOptions extends GetOptions {

    private String name;

    private String type;

    private String parentCategoryId;

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
