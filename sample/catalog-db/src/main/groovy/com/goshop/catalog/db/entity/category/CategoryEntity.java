package com.goshop.catalog.db.entity.category;

import com.goshop.catalog.db.entity.Entity;
import com.goshop.catalog.db.entity.attribute.AttributeEntity;
import com.goshop.catalog.db.entity.store.StoreEntityId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "categories")
public class CategoryEntity extends Entity<CategoryEntityId> {

    private String name;

    private String type;

    private CategoryEntityId parentCategoryId;

    private List<AttributeEntity> attributes;

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

    public CategoryEntityId getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(CategoryEntityId parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public List<AttributeEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeEntity> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getEntityType() {
        return "CategoryEntity";
    }
}
