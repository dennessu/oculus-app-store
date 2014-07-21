/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repo.sql;

import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import com.junbo.token.db.dao.TokenSetOfferDao;
import com.junbo.token.db.entity.TokenSetOfferEntity;
import com.junbo.token.db.mapper.TokenMapper;
import com.junbo.token.db.repo.TokenSetOfferRepository;
import com.junbo.token.spec.internal.TokenSetOffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-7-3.
 */
public class TokenSetOfferRepositorySqlImpl implements TokenSetOfferRepository{
    private TokenMapper tokenMapper;
    private TokenSetOfferDao tokenSetOfferDao;
    @Override
    public Promise<TokenSetOffer> get(String id) {
        TokenSetOfferEntity entity = tokenSetOfferDao.get(id);
        if(entity != null){
            return Promise.pure(tokenMapper.toTokenSetOffer(entity, new MappingContext()));
        }
        return Promise.pure(null);
    }

    @Override
    public Promise<TokenSetOffer> create(TokenSetOffer model) {
        TokenSetOfferEntity entity = tokenMapper.toTokenSetOfferEntity(model, new MappingContext());
        String savedId = tokenSetOfferDao.save(entity);
        return get(savedId);
    }

    @Override
    public Promise<TokenSetOffer> update(TokenSetOffer model, TokenSetOffer oldModel) {
        TokenSetOfferEntity entity = tokenMapper.toTokenSetOfferEntity(model, new MappingContext());
        TokenSetOfferEntity updated = tokenSetOfferDao.update(entity);
        return get(updated.getId());
    }

    @Override
    public Promise<Void> delete(String id) {
        throw new UnsupportedOperationException("Delete not support");
    }

    public void setTokenSetOfferDao(TokenSetOfferDao tokenSetOfferDao) {
        this.tokenSetOfferDao = tokenSetOfferDao;
    }

    public void setTokenMapper(TokenMapper tokenMapper) {
        this.tokenMapper = tokenMapper;
    }

    @Override
    public Promise<List<TokenSetOffer>> getByTokenSetId(String tokenSetId) {
        List<TokenSetOffer> results = new ArrayList<TokenSetOffer>();
        for(TokenSetOfferEntity entity : tokenSetOfferDao.getByTokenSetId(tokenSetId)){
            results.add(tokenMapper.toTokenSetOffer(entity, new MappingContext()));
        }
        return Promise.pure(results);
    }
}
