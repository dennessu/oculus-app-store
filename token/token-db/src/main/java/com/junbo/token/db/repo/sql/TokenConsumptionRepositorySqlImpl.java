/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repo.sql;

import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import com.junbo.token.db.dao.TokenConsumptionDao;
import com.junbo.token.db.entity.TokenConsumptionEntity;
import com.junbo.token.db.mapper.TokenMapper;
import com.junbo.token.db.repo.TokenConsumptionRepository;
import com.junbo.token.spec.model.TokenConsumption;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-7-3.
 */
public class TokenConsumptionRepositorySqlImpl implements TokenConsumptionRepository{
    private TokenMapper tokenMapper;
    private TokenConsumptionDao tokenConsumptionDao;

    @Override
    public Promise<TokenConsumption> get(String id) {
        TokenConsumptionEntity entity = tokenConsumptionDao.get(id);
        if(entity != null){
            return Promise.pure(tokenMapper.toTokenConsumption(entity, new MappingContext()));
        }
        return Promise.pure(null);
    }

    @Override
    public Promise<TokenConsumption> create(TokenConsumption model) {
        TokenConsumptionEntity entity = tokenMapper.toTokenConsumptionEntity(model, new MappingContext());
        String savedId = tokenConsumptionDao.save(entity);
        return get(savedId);
    }

    @Override
    public Promise<TokenConsumption> update(TokenConsumption model, TokenConsumption oldModel) {
        TokenConsumptionEntity entity = tokenMapper.toTokenConsumptionEntity(model, new MappingContext());
        TokenConsumptionEntity updated = tokenConsumptionDao.update(entity);
        return get(updated.getId());
    }

    @Override
    public Promise<Void> delete(String id) {
        throw new UnsupportedOperationException("Delete not support");
    }

    public void setTokenConsumptionDao(TokenConsumptionDao tokenConsumptionDao) {
        this.tokenConsumptionDao = tokenConsumptionDao;
    }

    public void setTokenMapper(TokenMapper tokenMapper) {
        this.tokenMapper = tokenMapper;
    }

    @Override
    public Promise<List<TokenConsumption>> getByTokenItemId(String itemId) {
        List<TokenConsumption> results = new ArrayList<TokenConsumption>();
        for(TokenConsumptionEntity entity : tokenConsumptionDao.getByTokenItemId(itemId)){
            results.add(tokenMapper.toTokenConsumption(entity, new MappingContext()));
        }
        return Promise.pure(results);
    }
}
