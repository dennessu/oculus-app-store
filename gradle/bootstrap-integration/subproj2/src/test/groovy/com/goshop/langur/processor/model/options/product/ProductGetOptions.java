package com.goshop.langur.processor.model.options.product;

import com.goshop.langur.processor.model.options.GetOptions;

import javax.ws.rs.QueryParam;

public class ProductGetOptions extends GetOptions {
    @QueryParam("name")
    private String name;

    @QueryParam("storeId")
    private String storeId;

    @QueryParam("categoryId")
    private String categoryId;

    @QueryParam("productId")
    private String productId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
