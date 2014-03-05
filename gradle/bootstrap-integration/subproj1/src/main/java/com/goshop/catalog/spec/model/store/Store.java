package com.goshop.catalog.spec.model.store;

import com.goshop.catalog.spec.model.Model;
import com.goshop.catalog.spec.model.attribute.Attribute;

import java.util.List;

public class Store extends Model {

    private String name;

    private List<Attribute> attributes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

}
