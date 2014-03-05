package com.goshop.catalog.rest.mapper;

import com.goshop.catalog.db.entity.EntityId;
import com.goshop.catalog.db.entity.category.CategoryEntityId;
import com.goshop.catalog.db.entity.product.ProductEntityId;
import com.goshop.catalog.db.entity.store.StoreEntityId;
import org.springframework.stereotype.Component;

@Component
public class CommonMapper {

    public EntityId toEntityId(String objectId) {
        if (objectId == null) return null;

        return new EntityId(objectId, true);
    }

    public String toId(EntityId id) {
        if (id == null) return null;

        return id.toString();
    }

    public CategoryEntityId toCategoryEntityId(String categoryId) {
        if (categoryId == null) return null;

        return new CategoryEntityId(categoryId, true);
    }

    public String toCategoryId(CategoryEntityId id) {
        if (id == null) return null;

        return id.toString();
    }

    public StoreEntityId toStoreEntityId(String storeId) {
        if (storeId == null) return null;

        return new StoreEntityId(storeId, true);
    }

    public String toStoreId(StoreEntityId id) {
        if (id == null) return null;

        return id.toString();
    }

    public ProductEntityId toProductEntityId(String productId) {
        if (productId == null) return null;

        return new ProductEntityId(productId, true);
    }

    public String toProductId(ProductEntityId id) {
        if (id == null) return null;

        return id.toString();
    }
}
