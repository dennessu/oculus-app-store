/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.core.service.impl

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.cart.auth.CartAuthorizeCallbackFactory
import com.junbo.cart.core.client.IdentityClient
import com.junbo.cart.core.service.CartPersistService
import com.junbo.cart.core.service.CartService
import com.junbo.cart.core.validation.Validation
import com.junbo.cart.spec.error.AppErrors
import com.junbo.cart.spec.model.Cart
import com.junbo.cart.spec.model.item.CartItem
import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.common.id.CartId
import com.junbo.common.id.CartItemId
import com.junbo.common.id.CouponId
import com.junbo.common.id.OfferId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

import static com.junbo.authorization.spec.error.AppErrors.INSTANCE

/**
 * Created by fzhang@wan-san.com on 14-2-14.
 */
@CompileStatic
class CartServiceImpl implements CartService {

    public final static String CART_NAME_PRIMARY = 'primary'

    private Validation validation

    private CartPersistService cartPersistService

    private IdentityClient identityClient

    private AuthorizeService authorizeService

    private CartAuthorizeCallbackFactory authorizeCallbackFactory

    void setValidation(Validation validation) {
        this.validation = validation
    }

    void setCartPersistService(CartPersistService cartPersistService) {
        this.cartPersistService = cartPersistService
    }

    void setIdentityClient(IdentityClient userClient) {
        this.identityClient = userClient
    }

    void setAuthorizeService(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService
    }

    void setAuthorizeCallbackFactory(CartAuthorizeCallbackFactory authorizeCallbackFactory) {
        this.authorizeCallbackFactory = authorizeCallbackFactory
    }

    @Override
    Promise<Cart> addCart(Cart cart, String clientId, UserId userId) {
        return identityClient.getUser(userId).then { User user ->
            validation.validateUser(user).validateCartAdd(clientId, userId, cart)
            def callback = authorizeCallbackFactory.create(userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('create')) {
                    throw INSTANCE.forbidden().exception()
                }

                cart.clientId = clientId
                cart.user = userId
                cart.userLoggedIn = true // todo : set from user
                processCartForAddOrUpdate(cart)
                cartPersistService.saveNewCart(cart)
                return Promise.pure(cart)
            }
        }
    }

    @Override
    Promise<Cart> getCart(UserId userId, CartId cartId) {
        return internalGetCart(userId, cartId, 'read')
    }

    private Promise<Cart> internalGetCart(UserId userId, CartId cartId, String requiredRight) {
        return identityClient.getUser(userId).then {
            validation.validateUser((User) it)
            def callback = authorizeCallbackFactory.create(userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights(requiredRight)) {
                    if (requiredRight == 'read') {
                        throw AppErrors.INSTANCE.cartNotFound().exception()
                    } else {
                        throw INSTANCE.forbidden().exception()
                    }
                }

                Cart cart = cartPersistService.getCart(cartId, true)
                if (cart == null) {
                    throw AppErrors.INSTANCE.cartNotFound().exception()
                }
                validation.validateCartOwner(cart, userId)
                return Promise.pure(cart)
            }
        }
    }

    @Override
    Promise<Cart> getCartByName(String clientId, String cartName, UserId userId) {
        if (StringUtils.isEmpty(cartName)) {
            throw AppErrors.INSTANCE.fieldInvalid('cartName', 'value could not be empty').exception()
        }
        if (cartName == CART_NAME_PRIMARY) {
            return getCartPrimary(clientId, userId)
        }
        return identityClient.getUser(userId).then {
            validation.validateUser((User) it)
            def callback = authorizeCallbackFactory.create(userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('read')) {
                    throw AppErrors.INSTANCE.cartNotFound().exception()
                }

                Cart cart = cartPersistService.getCart(clientId, cartName, userId, true)
                if (cart == null) {
                    return Promise.pure(null)
                }
                validation.validateCartOwner(cart, userId)
                return Promise.pure(cart)
            }
        }
    }

    @Override
    Promise<Cart> getCartPrimary(String clientId, UserId userId) {
        return identityClient.getUser(userId).then {
            validation.validateUser((User) it)
            def callback = authorizeCallbackFactory.create(userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('read')) {
                    throw AppErrors.INSTANCE.cartNotFound().exception()
                }

                Cart cart = cartPersistService.getCart(clientId, CART_NAME_PRIMARY, userId, true)
                if (cart == null) {
                    cart = new Cart()
                    cart.cartName = CART_NAME_PRIMARY
                    cart.user = userId
                    cart.userLoggedIn = true // todo : set from user
                    cart.clientId = clientId
                    cartPersistService.saveNewCart(cart)
                }
                return Promise.pure(cart)
            }
        }
    }

    @Override
    Promise<Cart> updateCart(UserId userId, CartId cartId, Cart cart) {
        return internalGetCart(userId, cartId, 'update').then {
            Cart oldCart = (Cart) it
            validation.validateCartUpdate(cart, oldCart)
            processCartForAddOrUpdate(cart)
            cart.id = cartId
            cart.cartName = oldCart.cartName
            cart.user = oldCart.user
            cart.clientId = oldCart.clientId
            cart.userLoggedIn = oldCart.userLoggedIn
            cart.createdTime = oldCart.createdTime
            cartPersistService.updateCart(cart)
            return Promise.pure(cart)
        }
    }

    /**
     * @deprecated the merge logic does not needed anymore
     */
    @Deprecated
    @Override
    Promise<Cart> mergeCart(UserId userId, CartId cartId, Cart fromCart) {
        validation.validateMerge(fromCart)
        return internalGetCart(userId, cartId, 'merge').then { Cart destCart ->
            return internalGetCart(fromCart.user, fromCart.getId(), 'merge').syncThen { Cart cart ->
                destCart.offers.each {
                    ((OfferItem) it).isSelected = false
                }
                cart.offers.each {
                    ((CartItem) it).id = null
                }
                addCartItems(destCart, cart.offers, cart.coupons)
                cart.offers = Collections.EMPTY_LIST
                cart.coupons = Collections.EMPTY_LIST
                cartPersistService.updateCart(cart)
                cartPersistService.updateCart(destCart)
                return destCart
            }
        }
    }

    @Override
    Promise<Cart> addOfferItem(UserId userId, CartId cartId, OfferItem offerItem) {
        return internalGetCart(userId, cartId, 'update').then {
            Cart cart = (Cart) it
            validation.validateOfferAdd(offerItem)
            addCartItems(cart, [offerItem], [])
            cartPersistService.updateCart(cart)
            return Promise.pure(cart)
        }
    }

    @Override
    Promise<Cart> updateOfferItem(UserId userId, CartId cartId, CartItemId offerItemId, OfferItem offerItem) {
        return internalGetCart(userId, cartId, 'update').then {
            Cart cart = (Cart) it
            OfferItem o = cart.offers.find {
                return ((OfferItem) it).id == offerItemId
            }
            if (o == null) {
                throw AppErrors.INSTANCE.cartItemNotFound().exception()
            }

            validation.validateOfferUpdate(offerItem)
            // update the offer and save
            o.offer = offerItem.offer
            o.quantity = offerItem.quantity
            o.isSelected = offerItem.isSelected
            cart.offers = mergeOffers(cart.offers)
            cartPersistService.updateCart(cart)
            return Promise.pure(cart)
        }
    }

    @Override
    Promise<Cart> deleteOfferItem(UserId userId, CartId cartId, CartItemId offerItemId) {
        return internalGetCart(userId, cartId, 'update').then {
            Cart cart = (Cart) it
            if (lookupAndRemoveItem((List<CartItem>) cart.offers, offerItemId) == null) {
                throw AppErrors.INSTANCE.cartItemNotFound().exception()
            }
            cartPersistService.updateCart(cart)
            return Promise.pure(cart)
        }
    }

    private List<OfferItem> mergeOffers(List<OfferItem> offers) {
        Map<OfferId, OfferItem> offersMap = new HashMap<OfferId, OfferItem>()
        offers.each {
            OfferItem e = (OfferItem) it
            OfferItem current = offersMap[e.offer]
            if (current == null) {
                offersMap[e.offer] = e
            } else {
                current.quantity += e.quantity
                current.isSelected |= e.isSelected
                if (current.id == null && e.id != null) {
                    current.id = e.id
                }
            }
        }
        return removeZeroQuantityOffer(new ArrayList<OfferItem>(offersMap.values()))
    }

    private static List<CouponId> mergeCoupons(List<CouponId> coupons) {
        Set<CouponId> couponsSet = [] as HashSet
        coupons.each { CouponId coupon ->
            couponsSet.add(coupon)
        }
        return new ArrayList<CouponId>(couponsSet)
    }

    private void addCartItems(Cart cart, List<OfferItem> offers, List<CouponId> coupons) {
        if (offers != null) {
            cart.offers.addAll(offers)
        }
        cart.offers = mergeOffers(cart.offers)
        if (coupons != null) {
            cart.coupons.addAll(coupons)
        }
        cart.coupons = mergeCoupons(cart.coupons)
    }

    private static List<OfferItem> removeZeroQuantityOffer(List<OfferItem> offers) {
        if (!CollectionUtils.isEmpty(offers)) {
            Iterator<OfferItem> it = offers.iterator()
            while (it.hasNext()) {
                if (it.next().quantity <= 0) {
                    it.remove()
                }
            }
        }
        return offers
    }

    private CartItem lookupAndRemoveItem(List<CartItem> items, CartItemId cartItemId) {
        Iterator<CartItem> it = items.iterator()
        while (it.hasNext()) {
            CartItem e = it.next()
            if (e.id == cartItemId) {
                it.remove()
                return e
            }
        }
        return null
    }

    private Cart processCartForAddOrUpdate(Cart cart) {
        cart.offers = mergeOffers(cart.offers)
        removeZeroQuantityOffer(cart.offers)
        cart.coupons = mergeCoupons(cart.coupons)
        return cart
    }
}

