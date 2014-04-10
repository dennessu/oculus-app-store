// CHECKSTYLE:OFF
package com.junbo.catalog.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("attributes")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class AttributeResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultAttributeResource")
    private com.junbo.catalog.spec.resource.AttributeResource __adaptee;

    public com.junbo.catalog.spec.resource.AttributeResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.catalog.spec.resource.AttributeResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{attributeId}")
    public void getAttribute(
    @javax.ws.rs.PathParam("attributeId") com.junbo.common.id.AttributeId attributeId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.catalog.spec.model.attribute.Attribute> future;
    
        try {
            future = __adaptee.getAttribute(
                attributeId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.catalog.spec.model.attribute.Attribute>() {
            @Override
            public void invoke(com.junbo.catalog.spec.model.attribute.Attribute result) {
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
    @javax.ws.rs.Path("/")
    public void getAttributes(
    @javax.ws.rs.BeanParam com.junbo.catalog.spec.model.attribute.AttributesGetOptions options,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.catalog.spec.model.attribute.Attribute>> future;
    
        try {
            future = __adaptee.getAttributes(
                options
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.catalog.spec.model.attribute.Attribute>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.catalog.spec.model.attribute.Attribute> result) {
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
    public void createAttribute(
    com.junbo.catalog.spec.model.attribute.Attribute attribute,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.catalog.spec.model.attribute.Attribute> future;
    
        try {
            future = __adaptee.createAttribute(
                attribute
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.catalog.spec.model.attribute.Attribute>() {
            @Override
            public void invoke(com.junbo.catalog.spec.model.attribute.Attribute result) {
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
