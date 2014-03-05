package com.goshop.catalog.rest.resource

import com.google.common.base.Function
import com.goshop.catalog.common.error.AppError
import com.goshop.catalog.common.error.AppErrors
import com.goshop.catalog.db.entity.EntityStatus
import com.goshop.catalog.db.entity.product.ProductEntity
import com.goshop.catalog.db.entity.product.ProductEntityId
import com.goshop.catalog.db.entity.product.SkuEntity
import com.goshop.catalog.db.entity.product.SkuEntityId
import com.goshop.catalog.rest.util.EntityUtil
import com.goshop.catalog.rest.util.MappingContextUtil
import com.goshop.catalog.rest.util.PageMetadataUtil
import com.goshop.catalog.rest.validation.ProductValidation
import com.goshop.catalog.spec.model.ResultList
import com.goshop.catalog.spec.model.options.GetOptions
import com.goshop.catalog.spec.model.options.product.ProductGetOptions
import com.goshop.catalog.spec.model.product.Product
import com.goshop.catalog.spec.resource.ProductResource
import com.goshop.oom.core.MappingContext
import com.mysema.query.types.Predicate
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerResponse
import org.glassfish.jersey.server.internal.process.RespondingContext
import org.springframework.beans.factory.annotation.Required
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider

@Provider
@Component
@CompileStatic
public class ProductResourceImpl extends CommonResource implements ProductResource {
    private ProductValidation validation;

    @Context
    private ContainerRequestContext requestContext;

    @Context
    private RespondingContext respondingContext;

    @Required
    public void setValidation(ProductValidation validation) {
        this.validation = validation
    }

    @Override
    ResultList<Product> getProducts(ProductGetOptions getOptions) {
        def pageable = pageableResolver.resolve(ProductEntity, getOptions.properties)

        Predicate predicate = predicateResolver.resolve(ProductEntity, getOptions.properties)

        def productEntities = productEntityDAO.findAll(predicate, pageable)

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, getOptions)

        def productModels = modelMapper.toProductModels(productEntities.content, mappingContext)

        return new ResultList<Product>(
                page: PageMetadataUtil.fromPage(productEntities),
                content: productModels
        )
    }

    @Override
    Product getProduct(String productId, GetOptions getOptions) {
        def productEntity = getProductByStatus(productId,
                getOptions.properties.get("status")?(String)getOptions.properties.get("status"):EntityStatus.RELEASED)
        if (productEntity == null) {
            throw AppErrors.INSTANCE.objectIdNotFound(productId, "productId").exception()
        }

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, getOptions)

        return modelMapper.toProductModel(productEntity, mappingContext)
    }

    @Override
    Product getProductDraft(String productId, GetOptions getOptions) {
        def productEntity = getProductByStatus(productId,
                getOptions.properties.get("status") ? (String)getOptions.properties.get("status"): EntityStatus.DRAFT)

        if(productEntity == null) {
            throw AppErrors.INSTANCE.objectIdNotFound(productId, "productId").exception();
        }

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, getOptions)

        return modelMapper.toProductModel(productEntity, mappingContext)
    }

    @Override
    ResultList<Product> getProductHists(String productId, GetOptions getOptions) {
        List<ProductEntity> productList = new ArrayList<ProductEntity>()
        productList.addAll(getProductsByStatus(productId, EntityStatus.DRAFT))
        productList.addAll(getProductsByStatus(productId, EntityStatus.RELEASED))
        productList.addAll(getProductsByStatus(productId, EntityStatus.DELETED))

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, getOptions)

        def result = modelMapper.toProductModels(productList, mappingContext)
        return new ResultList<Product>(content: result)
    }

    @Override
    Product getProductRevision(String productId, Long version, GetOptions getOptions) {
        ProductEntity productEntity = getProductByRevision(productId, version);

        if(productEntity == null) {
            throw AppErrors.INSTANCE.objectIdRevisionNotFound(productId, version).exception();
        }

        MappingContext mappingContext = new MappingContext();
        MappingContextUtil.applyOptions(mappingContext, getOptions);

        return modelMapper.toProductModel(productEntity, mappingContext)
    }


    @Override
    Product createProduct(Product product) {
        def productEntity = modelMapper.toProductEntity(product, new MappingContext())

        AppError appError = validation.validateCreation(productEntity);
        if(appError != null) {
            throw appError.exception();
        }

        EntityUtil.initializeForCreate(productEntity, ProductEntityId)
        initProductEntitySku(productEntity)

        productEntityDAO.save(productEntity)

        // process response header
        if (requestContext != null && respondingContext != null) {
            def location = requestContext.uriInfo.absolutePath.toString() + "/" + productEntity.entityId + "/draft"

            respondingContext.push({ ContainerResponse response ->
                if (response.statusInfo.family == Response.Status.Family.SUCCESSFUL) {
                    response.headers.add("location", location)

                    if (response.statusInfo == Response.Status.OK) {
                        response.statusInfo = Response.Status.CREATED
                    }
                }
                return response
            } as Function<ContainerResponse, ContainerResponse>);
        }

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, new GetOptions())

        return modelMapper.toProductModel(productEntity, mappingContext)
    }

    @Override
    Product updateProduct(String productId, Product product) {
        def productEntity = modelMapper.toProductEntity(product, new MappingContext())
        ProductEntityId id = new ProductEntityId(productId)
        productEntity.setEntityId(id)
        EntityUtil.initializeForUpdate(productEntity)
        def existingProductEntity = getProductByStatus(productId, EntityStatus.DRAFT);

        if(existingProductEntity == null) {
            existingProductEntity = getProductByStatus(id.toString(), EntityStatus.RELEASED);
        }
        // SKU ID can be changed per update, if it is released, it won't change any more. :)
        initProductEntitySku(productEntity);

        AppError appError = validation.validateUpdate(existingProductEntity, productEntity);
        if(appError != null) {
            throw appError.exception();
        }

        if(existingProductEntity.getStatus().equals(EntityStatus.DRAFT)) {
            productEntityDAO.delete(existingProductEntity)
        };
        productEntity = productEntityDAO.save(productEntity);

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, new GetOptions())

        return modelMapper.toProductModel(productEntity, mappingContext)
    }

    @Override
    Product releaseProduct(String productId) {
        def productEntity = getProductByStatus(productId, EntityStatus.DRAFT)

        if(productEntity == null) {
            throw AppErrors.INSTANCE.draftNotFound(productId, "product : " + EntityStatus).exception();
        }

        AppError appError = validation.validateRelease(productEntity)
        if(appError != null) {
            throw appError.exception();
        }

        def released = getProductByStatus(productId, EntityStatus.RELEASED)
        if(released != null) {
            productEntity.version = released.version + 1
        }else {
            productEntity.version = 1
        }

        productEntity.status = EntityStatus.RELEASED
        productEntity = productEntityDAO.save(productEntity)

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, new GetOptions())

        return modelMapper.toProductModel(productEntity, mappingContext)
    }

    @Override
    public Product deleteProduct(String productId) {
        def released = getProductByStatus(productId, EntityStatus.RELEASED)

        if(released == null) {
            throw AppErrors.INSTANCE.objectIdNotFound(productId, "Status : " + EntityStatus.RELEASED).exception();
        }
        AppError appError = validation.validateDelete(released)
        if(appError != null) {
            throw appError.exception();
        }
        released.setStatus(EntityStatus.DELETED)
        released = categoryEntityDAO.save(released)

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, new GetOptions())

        return modelMapper.toProductModel(released, mappingContext)
    }

    private void initProductEntitySku(ProductEntity productEntity) {
        if(CollectionUtils.isEmpty(productEntity.getSkus())) {
            return
        }

        for(SkuEntity sku : productEntity.getSkus()) {
            if(sku != null) {
                sku.setId(SkuEntityId.newInstance());
            }
        }
    }
}