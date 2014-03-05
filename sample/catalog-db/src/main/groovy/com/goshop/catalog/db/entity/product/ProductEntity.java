package com.goshop.catalog.db.entity.product;

import com.goshop.catalog.db.entity.Entity;
import com.goshop.catalog.db.entity.attribute.AttributeEntity;
import com.goshop.catalog.db.entity.category.CategoryEntityId;
import com.goshop.catalog.db.entity.event.EventEntity;
import com.goshop.catalog.db.entity.store.StoreEntityId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevingu on 11/21/13.
 */
@Document(collection = "products")
public class ProductEntity extends Entity<ProductEntityId> {
    private String name;

    private CategoryEntityId categoryId;

    private List<AttributeEntity> attributes = new ArrayList<AttributeEntity>();

    private List<SkuEntity> skus = new ArrayList<SkuEntity>();

    private StoreEntityId storeId;

    // Per product can only be sold using same method.
    private List<EventEntity> eventActions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryEntityId getCategoryId() { return categoryId; }

    public void setCategoryId(CategoryEntityId categoryId) {
        this.categoryId = categoryId;
    }

    public List<AttributeEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeEntity> attributes) {
        this.attributes = attributes;
    }

    public List<SkuEntity> getSkus() {
        return skus;
    }

    public void setSkus(List<SkuEntity> skus) {
        this.skus = skus;
    }

    public StoreEntityId getStoreId() {
        return storeId;
    }

    public void setStoreId(StoreEntityId storeId) {
        this.storeId = storeId;
    }

    public List<EventEntity> getEventActions() {
        return eventActions;
    }

    public void setEventActions(List<EventEntity> eventActions) {
        this.eventActions = eventActions;
    }

    @Override
    public String getEntityType() {
        return "ProductEntity";
    }
}
