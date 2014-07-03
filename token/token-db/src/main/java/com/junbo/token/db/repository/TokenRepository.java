/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repository;

import com.junbo.oom.core.MappingContext;
import com.junbo.sharding.IdGenerator;
import com.junbo.token.common.TokenUtil;
import com.junbo.token.db.dao.*;
import com.junbo.token.db.entity.*;
import com.junbo.token.db.mapper.TokenMapper;
import com.junbo.token.spec.enums.ItemStatus;
import com.junbo.token.spec.enums.ProductType;
import com.junbo.token.spec.internal.TokenSet;
import com.junbo.token.spec.internal.TokenOrder;
import com.junbo.token.spec.model.ProductDetail;
import com.junbo.token.spec.model.TokenConsumption;
import com.junbo.token.spec.model.TokenItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
    @Autowired
    @Qualifier("oculus48IdGenerator")
    protected IdGenerator idGenerator;

    public TokenSet addTokenSet(TokenSet tokenSet){
        TokenSetEntity entity = tokenMapper.toTokenSetEntity(tokenSet, new MappingContext());
        Long setId = tokenSetDao.save(entity);
        ProductType productType = TokenUtil.getEnumValue(ProductType.class, tokenSet.getProductType());
        if(productType.equals(ProductType.OFFER)){
            addTokenSetOffer(setId, productType, tokenSet.getProductDetail().getDefaultOffer(), true);
            if(tokenSet.getProductDetail().getOptionalOffers() != null){
                for(String offerId : tokenSet.getProductDetail().getOptionalOffers()){
                    addTokenSetOffer(setId, productType, offerId, false);
                }
            }
        }else if(productType.equals(ProductType.PROMOTION)){
            addTokenSetOffer(setId, productType, tokenSet.getProductDetail().getDefaultPromotion(), true);
            if(tokenSet.getProductDetail().getOptionalPromotion() != null){
                for(String offerId : tokenSet.getProductDetail().getOptionalPromotion()){
                    addTokenSetOffer(setId, productType, offerId, false);
                }
            }
        }

        tokenSet.setId(setId);
        return tokenSet;
    }

    private void addTokenSetOffer(Long setId, ProductType productType, String offerId, boolean isDefault) {
        TokenSetOfferEntity setOfferEntity = new TokenSetOfferEntity();
        setOfferEntity.setProductId(offerId);
        setOfferEntity.setTokenSetId(setId);
        setOfferEntity.setProductType(productType);
        setOfferEntity.setIsDefault(isDefault);
        tokenSetOfferDao.save(setOfferEntity);
    }

    public TokenSet getTokenSet(Long tokenSetId){
        TokenSetEntity entity = tokenSetDao.get(tokenSetId);
        TokenSet tokenSet = tokenMapper.toTokenSet(entity, new MappingContext());
        //tokenSet.setOfferIds(new ArrayList<Long>());
        ProductDetail productDetail = new ProductDetail();
        String defaultProduct = null;
        List<String> optionalProducts = new ArrayList<String>();
        ProductType productType = null;
        for(TokenSetOfferEntity setOfferEntity : tokenSetOfferDao.getByTokenSetId(tokenSetId)){
            productType = setOfferEntity.getProductType();
            if(setOfferEntity.getIsDefault()){
                defaultProduct = setOfferEntity.getProductId();
            }else{
                optionalProducts.add(setOfferEntity.getProductId());
            }
        }
        if(productType.equals(ProductType.OFFER)){
            productDetail.setDefaultOffer(defaultProduct);
            productDetail.setOptionalOffers(optionalProducts);
        }else if(productType.equals(ProductType.PROMOTION)){
            productDetail.setDefaultPromotion(defaultProduct);
            productDetail.setOptionalPromotion(optionalProducts);
        }
        tokenSet.setProductDetail(productDetail);
        tokenSet.setProductType(productType.toString());
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
            //TODO: use new generated PK first. Need to consider partitionable-id
            itemEntity.setId(idGenerator.nextId());
            tokenItemDao.save(itemEntity);
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
