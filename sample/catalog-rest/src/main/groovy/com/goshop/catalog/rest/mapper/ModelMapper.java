package com.goshop.catalog.rest.mapper;

import com.goshop.catalog.db.entity.category.CategoryEntity;
import com.goshop.catalog.db.entity.product.ProductEntity;
import com.goshop.catalog.spec.model.category.Category;
import com.goshop.catalog.spec.model.product.Product;
import com.goshop.oom.core.Mapper;
import com.goshop.oom.core.Mapping;
import com.goshop.oom.core.MappingContext;
import com.goshop.oom.core.Mappings;

import java.util.List;

@Mapper(uses = {
        CommonMapper.class,
        ExpandMapper.class
})
public interface ModelMapper {

    @Mappings({
            @Mapping(source = "createdDate", excluded = true, bidirectional = false),
            @Mapping(source = "createdBy", excluded = true, bidirectional = false),
            @Mapping(source = "modifiedBy", excluded = true, bidirectional = false),
            @Mapping(source = "modifiedDate", excluded = true, bidirectional = false),
    })
    CategoryEntity toCategoryEntity(Category category, MappingContext context);

    @Mappings({
            @Mapping(source = "parentCategoryId", target = "parentCategoryId", bidirectional = false),
            @Mapping(source = "parentCategoryId", target = "parentCategory", bidirectional = false),
    })
    Category toCategoryModel(CategoryEntity category, MappingContext context);


    List<Category> toCategoryModels(List<CategoryEntity> categories, MappingContext context);

    @Mappings({
            @Mapping(source = "createdDate", excluded = true, bidirectional = false),
            @Mapping(source = "createdBy", excluded = true, bidirectional = false),
            @Mapping(source = "modifiedBy", excluded = true, bidirectional = false),
            @Mapping(source = "modifiedDate", excluded = true, bidirectional = false),
            @Mapping(source = "skus", excluded = false, bidirectional = true)
    })
    ProductEntity toProductEntity(Product product, MappingContext context);

    @Mappings({
            @Mapping(source = "categoryId", target = "categoryId", bidirectional = false),
            @Mapping(source = "categoryId", target = "category", bidirectional = false),
            @Mapping(source = "storeId", target = "storeId", bidirectional = false),
            @Mapping(source = "storeId", target = "store", bidirectional = false),
    })
    Product toProductModel(ProductEntity product, MappingContext context);

    List<Product> toProductModels(List<ProductEntity> products, MappingContext context);
}
