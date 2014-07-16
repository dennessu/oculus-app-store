/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo.sql;

import com.junbo.billing.db.dao.BalanceEventEntityDao;
import com.junbo.billing.db.entity.BalanceEventEntity;
import com.junbo.billing.db.mapper.ModelMapper;
import com.junbo.billing.db.repo.BalanceEventRepository;
import com.junbo.billing.spec.model.BalanceEvent;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import org.springframework.beans.factory.annotation.Required;

import java.util.Date;

/**
 * Created by haomin on 14-6-9.
 */
public class BalanceEventRepositorySqlImpl implements BalanceEventRepository {
    private BalanceEventEntityDao balanceEventEntityDao;
    private ModelMapper modelMapper;

    @Required
    public void setBalanceEventEntityDao(BalanceEventEntityDao balanceEventEntityDao) {
        this.balanceEventEntityDao = balanceEventEntityDao;
    }

    @Required
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Promise<BalanceEvent> get(Long id) {
        BalanceEventEntity entity = balanceEventEntityDao.get(id);
        if (entity != null) {
            return Promise.pure(modelMapper.toBalanceEvent(entity, new MappingContext()));
        }

        return Promise.pure(null);
    }

    @Override
    public Promise<BalanceEvent> create(BalanceEvent model) {
        BalanceEventEntity entity = modelMapper.toBalanceEventEntity(model, new MappingContext());
        entity.setEventDate(new Date());
        BalanceEventEntity saved = balanceEventEntityDao.save(entity);
        return get(saved.getId());
    }

    @Override
    public Promise<BalanceEvent> update(BalanceEvent model, BalanceEvent oldModel) {
        BalanceEventEntity entity = modelMapper.toBalanceEventEntity(model, new MappingContext());
        BalanceEventEntity oldEntity = modelMapper.toBalanceEventEntity(oldModel, new MappingContext());
        entity.setEventDate(new Date());
        balanceEventEntityDao.update(entity, oldEntity);
        return get(entity.getId());
    }

    @Override
    public Promise<Void> delete(Long id) {
        throw new UnsupportedOperationException("Delete on balance event not support");
    }
}
