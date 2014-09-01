/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.sql

import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.dao.OrderItemSnapshotDao
import com.junbo.order.db.entity.OrderOfferItemSnapshotEntity
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.ItemSnapshotRepository
import com.junbo.order.spec.model.ItemSnapshot
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Created by LinYi on 2014/8/28.
 */
@CompileStatic
@TypeChecked
@Component('sqlItemSnapshotRepository')
class ItemSnapshotSqlImpl implements ItemSnapshotRepository {
    @Autowired
    private OrderItemSnapshotDao itemSnapshotDao

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Override
    Promise<List<ItemSnapshot>> getByOfferSnapshotId(Long offerSnapshotId) {
        List<ItemSnapshot> itemSnapshots = []
        MappingContext context = new MappingContext()
        itemSnapshotDao.readByOfferSnapshotId(offerSnapshotId)?.each { OrderOfferItemSnapshotEntity entity ->
            itemSnapshots << modelMapper.toItemSnapshotModel(entity, context)
        }
        return Promise.pure(itemSnapshots)
    }

    @Override
    Promise<ItemSnapshot> get(Long id) {
        return Promise.pure(modelMapper.toItemSnapshotModel(itemSnapshotDao.read(id), new MappingContext()))
    }

    @Override
    Promise<ItemSnapshot> create(ItemSnapshot model) {
        def entity = modelMapper.toOrderOfferItemSnapshotEntity(model, new MappingContext())
        entity.itemSnapshotId = idGenerator.nextId(model.offerSnapshotId)
        itemSnapshotDao.create(entity)
        return Promise.pure(modelMapper.toItemSnapshotModel(entity, new MappingContext()))
    }

    @Override
    Promise<ItemSnapshot> update(ItemSnapshot model, ItemSnapshot oldModel) {
        def entity = modelMapper.toOrderOfferItemSnapshotEntity(model, new MappingContext())
        itemSnapshotDao.update(entity)
        return Promise.pure(modelMapper.toItemSnapshotModel(entity, new MappingContext()))
    }

    @Override
    Promise<Void> delete(Long id) {
        itemSnapshotDao.markDelete(id)
        return Promise.pure(null)
    }
}
