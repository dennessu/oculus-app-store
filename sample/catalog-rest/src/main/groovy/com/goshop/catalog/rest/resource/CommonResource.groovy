package com.goshop.catalog.rest.resource
import com.goshop.catalog.db.dao.CategoryEntityDAO
import com.goshop.catalog.db.dao.ProductEntityDAO
import com.goshop.catalog.db.dao.StoreEntityDAO
import com.goshop.catalog.db.entity.category.CategoryEntity
import com.goshop.catalog.db.entity.product.ProductEntity
import com.goshop.catalog.rest.mapper.ModelMapper
import com.goshop.catalog.rest.resolver.PageableResolver
import com.goshop.catalog.rest.resolver.PredicateResolver
import com.goshop.catalog.rest.resolver.PredicateResolverList
import com.mysema.query.types.Predicate
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.stereotype.Component

import javax.ws.rs.ext.Provider

@Provider
@Component
@CompileStatic
public class CommonResource {
    protected CategoryEntityDAO categoryEntityDAO
    protected ProductEntityDAO productEntityDAO
    protected StoreEntityDAO storeEntityDAO
    protected PredicateResolverList predicateResolver
    protected ModelMapper modelMapper
    protected PageableResolver pageableResolver

    @Required
    public void setCategoryEntityDAO(CategoryEntityDAO categoryEntityDAO) { this.categoryEntityDAO = categoryEntityDAO }

    @Required
    public void setProductEntityDAO(ProductEntityDAO productEntityDAO) { this.productEntityDAO = productEntityDAO }

    @Required
    public void setStoreEntityDAO(StoreEntityDAO storeEntityDAO) { this.storeEntityDAO = storeEntityDAO }

    @Required
    public void setPredicateResolver(PredicateResolverList predicateResolver) { this.predicateResolver = predicateResolver }

    @Required
    public void setModelMapper(ModelMapper modelMapper) { this.modelMapper = modelMapper; }

    @Required
    public void setPageableResolver(PageableResolver pageableResolver) {
        this.pageableResolver = pageableResolver;
    }

    protected CategoryEntity getCategoryByStatus(String categoryId, String status) {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("categoryId", categoryId);
        options.put("status", status);
        Predicate predicate = predicateResolver.resolve(CategoryEntity.class, options);
        return categoryEntityDAO.findOne(predicate);
    }

    protected List<CategoryEntity> getCategoriesByStatus(String categoryId, String status) {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("categoryId", categoryId);
        options.put("status", status);
        Predicate predicate = predicateResolver.resolve(CategoryEntity.class, options);
        return categoryEntityDAO.findAll(predicate).asList();
    }

    protected List<CategoryEntity> getCategoriesByParentCategoryId(String parentCategoryId, String status) {
        Map<String, Object> options = new HashMap<String, Object>()
        options.put("parentCategoryId", parentCategoryId)
        options.put("status", status)
        Predicate predicate = predicateResolver.resolve(CategoryEntity.class, options)
        return categoryEntityDAO.findAll(predicate).asList()
    }

    protected ProductEntity getProductByStatus(String productId, String status) {
        Map<String, Object> options = new HashMap<String, Object>()
        options.put("productId", productId);
        options.put("status", status);
        Predicate predicate = predicateResolver.resolve(ProductEntity.class, options);
        return productEntityDAO.findOne(predicate);
    }

    protected List<ProductEntity> getProductsByStatus(String productId, String status) {
        Map<String, Object> options = new HashMap<String, Object>()
        options.put("productId", productId);
        options.put("status", status);
        Predicate predicate = predicateResolver.resolve(ProductEntity.class, options);
        return productEntityDAO.findAll(predicate).asList();
    }

    protected ProductEntity getProductByRevision(String productId, Long revision) {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("productId", productId);
        options.put("revision", revision);
        Predicate predicate = predicateResolver.resolve(ProductEntity.class, options);
        return productEntityDAO.findOne(predicate);
    }
}