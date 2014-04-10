// CHECKSTYLE:OFF
package com.junbo.catalog.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("items")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class ItemResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultItemResource")
    private com.junbo.catalog.spec.resource.ItemResource __adaptee;

    public com.junbo.catalog.spec.resource.ItemResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.catalog.spec.resource.ItemResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/")
    public void getItems(
    @javax.ws.rs.BeanParam com.junbo.catalog.spec.model.item.ItemsGetOptions options,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.catalog.spec.model.item.Item>> future;
    
        try {
            future = __adaptee.getItems(
                options
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.catalog.spec.model.item.Item>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.catalog.spec.model.item.Item> result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{itemId}")
    public void getItem(
    @javax.ws.rs.PathParam("itemId") com.junbo.common.id.ItemId itemId,
    
    @javax.ws.rs.BeanParam com.junbo.catalog.spec.model.common.EntityGetOptions options,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.catalog.spec.model.item.Item> future;
    
        try {
            future = __adaptee.getItem(
                itemId,
                options
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.catalog.spec.model.item.Item>() {
            @Override
            public void invoke(com.junbo.catalog.spec.model.item.Item result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.POST
    @javax.ws.rs.Path("/")
    public void create(
    com.junbo.catalog.spec.model.item.Item item,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.catalog.spec.model.item.Item> future;
    
        try {
            future = __adaptee.create(
                item
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.catalog.spec.model.item.Item>() {
            @Override
            public void invoke(com.junbo.catalog.spec.model.item.Item result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.PUT
    @javax.ws.rs.Path("/{itemId}")
    public void update(
    @javax.ws.rs.PathParam("itemId") com.junbo.common.id.ItemId itemId,
    
    com.junbo.catalog.spec.model.item.Item item,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.catalog.spec.model.item.Item> future;
    
        try {
            future = __adaptee.update(
                itemId,
                item
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.catalog.spec.model.item.Item>() {
            @Override
            public void invoke(com.junbo.catalog.spec.model.item.Item result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.DELETE
    @javax.ws.rs.Path("/{itemId}")
    public void delete(
    @javax.ws.rs.PathParam("itemId") com.junbo.common.id.ItemId itemId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<javax.ws.rs.core.Response> future;
    
        try {
            future = __adaptee.delete(
                itemId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<javax.ws.rs.core.Response>() {
            @Override
            public void invoke(javax.ws.rs.core.Response result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
}
