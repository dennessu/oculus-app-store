package com.goshop.catalog.rest.mapper;

import com.goshop.catalog.db.dao.CategoryEntityDAO;
import com.goshop.catalog.db.entity.category.CategoryEntity;
import com.goshop.catalog.db.entity.category.CategoryEntityId;
import com.goshop.catalog.spec.model.category.Category;
import com.goshop.oom.core.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExpandMapper {

    @Autowired
    private ModelMapper modelMapperImpl;

    @Autowired
    private CategoryEntityDAO categoryEntityDAO;

    public Category expand(CategoryEntityId categoryEntityId, MappingContext context) {
        if (categoryEntityId == null) return null;

        if (context.getEntitiesToExpand() == null || !context.getEntitiesToExpand().contains("category")) {
            return null;
        }

        CategoryEntity categoryEntity = categoryEntityDAO.findOne(categoryEntityId);

        return modelMapperImpl.toCategoryModel(categoryEntity, context);
    }
}
