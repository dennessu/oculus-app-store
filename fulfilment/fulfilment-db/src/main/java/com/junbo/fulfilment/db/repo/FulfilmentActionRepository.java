/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.db.repo;

import com.junbo.fulfilment.db.dao.FulfilmentActionDao;
import com.junbo.fulfilment.db.entity.FulfilmentActionEntity;
import com.junbo.fulfilment.db.entity.FulfilmentStatus;
import com.junbo.fulfilment.db.mapper.Mapper;
import com.junbo.fulfilment.spec.model.FulfilmentAction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * FulfilmentActionRepository.
 */
public class FulfilmentActionRepository {

    @Autowired
    private FulfilmentActionDao fulfilmentActionDao;

    public FulfilmentAction create(FulfilmentAction action) {
        action.setActionId(fulfilmentActionDao.create(Mapper.map(action)));
        return action;
    }

    public FulfilmentAction get(Long actionId) {
        return Mapper.map(fulfilmentActionDao.get(actionId));
    }

    public void update(Long actionId, String status, String result) {
        FulfilmentActionEntity entity = fulfilmentActionDao.get(actionId);

        entity.setStatus(FulfilmentStatus.valueOf(status));
        entity.setResult(result);

        fulfilmentActionDao.update(entity);
    }

    public List<FulfilmentAction> findByFulfilmentId(Long fulfilmentId) {
        List<FulfilmentActionEntity> fulfilmentActionEntities = fulfilmentActionDao.findByFulfilmentId(fulfilmentId);

        List<FulfilmentAction> actions = new ArrayList();
        for (FulfilmentActionEntity entity : fulfilmentActionEntities) {
            actions.add(Mapper.map(entity));
        }

        return actions;
    }
}
