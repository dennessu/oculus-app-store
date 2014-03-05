package com.goshop.langur.processor.model.store;

import com.goshop.langur.processor.model.Model;
import com.goshop.langur.processor.model.attribute.Attribute;

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
