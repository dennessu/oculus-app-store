/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.persist
import com.google.common.collect.HashMultimap
import com.junbo.cart.common.util.SystemOperation
import com.junbo.cart.core.service.CartPersistService
import com.junbo.cart.db.dao.CartDao
import com.junbo.cart.db.dao.CartItemDao
import com.junbo.cart.db.entity.CartEntity
import com.junbo.cart.db.entity.CouponItemEntity
import com.junbo.cart.db.entity.ItemStatus
import com.junbo.cart.db.entity.OfferItemEntity
import com.junbo.cart.db.mapper.CartMapper
import com.junbo.cart.spec.model.Cart
import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.common.id.CartId
import com.junbo.common.id.CartItemId
import com.junbo.common.id.CouponId
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

    class ItemPersistFuncSet {
        Closure create
        Closure update
        Closure delete
    }

    @Autowired
    private IdGeneratorFacade idGenerator

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
        saveOffers(newCart, newCart.offers, [], currentTime)
        // add coupon
        saveCoupons(newCart, newCart.coupons, [], currentTime)
    }

    @Override
    @Transactional()
    void updateCart(Cart cart) {
        Cart oldCart = getCart(cart.getId(), true)

        // update cart
        Date currentTime = systemOperation.currentTime()
        cart.updatedTime = currentTime
        if (cart.resourceAge == null) {
            cart.resourceAge = oldCart.resourceAge
        }
        CartEntity cartEntity = dataMapper.toCartEntity(cart, new MappingContext())
        cartEntity = cartDao.update(cartEntity)
        cart.resourceAge = cartEntity.resourceAge

        // update items
        cart.offers = saveOffers(cart, cart.offers, oldCart.offers, currentTime)
        cart.coupons = saveCoupons(cart, cart.coupons, oldCart.coupons, currentTime)
    }

    private List<OfferItem> saveOffers(Cart cart, List<OfferItem> newItems,
                                       List<OfferItem> oldItems, Date currentTime) {

        def offerPersistFuncSet = new ItemPersistFuncSet()

        offerPersistFuncSet.create = { OfferItem offerItem ->
                addOfferItem(offerItem, currentTime, cart)
        }

        offerPersistFuncSet.delete = { OfferItem offerItem ->
            return offerItemDao.markDelete(offerItem.id.value, currentTime)
        }

        offerPersistFuncSet.update = { OfferItem newOfferItem, OfferItem oldOfferItem ->
            if (diffOfferItem(newOfferItem, oldOfferItem)) {
                newOfferItem.id = oldOfferItem.id
                updateOfferItem(newOfferItem, currentTime)
                return true
            }
            return false
        }

        def keyFunc = { OfferItem offerItem ->
            return offerItem.offer
        }

        saveCartItems(newItems, oldItems, offerPersistFuncSet, keyFunc, 'offer')
        return newItems
    }

    private List<CouponId> saveCoupons(Cart cart, List<CouponId> newCoupons, List<CouponId> oldCoupons,
                                         Date currentTime) {

        Map<String, Long> couponCodesToId = new HashMap<>()
        couponItemDao.getItems(cart.getId().value, ItemStatus.OPEN)?.each { CouponItemEntity entity ->
            couponCodesToId[entity.couponCode] = entity.cartItemId
        }

        def couponPersistFuncSet = new ItemPersistFuncSet()

        couponPersistFuncSet.create =  { CouponId coupon ->
            return addCoupon(coupon.value.toString(), currentTime, cart)
        }

        couponPersistFuncSet.delete = { CouponId coupon ->
            Long couponId = couponCodesToId[coupon.value.toString()]
            if (couponId != null) {
                return couponItemDao.markDelete(couponId, currentTime)
            }
            return false
        }

        couponPersistFuncSet.update = { CouponId oldCoupon, CouponId newCoupon ->
            assert oldCoupon == newCoupon
            return false
        }

        def keyFunc = { CouponId coupon ->
            return coupon
        }

        saveCartItems(newCoupons, oldCoupons, couponPersistFuncSet, keyFunc, 'coupon')
        return newCoupons
    }

    private void saveCartItems(List newItems, List oldItems, ItemPersistFuncSet itemPersistFuncSet, Closure keyFunc,
                             String type) {
        def oldMap = HashMultimap.create()
        oldItems.each {
            oldMap.put(keyFunc.call(it), it)
        }
        def numCreated = 0, numUpdated = 0, numDeleted = 0

        def lookupAndRemove = {
            def values = oldMap.get(keyFunc.call(it))
            if (values.empty) {
                return null
            }
            def iterator = values.iterator()
            def found = iterator.next()
            iterator.remove()
            return found
        }

        newItems.each { newItem ->
            def oldItem = lookupAndRemove(newItem)
            if (oldItem == null) { // create
                itemPersistFuncSet.create.call(newItem)
                numCreated++
            } else { // update
                if (itemPersistFuncSet.update.call(newItem, oldItem)) {
                    numUpdated++
                }
            }
        }

        oldMap.values().each { // delete
            if (itemPersistFuncSet.delete.call(it)) {
                numDeleted++
            }
        }

        LOGGER.debug('name=Save_Cart_Item, itemType={}, numCreated={}, numUpdated={}, numDeleted={}',
                type, numCreated, numUpdated, numDeleted)
    }

    private void addOfferItem(OfferItem newItem, Date currentTime, Cart cart) {
        newItem.id = getId(cart.user, CartItemId)

        def entity =  dataMapper.toOfferItemEntity(newItem, new MappingContext())
        entity.cartId = cart.getId().value
        entity.status = ItemStatus.OPEN
        entity.createdTime = currentTime
        entity.updatedTime = currentTime

        offerItemDao.insert(entity)
    }

    private void updateOfferItem(OfferItem updateItem, Date currentTime) {
        def oldEntity = offerItemDao.get(updateItem.id.value)

        def entity = dataMapper.toOfferItemEntity(updateItem, new MappingContext())
        entity.status = oldEntity.status
        entity.cartId = oldEntity.cartId
        entity.createdTime = oldEntity.createdTime
        entity.updatedTime = currentTime

        offerItemDao.update(entity)
    }

    private void addCoupon(String couponCode, Date currentTime, Cart cart) {
        def entity = new CouponItemEntity(
                cartItemId: getId(cart.user, CartItemId).value,
                cartId: cart.getId().value,
                status: ItemStatus.OPEN,
                couponCode: couponCode,
                createdTime: currentTime,
                updatedTime: currentTime
        )

        couponItemDao.insert(entity)
    }

    private Cart toCart(CartEntity cartEntity, includeItems) {
        Cart result = dataMapper.toCartModel(cartEntity, new MappingContext())
        result.offers = []
        result.coupons = []
        if (includeItems) {
            List<OfferItemEntity> offerItemEntities = offerItemDao.getItems(cartEntity.id, ItemStatus.OPEN)
            List<CouponItemEntity> couponItemEntities = couponItemDao.getItems(cartEntity.id, ItemStatus.OPEN)
            if (!CollectionUtils.isEmpty(offerItemEntities)) {
                offerItemEntities.each {
                    result.offers << dataMapper.toOfferItemModel((OfferItemEntity) it, new MappingContext())
                }
            }
            if (!CollectionUtils.isEmpty(couponItemEntities)) {
                couponItemEntities.each { CouponItemEntity couponItemEntity ->
                    result.coupons << new CouponId(Long.parseLong(couponItemEntity.couponCode))
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

    private boolean diffOfferItem(OfferItem left, OfferItem right) {
        return !(left.offer == right.offer &&
                left.quantity == right.quantity &&
                left.isSelected == right.isSelected &&
                left.isApproved == right.isApproved)
    }
}
