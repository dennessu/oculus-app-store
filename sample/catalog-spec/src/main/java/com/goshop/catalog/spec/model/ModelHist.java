package com.goshop.catalog.spec.model;

import java.util.Date;

/**
 * Created by kevingu on 11/21/13.
 */
public class ModelHist<M extends Model> extends Model {

    private M item;

    public M getItem() {
        return item;
    }

    public void setItem(M item) {
        this.item = item;
    }
}
