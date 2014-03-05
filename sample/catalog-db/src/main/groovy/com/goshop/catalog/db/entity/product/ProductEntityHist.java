package com.goshop.catalog.db.entity.product;

import com.goshop.catalog.db.entity.EntityHist;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products_hist")
public class ProductEntityHist extends EntityHist<ProductEntity> {

    private ProductEntity item;

    public void setItem(ProductEntity item) {
        this.item = item;
    }

    public ProductEntity getItem() {
        return item;
    }

    @Override
    public String getEntityType() {
        return "ProductEntityHist";
    }
}
