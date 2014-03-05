/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.db.repo;

import com.junbo.fulfilment.db.dao.FulfilmentDao;
import com.junbo.fulfilment.db.entity.FulfilmentEntity;
import com.junbo.fulfilment.db.mapper.Mapper;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * FulfilmentRepository.
 */
public class FulfilmentRepository {
    @Autowired
    private FulfilmentDao fulfilmentDao;

    public FulfilmentItem create(FulfilmentItem item) {
        item.setFulfilmentId(fulfilmentDao.create(Mapper.map(item)));
        return item;
    }

    public FulfilmentItem get(Long id) {
        return Mapper.map(fulfilmentDao.get(id));
    }

    public List<FulfilmentItem> findByRequestId(Long requestId) {
        List<FulfilmentEntity> fulfilmentEntities = fulfilmentDao.findByRequestId(requestId);

        List<FulfilmentItem> items = new ArrayList();
        for (FulfilmentEntity entity : fulfilmentEntities) {
            items.add(Mapper.map(entity));
        }

        return items;
    }
}
