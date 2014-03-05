package com.goshop.langur.processor.model.product;


import com.goshop.langur.processor.model.Model;
import com.goshop.langur.processor.model.attribute.Attribute;
import com.goshop.langur.processor.model.category.Category;
import com.goshop.langur.processor.model.event.Event;
import com.goshop.langur.processor.model.store.Store;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevingu on 11/21/13.
 */
public class Product extends Model {

    private String type;

    private String name;

    private String categoryId;

    private Category category;

    private List<Attribute> attributes;

    private List<Sku> skus = new ArrayList<Sku>();

    private String storeId;

    private Store store;

    private List<Event> eventActions;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Category getCategory() { return category; }

    public void setCategory(Category categories) {
        this.category = category;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<Sku> getSkus() {
        return skus;
    }

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public List<Event> getEventActions() {
        return eventActions;
    }

    public void setEventActions(List<Event> eventActions) {
        this.eventActions = eventActions;
    }
}
