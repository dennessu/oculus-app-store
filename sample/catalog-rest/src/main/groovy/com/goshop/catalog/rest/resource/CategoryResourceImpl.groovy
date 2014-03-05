package com.goshop.catalog.rest.resource

import com.google.common.base.Function
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.goshop.catalog.clientproxy.proxy.MyResourceClientProxy
import com.goshop.catalog.common.error.AppError
import com.goshop.catalog.common.error.AppErrors
import com.goshop.catalog.db.entity.EntityStatus
import com.goshop.catalog.db.entity.category.CategoryEntity
import com.goshop.catalog.db.entity.category.CategoryEntityId
import com.goshop.catalog.rest.util.EntityUtil
import com.goshop.catalog.rest.util.MappingContextUtil
import com.goshop.catalog.rest.util.PageMetadataUtil
import com.goshop.catalog.rest.validation.CategoryValidation
import com.goshop.catalog.spec.model.ResultList
import com.goshop.catalog.spec.model.category.Category
import com.goshop.catalog.spec.model.options.category.CategoryGetOptions
import com.goshop.catalog.spec.model.options.category.CategoryPostOptions
import com.goshop.catalog.spec.resource.CategoryResource
import com.goshop.langur.core.client.JsonMessageTranscoder
import com.goshop.oom.core.MappingContext
import com.mysema.query.types.Predicate
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncHttpClientConfigBean
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerResponse
import org.glassfish.jersey.server.internal.process.RespondingContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

import javax.annotation.Nullable
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider

@Component
@Scope("prototype")
@CompileStatic
public class CategoryResourceImpl extends CommonResource implements CategoryResource {
    private CategoryValidation validation;

    @Context
    private ContainerRequestContext requestContext;

    @Context
    private RespondingContext respondingContext;

    private static AsyncHttpClient asyncHttpClient;

    @Required
    void setValidation(CategoryValidation validation) {
        this.validation = validation
    }

    CategoryResourceImpl() {

        if (asyncHttpClient == null) {
           asyncHttpClient = new AsyncHttpClient(new AsyncHttpClientConfigBean());
        }
    }

    @Override
    ListenableFuture<ResultList<Category>> getCategories(CategoryGetOptions getOptions) {

        Pageable pageable = pageableResolver.resolve(CategoryEntity, getOptions.properties)

        Predicate predicate = predicateResolver.resolve(CategoryEntity, getOptions.properties)

        def categoryEntities = categoryEntityDAO.findAll(predicate, pageable)

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, getOptions)

        def categoryModels = modelMapper.toCategoryModels(categoryEntities.content, mappingContext)

        if (getOptions.name == "blocking") {

            def future = new MyResourceClientProxy(asyncHttpClient, new JsonMessageTranscoder(), "http://localhost:8081/myapp").getIt()
            //future.get()

            return Futures.transform(future, new Function<String, ResultList<Category>>() {
                @Override
                ResultList<Category> apply(String input) {
                    System.out.println("Output: " + input)

                    new ResultList<Category>(
                            page: PageMetadataUtil.fromPage(categoryEntities),
                            content: categoryModels
                    )
                }
            })
        }

        return Futures.immediateFuture(new ResultList<Category>(
                page: PageMetadataUtil.fromPage(categoryEntities),
                content: categoryModels
        ))
    }

    @Override
    ListenableFuture<Category> getCategory(String categoryId, CategoryGetOptions getOptions) {

        def categoryEntity = getCategoryByStatus(categoryId,
                getOptions.properties.get("status") ? (String) getOptions.properties.get("status") : EntityStatus.RELEASED)
        if (categoryEntity == null) {
            throw AppErrors.INSTANCE.objectIdNotFound(categoryId, "categoryId").exception()
        }

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, getOptions)

        return Futures.immediateFuture(modelMapper.toCategoryModel(categoryEntity, mappingContext))
    }

    @Override
    ListenableFuture<Category> getCategoryDraft(String categoryId, CategoryGetOptions getOptions) {

        def categoryEntity = getCategoryByStatus(categoryId, EntityStatus.DRAFT)

        if (categoryEntity == null) {
            throw AppErrors.INSTANCE.draftNotFound(categoryId, "categoryId").exception()
        }

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, getOptions)

        return Futures.immediateFuture(modelMapper.toCategoryModel(categoryEntity, mappingContext))
    }

    @Override
    ListenableFuture<ResultList<Category>> getCategoryChildren(String categoryId, CategoryGetOptions getOptions) {
        def categoryEntity = getCategoryByStatus(categoryId, getOptions.getStatus())
        if (categoryEntity == null) {
            throw AppErrors.INSTANCE.objectIdNotFound(categoryId, "categoryId").exception()
        }

        def children = getCategoriesByParentCategoryId(categoryId, getOptions.getStatus())

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, getOptions)

        def result = modelMapper.toCategoryModels(children, mappingContext)
        return Futures.immediateFuture(new ResultList<Category>(content: result))
    }

    @Override
    ListenableFuture<ResultList<Category>> getCategoryDescendents(String categoryId, CategoryGetOptions getOptions) {
        def entityId = new CategoryEntityId(categoryId, true)

        def categoryEntity = getCategoryByStatus(categoryId, getOptions.getStatus())
        if (categoryEntity == null) {
            throw AppErrors.INSTANCE.objectIdNotFound(categoryId, "categoryId").exception()
        }

        def descendents = [];

        Closure listDescendents = null;
        listDescendents = { CategoryEntityId parentId ->
            if (descendents.size() > 1000) return

            def children = getCategoriesByParentCategoryId(parentId.toString(), getOptions.getStatus())
            descendents.addAll(children)

            children.each { CategoryEntity parent -> listDescendents(parent.entityId) }
        }
        listDescendents(entityId)

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, getOptions)

        def result = modelMapper.toCategoryModels(descendents, mappingContext)
        return Futures.immediateFuture(new ResultList<Category>(content: result))
    }

    @Override
    ListenableFuture<ResultList<Category>> getCategoryParents(String categoryId, CategoryGetOptions getOptions) {
        def categoryEntity = getCategoryByStatus(categoryId, getOptions.getStatus())
        if (categoryEntity == null) {
            throw AppErrors.INSTANCE.objectIdNotFound(categoryId, "categoryId").exception()
        }

        def parents = []
        while (categoryEntity.parentCategoryId != null && parents.size() < 1000) {
            categoryEntity = getCategoryByStatus(categoryEntity.parentCategoryId.toString(), getOptions.getStatus())

            if (categoryEntity != null) {
                parents.add(categoryEntity)
            } else {
                break
            }
        }

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, getOptions)

        def result = modelMapper.toCategoryModels(parents, mappingContext)
        return Futures.immediateFuture(new ResultList<Category>(content: result))
    }

    @Override
    ListenableFuture<Category> createCategory(Category category, CategoryPostOptions postOptions) {
        def categoryEntity = modelMapper.toCategoryEntity(category, new MappingContext())

        AppError appError = validation.validateCreation(categoryEntity);
        if (appError != null) {
            throw appError.exception();
        }
        EntityUtil.initializeForCreate(categoryEntity, CategoryEntityId)
        categoryEntityDAO.save(categoryEntity)

        // process response header
        if (requestContext != null && respondingContext != null) {
            def location = requestContext.uriInfo.absolutePath.toString() + "/" + categoryEntity.entityId + "/draft"

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
        MappingContextUtil.applyOptions(mappingContext, postOptions)

        return Futures.immediateFuture(modelMapper.toCategoryModel(categoryEntity, mappingContext))
    }

    @Override
    ListenableFuture<Category> updateCategory(String categoryId, Category category, CategoryPostOptions postOptions) {
        def categoryEntity = modelMapper.toCategoryEntity(category, new MappingContext())
        CategoryEntityId id = new CategoryEntityId(categoryId)
        categoryEntity.setEntityId(id)
        EntityUtil.initializeForUpdate(categoryEntity)

        def existingCategoryEntity = getCategoryByStatus(categoryId, EntityStatus.DRAFT);

        if (existingCategoryEntity == null) {
            existingCategoryEntity = getCategoryByStatus(id.toString(), EntityStatus.RELEASED);
        }

        AppError appError = validation.validateUpdate(existingCategoryEntity, categoryEntity);
        if (appError != null) {
            throw appError.exception();
        }

        if (existingCategoryEntity.getStatus().equals(EntityStatus.DRAFT)) {
            categoryEntityDAO.delete(existingCategoryEntity)
        };
        categoryEntity = categoryEntityDAO.save(categoryEntity);

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, postOptions)

        return Futures.immediateFuture(modelMapper.toCategoryModel(categoryEntity, mappingContext))
    }

    @Override
    ListenableFuture<Category> releaseCategory(String categoryId, CategoryPostOptions postOptions) {
        def draft = getCategoryByStatus(categoryId, EntityStatus.DRAFT)
        if (draft == null) {
            throw AppErrors.INSTANCE.draftNotFound(categoryId, "categoryId").exception()
        }

        AppError appError = validation.validateRelease(draft);
        if (appError != null) {
            throw appError.exception();
        }

        def current = getCategoryByStatus(categoryId, EntityStatus.RELEASED)
        if (current != null) {
            draft.version = current.version + 1
        } else {
            draft.version = 1
        }

        draft.status = EntityStatus.RELEASED

        draft = categoryEntityDAO.save(draft)

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, postOptions)

        return Futures.immediateFuture(modelMapper.toCategoryModel(draft, mappingContext))
    }

    @Override
    ListenableFuture<ResultList<Category>> getCategoryHists(String categoryId, CategoryGetOptions getOptions) {
        List<CategoryEntity> hists = new ArrayList<>();
        hists.addAll(getCategoriesByStatus(categoryId, EntityStatus.DRAFT))
        hists.addAll(getCategoriesByStatus(categoryId, EntityStatus.RELEASED))
        hists.addAll(getCategoriesByStatus(categoryId, EntityStatus.DELETED))

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, getOptions)

        def result = modelMapper.toCategoryModels(hists, mappingContext)
        return Futures.immediateFuture(new ResultList<Category>(content: result))
    }

    @Override
    ListenableFuture<Category> deleteCategory(String categoryId, CategoryPostOptions postOptions) {
        def his = getCategoryByStatus(categoryId, EntityStatus.RELEASED)

        if (his == null) {
            throw AppErrors.INSTANCE.objectIdNotFound(categoryId, "Status : " + EntityStatus.RELEASED).exception();
        }
        AppError appError = validation.validateDelete(his)
        if (appError != null) {
            throw appError.exception();
        }
        categoryEntityDAO.delete(his)
        his.setStatus(EntityStatus.DELETED)
        his = categoryEntityDAO.save(his)

        MappingContext mappingContext = new MappingContext()
        MappingContextUtil.applyOptions(mappingContext, postOptions)

        return Futures.immediateFuture(modelMapper.toCategoryModel(his, mappingContext))
    }
}
