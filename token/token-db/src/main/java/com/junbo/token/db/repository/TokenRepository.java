/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repository;

import com.junbo.oom.core.MappingContext;
import com.junbo.token.common.TokenUtil;
import com.junbo.token.db.dao.*;
import com.junbo.token.db.entity.*;
import com.junbo.token.db.mapper.TokenMapper;
import com.junbo.token.spec.enums.ItemStatus;
import com.junbo.token.spec.enums.ProductType;
import com.junbo.token.spec.internal.TokenSet;
import com.junbo.token.spec.internal.TokenOrder;
import com.junbo.token.spec.model.TokenConsumption;
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
            setOfferEntity.setProductType(TokenUtil.getEnumValue(ProductType.class, tokenSet.getProductType()));
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
            tokenSet.setProductType(setOfferEntity.getProductType().toString());
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
            //TODO: use hashValue as PK first. Need to consider partitionable-id
            itemEntity.setId(itemEntity.getHashValue());
            Long itemEntityId = tokenItemDao.save(itemEntity);
        }
        return items;
    }

    public TokenItem getTokenItem(Long hashValue){
        TokenItem item = tokenMapper.toTokenItem(tokenItemDao.getByHashValue(hashValue), new MappingContext());
        if(item == null){
            return null;
        }
        item.setTokenConsumptions(getTokenConsumption(item.getId()));
        return item;
    }

    public TokenConsumption addConsumption(TokenConsumption consumption){
        TokenConsumptionEntity entity = tokenMapper.toTokenConsumptionEntity(
                consumption, new MappingContext());
        Long consumptionId = tokenConsumptionDao.save(entity);
        consumption.setId(consumptionId);
        return consumption;
    }

    public List<TokenConsumption> getTokenConsumption(Long itemId){
        List<TokenConsumption> consumptions = new ArrayList<TokenConsumption>();
        for(TokenConsumptionEntity entity : tokenConsumptionDao.getByTokenItemId(itemId)){
            if(entity != null){
                consumptions.add(tokenMapper.toTokenConsumption(entity, new MappingContext()));
            }
        }
        return consumptions;
    }

    public void updateTokenStatus(long hashValue, ItemStatus status){
        TokenItemEntity entity = tokenItemDao.getByHashValue(hashValue);
        entity.setStatus(status);
        tokenItemDao.update(entity);
    }

}
