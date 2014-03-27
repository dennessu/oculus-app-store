/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repository;

import com.junbo.oom.core.MappingContext;
import com.junbo.token.db.dao.*;
import com.junbo.token.db.entity.TokenItemEntity;
import com.junbo.token.db.entity.TokenOrderEntity;
import com.junbo.token.db.entity.TokenSetEntity;
import com.junbo.token.db.entity.TokenSetOfferEntity;
import com.junbo.token.db.mapper.TokenMapper;
import com.junbo.token.spec.internal.TokenSet;
import com.junbo.token.spec.internal.TokenOrder;
import com.junbo.token.spec.model.TokenItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Token Repository.
 */
public class TokenRepository {
    @Autowired
    private TokenSetDao tokenSetDao;
    @Autowired
    private TokenOrderDao tokenOrderDao;
    @Autowired
    private TokenItemDao tokenItemDao;
    @Autowired
    private TokenSetOfferDao tokenSetOfferDao;
    @Autowired
    private TokenConsumptionDao tokenConsumptionDao;
    @Autowired
    private TokenMapper tokenMapper;

    public TokenSet addTokenSet(TokenSet tokenSet){
        TokenSetEntity entity = tokenMapper.toTokenSetEntity(tokenSet, new MappingContext());
        Long setId = tokenSetDao.save(entity);
        for(Long offerId : tokenSet.getOfferIds()){
            TokenSetOfferEntity setOfferEntity = new TokenSetOfferEntity();
            setOfferEntity.setProductId(offerId);
            setOfferEntity.setTokenSetId(setId);
            setOfferEntity.setProductType(tokenSet.getOfferType());
            tokenSetOfferDao.save(setOfferEntity);
        }
        tokenSet.setId(setId);
        return tokenSet;
    }

    public TokenSet getTokenSet(Long tokenSetId){
        TokenSetEntity entity = tokenSetDao.get(tokenSetId);
        TokenSet tokenSet = tokenMapper.toTokenSet(entity, new MappingContext());
        tokenSet.setOfferIds(new ArrayList<Long>());
        for(TokenSetOfferEntity setOfferEntity : tokenSetOfferDao.getByTokenSetId(tokenSetId)){
            tokenSet.getOfferIds().add(setOfferEntity.getProductId());
            tokenSet.setOfferType(setOfferEntity.getProductType());
        }
        return tokenSet;
    }

    public TokenOrder addTokenOrder(TokenOrder order){
        TokenOrderEntity entity = tokenMapper.toTokenOrderEntity(order, new MappingContext());
        Long orderId = tokenOrderDao.save(entity);
        order.setId(orderId);
        return order;
    }

    public TokenOrder getTokenOrder(Long orderId){
        TokenOrderEntity entity = tokenOrderDao.get(orderId);
        return tokenMapper.toTokenOrder(entity, new MappingContext());
    }

    public List<TokenItem> addTokenItems(List<TokenItem> items){
        for(TokenItem item : items){
            TokenItemEntity itemEntity = tokenMapper.toTokenItemEntity(item, new MappingContext());
            Long itemEntityId = tokenItemDao.save(itemEntity);
        }
        return items;
    }

}
