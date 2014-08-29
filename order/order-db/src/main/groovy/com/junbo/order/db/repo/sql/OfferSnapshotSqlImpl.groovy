/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.sql

import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.dao.OrderOfferSnapshotDao
import com.junbo.order.db.entity.OrderOfferSnapshotEntity
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.OfferSnapshotRepository
import com.junbo.order.spec.model.OfferSnapshot
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
@Component('sqlOfferSnapshotRepository')
class OfferSnapshotSqlImpl implements OfferSnapshotRepository {
    @Autowired
    private OrderOfferSnapshotDao offerSnapshotDao

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Override
    Promise<OfferSnapshot> get(Long id) {
        return Promise.pure(modelMapper.toOfferSnapshotModel(offerSnapshotDao.read(id), new MappingContext()))
    }

    @Override
    Promise<OfferSnapshot> create(OfferSnapshot model) {
        def entity = modelMapper.toOrderOfferSnapshotEntity(model, new MappingContext())
        entity.offerSnapshotId = idGenerator.nextId(model.orderId)
        offerSnapshotDao.create(entity)
        return Promise.pure(modelMapper.toOfferSnapshotModel(entity, new MappingContext()))
    }

    @Override
    Promise<OfferSnapshot> update(OfferSnapshot model, OfferSnapshot oldModel) {
        def entity = modelMapper.toOrderOfferSnapshotEntity(model, new MappingContext())
        offerSnapshotDao.update(entity)
        return Promise.pure(modelMapper.toOfferSnapshotModel(entity, new MappingContext()))
    }

    @Override
    Promise<Void> delete(Long id) {
        offerSnapshotDao.markDelete(id)
        return Promise.pure(null)
    }

    @Override
    Promise<List<OfferSnapshot>> getByOrderId(Long orderId) {
        List<OfferSnapshot> offerSnapshots = []
        MappingContext context = new MappingContext()
        offerSnapshotDao.readByOrderId(orderId)?.each { OrderOfferSnapshotEntity entity ->
            offerSnapshots << modelMapper.toOfferSnapshotModel(entity, context)
        }
        return Promise.pure(offerSnapshots)
    }
}
