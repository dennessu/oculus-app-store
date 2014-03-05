package com.goshop.langur.processor.model;

public class ModelHist<M extends Model> extends Model {

    private M item;

    public M getItem() {
        return item;
    }

    public void setItem(M item) {
        this.item = item;
    }
}
