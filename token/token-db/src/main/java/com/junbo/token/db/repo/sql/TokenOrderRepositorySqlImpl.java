/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repo.sql;

import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import com.junbo.token.db.dao.TokenOrderDao;
import com.junbo.token.db.entity.TokenOrderEntity;
import com.junbo.token.db.mapper.TokenMapper;
import com.junbo.token.db.repo.TokenOrderRepository;
import com.junbo.token.spec.internal.TokenOrder;

/**
 * Created by Administrator on 14-7-3.
 */
public class TokenOrderRepositorySqlImpl implements TokenOrderRepository{
    private TokenMapper tokenMapper;
    private TokenOrderDao tokenOrderDao;
    @Override
    public Promise<TokenOrder> get(String id) {
        TokenOrderEntity entity = tokenOrderDao.get(id);
        if(entity != null){
            return Promise.pure(tokenMapper.toTokenOrder(entity, new MappingContext()));
        }
        return Promise.pure(null);
    }

    @Override
    public Promise<TokenOrder> create(TokenOrder model) {
        TokenOrderEntity entity = tokenMapper.toTokenOrderEntity(model, new MappingContext());
        String savedId = tokenOrderDao.save(entity);
        return get(savedId);
    }

    @Override
    public Promise<TokenOrder> update(TokenOrder model) {
        TokenOrderEntity entity = tokenMapper.toTokenOrderEntity(model, new MappingContext());
        TokenOrderEntity updated = tokenOrderDao.update(entity);
        return get(updated.getId());
    }

    @Override
    public Promise<Void> delete(String id) {
        throw new UnsupportedOperationException("Delete not support");
    }
    public void setTokenOrderDao(TokenOrderDao tokenOrderDao) {
        this.tokenOrderDao = tokenOrderDao;
    }

    public void setTokenMapper(TokenMapper tokenMapper) {
        this.tokenMapper = tokenMapper;
    }
}
