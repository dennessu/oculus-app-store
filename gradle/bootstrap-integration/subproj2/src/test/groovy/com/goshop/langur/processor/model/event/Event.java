package com.goshop.langur.processor.model.event;

import com.goshop.langur.processor.model.attribute.Attribute;

import java.util.List;

/**
 * Created by kevingu on 11/21/13.
 */
public class Event {

    private String type;

    private List<Attribute> attributes;

    private List<Action> actions;

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

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
