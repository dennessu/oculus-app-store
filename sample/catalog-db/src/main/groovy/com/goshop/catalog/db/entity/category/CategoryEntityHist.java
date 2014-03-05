package com.goshop.catalog.db.entity.category;

import com.goshop.catalog.db.entity.EntityHist;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories_hist")
public class CategoryEntityHist extends EntityHist<CategoryEntity> {
    private CategoryEntity item;

    public CategoryEntity getItem() {
        return item;
    }

    public void setItem(CategoryEntity item) {
        this.item = item;
    }

    @Override
    public String getEntityType() {
        return "CategoryEntityHist";
    }
}
