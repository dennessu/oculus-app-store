/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.sql
import com.junbo.common.id.OrderId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.dao.OrderDao
import com.junbo.order.db.dao.OrderPaymentInfoDao
import com.junbo.order.db.dao.OrderRevisionDao
import com.junbo.order.db.entity.OrderEntity
import com.junbo.order.db.entity.OrderPaymentInfoEntity
import com.junbo.order.db.entity.OrderRevisionEntity
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.db.repo.util.RepositoryFuncSet
import com.junbo.order.db.repo.util.Utils
import com.junbo.order.spec.model.*
import com.junbo.order.spec.model.enums.OrderStatus
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * Order Repository SQL impl
 */
@CompileStatic
@TypeChecked
@Component('sqlOrderRepository')
class OrderRepositorySqlImpl implements OrderRepository {

    @Autowired
    private OrderDao orderDao

    @Autowired
    private OrderPaymentInfoDao orderPaymentInfoDao

    @Resource(name = 'orderRevisionDao')
    private OrderRevisionDao orderRevisionDao

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('oculus40IdGenerator')
    private IdGenerator orderIdGenerator

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Override
    Promise<Order> get(OrderId id) {
        OrderEntity orderEntity = orderDao.read(id.value)
        if (orderEntity == null) {
            return Promise.pure(null)
        }
        Order result = modelMapper.toOrderModel(orderEntity, new MappingContext())
        result.setPayments(getPayments(result.getId().value))
        if (!result.tentative) {
            result.setOrderRevisions(getOrderRevisions(result.getId().value))
        }
        return Promise.pure(result)
    }

    @Override
    Promise<Order> create(Order order) {
        MappingContext context = new MappingContext()
        def orderEntity = modelMapper.toOrderEntity(order, context)

        // Save Order
        orderEntity.setOrderId(orderIdGenerator.nextId(order.user.value))
        orderEntity.setResourceAge(null)

        def id = orderDao.create(orderEntity)
        def orderId = new OrderId(id)
        order.setId(orderId)
        Utils.fillDateInfo(order, orderEntity)

        savePaymentInstruments(order.getId(), order.payments)

        // Save Order Item Tax Info
        return Promise.pure(order)
    }

    @Override
    Promise<Order> update(Order order) {       // Validations
        // TODO Log error and throw exception
        assert (order != null)
        assert (order.id != null)

        def oldEntity = orderDao.read(order.getId().value)
        if (oldEntity == null) {
            throw new IllegalArgumentException('name=Order_Not_Found')
        }
        // Update Order
        def orderEntity = modelMapper.toOrderEntity(order, new MappingContext())

        if (oldEntity.latestOrderRevisionId != orderEntity.latestOrderRevisionId) {
            def latestRevision = order.orderRevisions.find() { OrderRevision revision ->
                revision.id == orderEntity.latestOrderRevisionId
            }
            assert (latestRevision != null)
            orderRevisionDao.create(modelMapper.toOrderRevisionEntity(latestRevision, new MappingContext()))
        }

        orderEntity.createdTime = oldEntity.createdTime
        orderEntity.createdBy = oldEntity.createdBy
        if (orderEntity.resourceAge == null) {
            orderEntity.resourceAge = oldEntity.resourceAge
        }
        orderDao.update(orderEntity)
        orderEntity = orderDao.read(orderEntity.orderId)
        Utils.fillDateInfo(order, orderEntity)

        savePaymentInstruments(order.getId(), order.payments)

        return Promise.pure(order)
    }

    @Override
    Promise<Void> delete(OrderId id) {
        orderDao.markDelete(id.value)
        return Promise.pure(null)
    }

    @Override
    Promise<List<Order>> getByUserId(Long userId, OrderQueryParam orderQueryParam, PageParam pageParam) {
        return Promise.pure(orderDao.readByUserId(userId,
                orderQueryParam.tentative, pageParam?.start, pageParam?.count).collect { OrderEntity entity ->
            def result = modelMapper.toOrderModel(entity, new MappingContext())
            result.setPayments(getPayments(result.getId().value))
            result.setOrderRevisions(getOrderRevisions(result.getId().value))
            return result
        })
    }

    @Override
    Promise<List<Order>> getByStatus(Integer dataCenterId, Object shardKey, List<String> statusList,
                                     boolean updatedByAscending, PageParam pageParam) {
        return Promise.pure(orderDao.readByStatus(dataCenterId, (Integer) shardKey,
                statusList.collect { String status -> OrderStatus.valueOf(status) },
                updatedByAscending, pageParam?.start, pageParam?.count).collect { OrderEntity entity ->
            def result = modelMapper.toOrderModel(entity, new MappingContext())
            result.setPayments(getPayments(result.getId().value))
            result.setOrderRevisions(getOrderRevisions(result.getId().value))
            return result
        })
    }

    private List<PaymentInfo> getPayments(Long orderId) {
        List<PaymentInfo> paymentInfos = []
        orderPaymentInfoDao.readByOrderId(orderId)?.each { OrderPaymentInfoEntity paymentInfoEntity ->
            paymentInfos << modelMapper.toPaymentInfo(paymentInfoEntity, new MappingContext())
        }
        return paymentInfos
    }

    private List<OrderRevision> getOrderRevisions(Long orderId) {
        List<OrderRevision> orderRevisions = []
        orderRevisionDao.readByOrderId(orderId)?.each { OrderRevisionEntity revisionEntity ->
            orderRevisions << modelMapper.toOrderRevisionModel(revisionEntity, new MappingContext())
        }
        return orderRevisions
    }

    private void savePaymentInstruments(OrderId orderId, List<PaymentInfo> paymentInfos) {
        def repositoryFuncSet = new RepositoryFuncSet()

        def oldPaymentInfoEntityList = orderPaymentInfoDao.readByOrderId(orderId.value)
        def oldPaymentInfos = [] as ArrayList<PaymentInfo>
        def oldPaymentInfoEntityMap = [:] as HashMap<PaymentInstrumentId, OrderPaymentInfoEntity>

        oldPaymentInfoEntityList.each { OrderPaymentInfoEntity paymentInfo ->
            oldPaymentInfos << modelMapper.toPaymentInfo(paymentInfo, new MappingContext())
            oldPaymentInfoEntityMap[new PaymentInstrumentId(Long.parseLong(paymentInfo.paymentInstrumentId))] =
                    paymentInfo
        }

        repositoryFuncSet.create = { PaymentInfo pi ->
            savePaymentInstrument(orderId, pi, null)
        }

        repositoryFuncSet.update = { PaymentInfo newPi, PaymentInfo oldPi ->
            assert newPi.paymentInstrument == oldPi.paymentInstrument
            savePaymentInstrument(orderId, newPi, oldPaymentInfoEntityMap[oldPi.paymentInstrument].orderPaymentId)
            return true
        }

        repositoryFuncSet.delete = { PaymentInfo pi ->
            def entity = oldPaymentInfoEntityMap[pi.paymentInstrument]
            if (entity != null) {
                orderPaymentInfoDao.markDelete(entity.orderPaymentId)
                return true
            }
            return false
        }
        def keyFunc = { PaymentInfo pi ->
            return pi.paymentInstrument
        }

        Utils.updateListTypeField(paymentInfos, oldPaymentInfos, repositoryFuncSet, keyFunc,
                'payments')
    }

    private void savePaymentInstrument(OrderId orderId, PaymentInfo paymentInfo, Long oldEntityId) {
        def entity = modelMapper.toOrderPaymentInfoEntity(paymentInfo, new MappingContext())
        entity.orderId = orderId == null ? null : orderId.value
        entity.paymentInstrumentType = 'CREDIT_CAR' // todo may not need to save this field in db

        if (oldEntityId == null) { // create
            entity.orderPaymentId = idGenerator.nextId(orderId.value)
            orderPaymentInfoDao.create(entity)
        } else { // update
            def oldEntity = orderPaymentInfoDao.read(oldEntityId)
            entity.createdTime = oldEntity.createdTime
            entity.createdBy = oldEntity.createdBy
            entity.resourceAge = oldEntity.resourceAge
            entity.orderPaymentId = oldEntityId
            orderPaymentInfoDao.update(entity)
        }
    }
}
