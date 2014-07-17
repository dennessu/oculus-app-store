/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repo.sql;

import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import com.junbo.token.db.dao.TokenSetDao;
import com.junbo.token.db.entity.TokenSetEntity;
import com.junbo.token.db.mapper.TokenMapper;
import com.junbo.token.db.repo.TokenSetRepository;
import com.junbo.token.spec.internal.TokenSet;

/**
 * Created by Administrator on 14-7-3.
 */
public class TokenSetRepositorySqlImpl implements TokenSetRepository{
    private TokenMapper tokenMapper;
    private TokenSetDao tokenSetDao;
    @Override
    public Promise<TokenSet> get(String id) {
        TokenSetEntity entity = tokenSetDao.get(id);
        if(entity != null){
            return Promise.pure(tokenMapper.toTokenSet(entity, new MappingContext()));
        }
        return Promise.pure(null);
    }

    @Override
    public Promise<TokenSet> create(TokenSet model) {
        TokenSetEntity entity = tokenMapper.toTokenSetEntity(model, new MappingContext());
        String savedId = tokenSetDao.save(entity);
        return get(savedId);
    }

    @Override
    public Promise<TokenSet> update(TokenSet model, TokenSet oldModel) {
        TokenSetEntity entity = tokenMapper.toTokenSetEntity(model, new MappingContext());
        TokenSetEntity updated = tokenSetDao.update(entity);
        return get(updated.getId());
    }

    @Override
    public Promise<Void> delete(String id) {
        throw new UnsupportedOperationException("Delete not support");
    }
    public void setTokenSetDao(TokenSetDao tokenSetDao) {
        this.tokenSetDao = tokenSetDao;
    }

    public void setTokenMapper(TokenMapper tokenMapper) {
        this.tokenMapper = tokenMapper;
    }
}
