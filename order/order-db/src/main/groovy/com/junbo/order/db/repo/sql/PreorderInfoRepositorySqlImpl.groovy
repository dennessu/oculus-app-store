/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.sql
import com.junbo.common.id.PreorderId
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.dao.OrderItemPreorderInfoDao
import com.junbo.order.db.entity.OrderItemPreorderInfoEntity
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.PreorderInfoRepository
import com.junbo.order.spec.model.PreorderInfo
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
@Component('sqlPreorderInfoRepository')
class PreorderInfoRepositorySqlImpl implements PreorderInfoRepository {

    @Autowired
    private OrderItemPreorderInfoDao preorderInfoDao

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Override
    Promise<PreorderInfo> get(PreorderId id) {
        return Promise.pure(modelMapper.toPreOrderInfoModel(preorderInfoDao.read(id.value), new MappingContext()))
    }

    @Override
    Promise<PreorderInfo> create(PreorderInfo preorderInfo) {
        def entity = modelMapper.toOrderItemPreorderInfoEntity(preorderInfo, new MappingContext())
        entity.orderItemPreorderInfoId = idGenerator.nextId(preorderInfo.orderItemId.value)
        preorderInfoDao.create(entity)
        return Promise.pure(modelMapper.toPreOrderInfoModel(entity, new MappingContext()))
    }

    @Override
    Promise<PreorderInfo> update(PreorderInfo preorderInfo, PreorderInfo oldPreorderInfo) {
        def entity = modelMapper.toOrderItemPreorderInfoEntity(preorderInfo, new MappingContext())
        preorderInfoDao.update(entity)
        return Promise.pure(modelMapper.toPreOrderInfoModel(entity, new MappingContext()))
    }

    @Override
    Promise<Void> delete(PreorderId id) {
        preorderInfoDao.markDelete(id.value)
        return Promise.pure(null)
    }

    @Override
    Promise<List<PreorderInfo>> getByOrderItemId(Long orderItemId) {
        List<PreorderInfo> preorderInfoList = []
        MappingContext context = new MappingContext()
        preorderInfoDao.readByOrderItemId(orderItemId).each { OrderItemPreorderInfoEntity entity ->
            preorderInfoList << modelMapper.toPreOrderInfoModel(entity, context)
        }
        return Promise.pure(preorderInfoList)
    }
}
