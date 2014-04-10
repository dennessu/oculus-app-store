// CHECKSTYLE:OFF
package com.junbo.identity.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/password-rules")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class PasswordResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultPasswordResource")
    private com.junbo.identity.spec.resource.PasswordResource __adaptee;

    public com.junbo.identity.spec.resource.PasswordResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.identity.spec.resource.PasswordResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{passwordRuleId}")
    public void getPasswordRule(
    @javax.ws.rs.PathParam("passwordRuleId") com.junbo.common.id.PasswordRuleId passwordRuleId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.identity.spec.model.password.PasswordRule> future;
    
        try {
            future = __adaptee.getPasswordRule(
                passwordRuleId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.identity.spec.model.password.PasswordRule>() {
            @Override
            public void invoke(com.junbo.identity.spec.model.password.PasswordRule result) {
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
    public void postPasswordRule(
    com.junbo.identity.spec.model.password.PasswordRule passwordRule,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.identity.spec.model.password.PasswordRule> future;
    
        try {
            future = __adaptee.postPasswordRule(
                passwordRule
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.identity.spec.model.password.PasswordRule>() {
            @Override
            public void invoke(com.junbo.identity.spec.model.password.PasswordRule result) {
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
    @javax.ws.rs.Path("/{appId}")
    public void updatePasswordRule(
    @javax.ws.rs.PathParam("passwordRuleId") com.junbo.common.id.PasswordRuleId passwordRuleId,
    
    com.junbo.identity.spec.model.password.PasswordRule passwordRule,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.identity.spec.model.password.PasswordRule> future;
    
        try {
            future = __adaptee.updatePasswordRule(
                passwordRuleId,
                passwordRule
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.identity.spec.model.password.PasswordRule>() {
            @Override
            public void invoke(com.junbo.identity.spec.model.password.PasswordRule result) {
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
