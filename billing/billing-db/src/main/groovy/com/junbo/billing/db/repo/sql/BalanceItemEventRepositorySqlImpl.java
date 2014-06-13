/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo.sql;

import com.junbo.billing.db.dao.BalanceItemEventEntityDao;
import com.junbo.billing.db.entity.BalanceItemEventEntity;
import com.junbo.billing.db.mapper.ModelMapper;
import com.junbo.billing.db.repo.BalanceItemEventRepository;
import com.junbo.billing.spec.model.BalanceItemEvent;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;

import java.util.Date;

/**
 * Created by haomin on 14-6-9.
 */
public class BalanceItemEventRepositorySqlImpl implements BalanceItemEventRepository {
    private BalanceItemEventEntityDao balanceItemEventEntityDao;
    private ModelMapper modelMapper;
    private IdGenerator idGenerator;

    @Required
    public void setBalanceItemEventEntityDao(BalanceItemEventEntityDao balanceItemEventEntityDao) {
        this.balanceItemEventEntityDao = balanceItemEventEntityDao;
    }

    @Required
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Required
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Promise<BalanceItemEvent> get(Long id) {
        BalanceItemEventEntity entity = balanceItemEventEntityDao.get(id);
        if (entity != null) {
            return Promise.pure(modelMapper.toBalanceItemEvent(entity, new MappingContext()));
        }

        return Promise.pure(null);
    }

    @Override
    public Promise<BalanceItemEvent> create(BalanceItemEvent model) {
        if (model.getId() == null) {
            model.setId(idGenerator.nextId(model.getBalanceItemId()));
        }
        BalanceItemEventEntity entity = modelMapper.toBalanceItemEventEntity(model, new MappingContext());
        BalanceItemEventEntity saved = balanceItemEventEntityDao.save(entity);
        return get(saved.getEventId());
    }

    @Override
    public Promise<BalanceItemEvent> update(BalanceItemEvent model) {
        BalanceItemEventEntity entity = modelMapper.toBalanceItemEventEntity(model, new MappingContext());
        entity.setEventDate(new Date());
        balanceItemEventEntityDao.update(entity);
        return get(entity.getEventId());
    }

    @Override
    public Promise<Void> delete(Long id) {
        throw new UnsupportedOperationException("Delete on balance item event not support");
    }
}
