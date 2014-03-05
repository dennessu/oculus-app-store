package com.goshop.catalog.spec.model.event;

import com.goshop.catalog.spec.model.attribute.Attribute;

import java.util.List;

/**
 * Created by kevingu on 11/21/13.
 */
public class Action {

    private String type;

    private List<Attribute> attributes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }
}
