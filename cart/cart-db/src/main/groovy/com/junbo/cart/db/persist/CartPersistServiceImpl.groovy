/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.persist

import com.junbo.cart.common.util.SystemOperation
import com.junbo.cart.core.service.CartPersistService
import com.junbo.cart.db.dao.CartDao
import com.junbo.cart.db.dao.CartItemDao
import com.junbo.cart.db.entity.*
import com.junbo.cart.db.mapper.CartMapper
import com.junbo.cart.spec.model.Cart
import com.junbo.cart.spec.model.item.CartItem
import com.junbo.cart.spec.model.item.CouponItem
import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.common.id.CartId
import com.junbo.common.id.CartItemId
import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.oom.core.MappingContext
import com.junbo.sharding.IdGeneratorFacade
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.CollectionUtils

/**
 * Created by fzhang@wan-san.com on 14-1-28.
 */
@CompileStatic
class CartPersistServiceImpl implements CartPersistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartPersistServiceImpl)

    private SystemOperation systemOperation

    private CartDao cartDao

    private CartItemDao<OfferItemEntity> offerItemDao

    private CartItemDao<CouponItemEntity> couponItemDao

    private CartMapper dataMapper

    @Autowired
    private IdGeneratorFacade idGenerator

    private final Map<Class, CartItemCallback> itemCallbackMap =
            [
                    (OfferItem):(CartItemCallback) new OfferCallback(),
                    (CouponItem):(CartItemCallback) new CouponCallback()
            ]

    private interface  CartItemCallback {

        CartItemEntity toEntity(CartItem m)

        boolean diff(CartItem left, CartItem right)
    }

    void setSystemOperation(SystemOperation systemOperation) {
        this.systemOperation = systemOperation
    }

    void setCartDao(CartDao cartDao) {
        this.cartDao = cartDao
    }

    void setOfferItemDao(CartItemDao<OfferItemEntity> offerItemDao) {
        this.offerItemDao = offerItemDao
    }

    void setCouponItemDao(CartItemDao<CouponItemEntity> couponItemDao) {
        this.couponItemDao = couponItemDao
    }

    void setDataMapper(CartMapper dataMapper) {
        this.dataMapper = dataMapper
    }

    void setIdGenerator(IdGeneratorFacade idGenerator) {
        this.idGenerator = idGenerator
    }

    private class OfferCallback implements CartItemCallback {

        @Override
        CartItemEntity toEntity(CartItem m) {
            return dataMapper.toOfferItemEntity((OfferItem) m, new MappingContext())
        }

        @Override
        boolean diff(CartItem left, CartItem right) {
            def leftOffer = (OfferItem) left
            def rightOffer = (OfferItem) right
            return !(leftOffer.offer.id == rightOffer.offer.id &&
                    leftOffer.quantity == rightOffer.quantity &&
                    leftOffer.selected == rightOffer.selected)
        }
    }

    private class CouponCallback implements CartItemCallback {

        @Override
        CartItemEntity toEntity(CartItem m) {
            return dataMapper.toCouponItemEntity((CouponItem) m, new MappingContext())
        }

        @Override
        boolean diff(CartItem left, CartItem right) {
            return ((CouponItem) left).coupon.id != ((CouponItem) right).coupon.id
        }
    }

    @Override
    @Transactional
    Cart getCart(CartId cartId, boolean includeItems) {
        CartEntity entity = cartDao.get(cartId.value)
        return entity == null ? null : toCart(entity, includeItems)
    }

    @Override
    @Transactional
    Cart getCart(String clientId, String cartName, UserId userId, boolean includeItems) {
        CartEntity entity = cartDao.get(clientId, cartName, userId.value)
        return entity == null ? null : toCart(entity, includeItems)
    }

    @Override
    @Transactional
    void saveNewCart(Cart newCart) {
        // add cart
        CartId cartId = getId(newCart.user, CartId)
        Date currentTime = systemOperation.currentTime()
        newCart.createdTime = currentTime
        newCart.updatedTime = currentTime
        newCart.resourceAge = null
        newCart.id = cartId
        CartEntity cartEntity = dataMapper.toCartEntity(newCart, new MappingContext())
        cartDao.insert(cartEntity)
        newCart.resourceAge = cartEntity.resourceAge

        // add offer
        if (!CollectionUtils.isEmpty(newCart.offers)) {
            newCart.offers.each {
                saveNew((OfferItem) it, currentTime, newCart, offerItemDao)
            }
        }

        // add coupon
        if (!CollectionUtils.isEmpty(newCart.coupons)) {
            newCart.coupons.each {
                saveNew((CouponItem) it, currentTime, newCart, couponItemDao)
            }
        }
    }

    @Override
    @Transactional()
    void updateCart(Cart cart) {
        Cart oldCart = getCart(cart.id, true)

        // update cart
        Date currentTime = systemOperation.currentTime()
        cart.updatedTime = currentTime
        cart.resourceAge = oldCart.resourceAge
        CartEntity cartEntity = dataMapper.toCartEntity(cart, new MappingContext())
        cartEntity = cartDao.update(cartEntity)
        cart.resourceAge = cartEntity.resourceAge

        // update items
        cart.offers = updateCartItems(cart, cart.offers, oldCart.offers, currentTime, offerItemDao)
        cart.coupons = updateCartItems(cart, cart.coupons, oldCart.coupons, currentTime, couponItemDao)
    }

    private <M extends CartItem, E extends CartItemEntity> List<M> updateCartItems(Cart newCart,
                    List<M> newItems, List<M> oldItems, Date currentTime, CartItemDao<E> dao) {
        def numAdded = 0, numUpdated = 0, numDeleted = 0
        Map<CartItemId, M> remainingItemsMap = new HashMap<CartItemId, M>()
        List<M> result = []
        oldItems.each {
            M item = (M) it
            remainingItemsMap[item.id] = item
        }

        newItems.each {
            M updatedItem = (M) it
            if (updatedItem.id != null) {
                M originalItem = remainingItemsMap[updatedItem.id]
                if (originalItem != null) {
                    if (itemCallbackMap[updatedItem.class].diff(originalItem, updatedItem)) { // update item
                        numUpdated++
                        updatedItem.createdTime = originalItem.createdTime
                        saveUpdate(updatedItem, currentTime, newCart, dao)
                    } else {
                        updatedItem = originalItem
                    }
                    remainingItemsMap.remove(updatedItem.id)
                    result << updatedItem
                }
            } else { // add item
                numAdded++
                saveNew((M) it, currentTime, newCart, dao)
                result << updatedItem
            }
        }

        remainingItemsMap.values().each { // delete item
            E entity = dao.get(((M) it).id.value)
            if (entity != null) {
                entity.status = ItemStatus.DELETED
                numDeleted++
            }
        }

        LOGGER.info('name=CartItemPersist, numAdded={}, numUpdated={}, numDeleted={}',
                [numAdded.toString(), numUpdated.toString(), numDeleted.toString()])

        return result
    }

    private void saveNew(CartItem newItem, Date currentTime, Cart cart, CartItemDao dao) {
        newItem.createdTime = currentTime
        newItem.updatedTime = currentTime
        newItem.id = getId(cart.user, CartItemId)
        CartItemEntity entity =  itemCallbackMap[newItem.getClass()].toEntity(newItem)
        entity.cartId = cart.id.value
        entity.status = ItemStatus.OPEN
        dao.insert((CartItemEntity) entity)
    }

    private  void saveUpdate(CartItem updateItem, Date currentTime, Cart cart, CartItemDao dao) {
        updateItem.updatedTime = currentTime
        CartItemEntity entity = itemCallbackMap[updateItem.getClass()].toEntity(updateItem)
        entity.status = ItemStatus.OPEN
        entity.cartId = cart.id.value
        dao.update((CartItemEntity) entity)
    }

    private Cart toCart(CartEntity cartEntity, includeItems) {
        List<OfferItemEntity> offerItemEntities = offerItemDao.getItems(cartEntity.id, ItemStatus.OPEN)
        List<CouponItemEntity> couponItemEntities = couponItemDao.getItems(cartEntity.id, ItemStatus.OPEN)
        Cart result = dataMapper.toCartModel(cartEntity, new MappingContext())
        result.offers = []
        result.coupons = []
        if (includeItems) {
            if (!CollectionUtils.isEmpty(offerItemEntities)) {
                offerItemEntities.each {
                    result.offers << dataMapper.toOfferItemModel((OfferItemEntity) it, new MappingContext())
                }
            }
            if (!CollectionUtils.isEmpty(couponItemEntities)) {
                couponItemEntities.each {
                    result.coupons << dataMapper.toCouponItemModel((CouponItemEntity) it, new MappingContext())
                }
            }
        }
        return result
    }

    private <T extends Id> T getId(UserId userId, Class<T> type) {
        T id = type.newInstance()
        id.value = idGenerator.nextId(type, userId.value)
        return id
    }
}
