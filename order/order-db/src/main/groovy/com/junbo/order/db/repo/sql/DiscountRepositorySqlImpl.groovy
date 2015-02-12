/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.sql
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.dao.OrderDiscountInfoDao
import com.junbo.order.db.entity.OrderDiscountInfoEntity
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.DiscountRepository
import com.junbo.order.db.repo.util.Utils
import com.junbo.order.spec.model.Discount
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
@Component('sqlDiscountRepository')
class DiscountRepositorySqlImpl implements DiscountRepository {

    @Autowired
    private OrderDiscountInfoDao discountDao

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Override
    Promise<Discount> get(Long id) {
        return Promise.pure(null)
    }

    @Override
    Promise<Discount> create(Discount discount) {
        discount.id = idGenerator.nextId(discount.orderId.value)
        def entity = modelMapper.toDiscountEntity(discount, new MappingContext())
        discountDao.create(entity)

        Utils.fillDateInfo(discount, entity)
        return Promise.pure(discount)
    }

    @Override
    Promise<Discount> update(Discount discount, Discount oldDiscount) {
        assert discount != null && (discount.orderItemId != null || discount.orderId != null)

        def entity = modelMapper.toDiscountEntity(discount, new MappingContext())
        def oldEntity = discountDao.read(entity.discountInfoId)

        entity.createdTime = oldEntity.createdTime
        entity.createdBy = oldEntity.createdBy
        entity.resourceAge = oldEntity.resourceAge

        discountDao.update(entity)
        Utils.fillDateInfo(discount, entity)

        return Promise.pure(discount)
    }

    @Override
    Promise<Void> delete(Long id) {
        discountDao.markDelete(id)
        return Promise.pure(null)
    }

    @Override
    Promise<List<Discount>> getByOrderId(Long orderId) {
        List<Discount> discounts = []
        MappingContext context = new MappingContext()
        discountDao.readByOrderId(orderId)?.each { OrderDiscountInfoEntity orderDiscountInfoEntity ->
            discounts << modelMapper.toDiscountModel(orderDiscountInfoEntity, context)
        }
        return Promise.pure(discounts)
    }
}
