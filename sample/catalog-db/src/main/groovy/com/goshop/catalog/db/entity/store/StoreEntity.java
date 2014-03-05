package com.goshop.catalog.db.entity.store;

import com.goshop.catalog.db.entity.Entity;
import com.goshop.catalog.db.entity.attribute.AttributeEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by kevingu on 11/21/13.
 */
@Document(collection = "stores")
public class StoreEntity extends Entity<StoreEntityId> {

    private String name;

    private List<AttributeEntity> attributes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AttributeEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeEntity> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getEntityType() {
        return "StoreEntity";
    }
}
