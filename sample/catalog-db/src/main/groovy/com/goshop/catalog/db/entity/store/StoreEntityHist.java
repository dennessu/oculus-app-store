package com.goshop.catalog.db.entity.store;

import com.goshop.catalog.db.entity.EntityHist;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stores_hist")
public class StoreEntityHist extends EntityHist<StoreEntity> {

    private StoreEntity item;

    public void setItem(StoreEntity item) {
        this.item = item;
    }

    public StoreEntity getItem() {
        return item;
    }

    @Override
    public String getEntityType() {
        return "StoreEntityHist";
    }
}
