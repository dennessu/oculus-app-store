// CHECKSTYLE:OFF
package com.junbo.email.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/email-templates")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class EmailTemplateResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultEmailTemplateResource")
    private com.junbo.email.spec.resource.EmailTemplateResource __adaptee;

    public com.junbo.email.spec.resource.EmailTemplateResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.email.spec.resource.EmailTemplateResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    public void getEmailTemplates(
    @javax.ws.rs.BeanParam com.junbo.email.spec.model.Paging paging,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.email.spec.model.EmailTemplate>> future;
    
        try {
            future = __adaptee.getEmailTemplates(
                paging
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.email.spec.model.EmailTemplate>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.email.spec.model.EmailTemplate> result) {
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
    public void postEmailTemplate(
    com.junbo.email.spec.model.EmailTemplate template,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.email.spec.model.EmailTemplate> future;
    
        try {
            future = __adaptee.postEmailTemplate(
                template
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.email.spec.model.EmailTemplate>() {
            @Override
            public void invoke(com.junbo.email.spec.model.EmailTemplate result) {
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
    @javax.ws.rs.Path("/{id}")
    public void getEmailTemplate(
    @javax.ws.rs.PathParam("id") com.junbo.common.id.EmailId id,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.email.spec.model.EmailTemplate> future;
    
        try {
            future = __adaptee.getEmailTemplate(
                id
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.email.spec.model.EmailTemplate>() {
            @Override
            public void invoke(com.junbo.email.spec.model.EmailTemplate result) {
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
    @javax.ws.rs.Path("/{id}")
    public void putEmailTemplate(
    @javax.ws.rs.PathParam("id") com.junbo.common.id.EmailId id,
    
    com.junbo.email.spec.model.EmailTemplate template,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.email.spec.model.EmailTemplate> future;
    
        try {
            future = __adaptee.putEmailTemplate(
                id,
                template
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.email.spec.model.EmailTemplate>() {
            @Override
            public void invoke(com.junbo.email.spec.model.EmailTemplate result) {
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
    @javax.ws.rs.Path("/{id}")
    public void deleteEmailTemplate(
    @javax.ws.rs.PathParam("id") com.junbo.common.id.EmailId id,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<javax.ws.rs.core.Response> future;
    
        try {
            future = __adaptee.deleteEmailTemplate(
                id
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
