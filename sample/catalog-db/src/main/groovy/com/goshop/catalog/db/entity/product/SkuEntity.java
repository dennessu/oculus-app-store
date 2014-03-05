package com.goshop.catalog.db.entity.product;

import com.goshop.catalog.db.entity.attribute.AttributeEntity;

import java.util.List;

/**
 * When user wants to buy one SkuEntity.
 * 1):  Billing will record which product, version and SKU the user is buying to avoid potential risk;
 * 2):  In billing history page, billing should show user's buying information;
 * 3):  SKU can't be deleted or updated. If user wants to disable this SKU, he can set the SKU's inventory to zero.
 */
public class SkuEntity {
    // Id is just for tracking purpose only. It will be changed during every update.
    // If user want to find which SKU is best sold, it should search the attributes.
    private SkuEntityId id;

    private List<AttributeEntity> attributes;   //Those attributes will determine the SKU.
    private List<PriceEntity> prices;

    public SkuEntityId getId() {
        return id;
    }

    public void setId(SkuEntityId id) {
        this.id = id;
    }

    public List<AttributeEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeEntity> attributes) {
        this.attributes = attributes;
    }

    public List<PriceEntity> getPrices() {
        return prices;
    }

    public void setPrices(List<PriceEntity> prices) {
        this.prices = prices;
    }
}