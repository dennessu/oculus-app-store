package com.goshop.langur.processor.model.product;

import com.goshop.langur.processor.model.attribute.Attribute;

import java.util.List;

public class Sku {
    private String skuId;

    private List<Attribute> attributes;
    private List<Price> prices;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }
}