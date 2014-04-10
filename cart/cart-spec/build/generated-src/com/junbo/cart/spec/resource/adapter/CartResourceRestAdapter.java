// CHECKSTYLE:OFF
package com.junbo.cart.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("users")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class CartResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultCartResource")
    private com.junbo.cart.spec.resource.CartResource __adaptee;

    public com.junbo.cart.spec.resource.CartResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.cart.spec.resource.CartResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    @javax.ws.rs.Path("/{userId}/carts")
    public void addCart(
    @javax.ws.rs.PathParam("userId") com.junbo.common.id.UserId userId,
    
    com.junbo.cart.spec.model.Cart cart,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.cart.spec.model.Cart> future;
    
        try {
            future = __adaptee.addCart(
                userId,
                cart
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.cart.spec.model.Cart>() {
            @Override
            public void invoke(com.junbo.cart.spec.model.Cart result) {
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
    @javax.ws.rs.Path("/{userId}/carts/{cartId}")
    public void getCart(
    @javax.ws.rs.PathParam("userId") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("cartId") com.junbo.common.id.CartId cartId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.cart.spec.model.Cart> future;
    
        try {
            future = __adaptee.getCart(
                userId,
                cartId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.cart.spec.model.Cart>() {
            @Override
            public void invoke(com.junbo.cart.spec.model.Cart result) {
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
    @javax.ws.rs.Path("/{userId}/carts/primary")
    public void getCartPrimary(
    @javax.ws.rs.PathParam("userId") com.junbo.common.id.UserId userId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.cart.spec.model.Cart> future;
    
        try {
            future = __adaptee.getCartPrimary(
                userId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.cart.spec.model.Cart>() {
            @Override
            public void invoke(com.junbo.cart.spec.model.Cart result) {
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
    @javax.ws.rs.Path("/{userId}/carts")
    public void getCartByName(
    @javax.ws.rs.PathParam("userId") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.QueryParam("cartName") java.lang.String cartName,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.cart.spec.model.Cart> future;
    
        try {
            future = __adaptee.getCartByName(
                userId,
                cartName
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.cart.spec.model.Cart>() {
            @Override
            public void invoke(com.junbo.cart.spec.model.Cart result) {
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
    @javax.ws.rs.Path("/{userId}/carts/{cartId}")
    public void updateCart(
    @javax.ws.rs.PathParam("userId") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("cartId") com.junbo.common.id.CartId cartId,
    
    com.junbo.cart.spec.model.Cart cart,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.cart.spec.model.Cart> future;
    
        try {
            future = __adaptee.updateCart(
                userId,
                cartId,
                cart
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.cart.spec.model.Cart>() {
            @Override
            public void invoke(com.junbo.cart.spec.model.Cart result) {
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
    @javax.ws.rs.Path("/{userId}/carts/{cartId}/merge")
    public void mergeCart(
    @javax.ws.rs.PathParam("userId") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("cartId") com.junbo.common.id.CartId cartId,
    
    com.junbo.cart.spec.model.Cart fromCart,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.cart.spec.model.Cart> future;
    
        try {
            future = __adaptee.mergeCart(
                userId,
                cartId,
                fromCart
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.cart.spec.model.Cart>() {
            @Override
            public void invoke(com.junbo.cart.spec.model.Cart result) {
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
