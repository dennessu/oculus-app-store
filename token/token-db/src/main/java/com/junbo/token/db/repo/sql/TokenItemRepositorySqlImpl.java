/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repo.sql;

import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import com.junbo.token.db.dao.TokenItemDao;
import com.junbo.token.db.entity.TokenItemEntity;
import com.junbo.token.db.mapper.TokenMapper;
import com.junbo.token.db.repo.TokenItemRepository;
import com.junbo.token.spec.model.TokenItem;

/**
 * Created by Administrator on 14-7-3.
 */
public class TokenItemRepositorySqlImpl implements TokenItemRepository{
    private TokenMapper tokenMapper;
    private TokenItemDao tokenItemDao;

    @Override
    public Promise<TokenItem> get(String id) {
        TokenItemEntity entity = tokenItemDao.get(id);
        if(entity != null){
            return Promise.pure(tokenMapper.toTokenItem(entity, new MappingContext()));
        }
        return Promise.pure(null);
    }

    @Override
    public Promise<TokenItem> create(TokenItem model) {
        TokenItemEntity entity = tokenMapper.toTokenItemEntity(model, new MappingContext());
        String savedId = tokenItemDao.save(entity);
        return get(savedId);
    }

    @Override
    public Promise<TokenItem> update(TokenItem model, TokenItem oldModel) {
        TokenItemEntity entity = tokenMapper.toTokenItemEntity(model, new MappingContext());
        TokenItemEntity updated = tokenItemDao.update(entity);
        return get(updated.getId());
    }

    @Override
    public Promise<Void> delete(String id) {
        throw new UnsupportedOperationException("Delete not support");
    }

    public void setTokenItemDao(TokenItemDao tokenItemDao) {
        this.tokenItemDao = tokenItemDao;
    }

    public void setTokenMapper(TokenMapper tokenMapper) {
        this.tokenMapper = tokenMapper;
    }

    @Override
    public Promise<TokenItem> getByHashValue(Long hashValue) {
        return Promise.pure(tokenMapper.toTokenItem(tokenItemDao.getByHashValue(hashValue), new MappingContext()));
    }
}
