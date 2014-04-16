package com.junbo.cart.rest.util

import com.junbo.cart.core.service.CartService
import com.junbo.cart.spec.model.Cart
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Created by fzhang on 4/16/2014.
 */
@CompileStatic
class CartServiceAdapterFactory {

    static CartService newInstance(CartService adaptee) {
        return (CartService) Proxy.newProxyInstance(CartService.classLoader,
                [CartService].toArray(new Class[0]),
                new CartIdPropertyHandler(adaptee))
    }

    static class CartIdPropertyHandler implements InvocationHandler {

        private final CartService cartService

        CartIdPropertyHandler(CartService cartService) {
            this.cartService = cartService
        }

        @Override
        Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            assert Promise.isAssignableFrom(method.returnType)
            return ((Promise) method.invoke(cartService, args)).syncThen { Cart cart ->
                // todo set user id
                // if (cart != null) {
                //    cart.id.properties['userId'] = cart.user
                // }
                return cart
            }
        }
    }
}
