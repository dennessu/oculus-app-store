package com.goshop.catalog.db.entity.event;

import com.goshop.catalog.db.entity.attribute.AttributeEntity;

import java.util.List;

/**
 * Created by kevingu on 11/21/13.
 */
public class ActionEntity {

    private String type;

    private List<AttributeEntity> attributes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AttributeEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeEntity> attributes) {
        this.attributes = attributes;
    }

}
