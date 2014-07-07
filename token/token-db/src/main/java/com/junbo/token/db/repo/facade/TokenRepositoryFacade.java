/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repo.facade;

import com.junbo.sharding.IdGenerator;
import com.junbo.token.common.TokenUtil;
import com.junbo.token.db.repo.*;
import com.junbo.token.spec.enums.ItemStatus;
import com.junbo.token.spec.enums.ProductType;
import com.junbo.token.spec.internal.TokenOrder;
import com.junbo.token.spec.internal.TokenSet;
import com.junbo.token.spec.internal.TokenSetOffer;
import com.junbo.token.spec.model.ProductDetail;
import com.junbo.token.spec.model.TokenConsumption;
import com.junbo.token.spec.model.TokenItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-7-3.
 */
public class TokenRepositoryFacade {
    private TokenConsumptionRepository tokenConsumptionRepository;
    private TokenItemRepository tokenItemRepository;
    private TokenOrderRepository tokenOrderRepository;
    private TokenSetOfferRepository tokenSetOfferRepository;
    private TokenSetRepository tokenSetRepository;
    @Autowired
    @Qualifier("oculus48IdGenerator")
    protected IdGenerator idGenerator;

    public TokenSet addTokenSet(TokenSet tokenSet){
        TokenSet saved = tokenSetRepository.create(tokenSet).get();
        String setId = saved.getId();
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

    private void addTokenSetOffer(String setId, ProductType productType, String offerId, boolean isDefault) {
        TokenSetOffer model = new TokenSetOffer();
        model.setProductId(offerId);
        model.setTokenSetId(setId);
        model.setProductType(productType.toString());
        model.setIsDefault(isDefault);
        tokenSetOfferRepository.create(model).get();
    }

    public TokenSet getTokenSet(String tokenSetId){
        TokenSet tokenSet = tokenSetRepository.get(tokenSetId).get();
        //tokenSet.setOfferIds(new ArrayList<Long>());
        ProductDetail productDetail = new ProductDetail();
        String defaultProduct = null;
        List<String> optionalProducts = new ArrayList<String>();
        ProductType productType = null;
        for(TokenSetOffer setOffer : tokenSetOfferRepository.getByTokenSetId(tokenSetId).get()){
            productType = ProductType.valueOf(setOffer.getProductType());
            if(setOffer.getIsDefault()){
                defaultProduct = setOffer.getProductId();
            }else{
                optionalProducts.add(setOffer.getProductId());
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
        TokenOrder saved = tokenOrderRepository.create(order).get();
        order.setId(saved.getId());
        return order;
    }

    public TokenOrder getTokenOrder(String orderId){
        return tokenOrderRepository.get(orderId).get();
    }

    public List<TokenItem> addTokenItems(List<TokenItem> items){
        for(TokenItem item : items){
            //TODO: use new generated PK first. Need to consider partitionable-id
            item.setId(String.valueOf(idGenerator.nextId()));
            tokenItemRepository.create(item).get();
        }
        return items;
    }

    public TokenItem getTokenItem(Long hashValue){
        TokenItem item = tokenItemRepository.getByHashValue(hashValue).get();
        if(item == null){
            return null;
        }
        item.setTokenConsumptions(getTokenConsumption(item.getId()));
        return item;
    }

    public TokenConsumption addConsumption(TokenConsumption consumption){
        TokenConsumption saved = tokenConsumptionRepository.create(consumption).get();
        consumption.setId(saved.getId());
        return consumption;
    }

    public List<TokenConsumption> getTokenConsumption(String itemId){
        return tokenConsumptionRepository.getByTokenItemId(itemId).get();
    }

    public void updateTokenStatus(long hashValue, ItemStatus status){
        TokenItem item = tokenItemRepository.getByHashValue(hashValue).get();
        item.setStatus(status.toString());
        tokenItemRepository.update(item).get();
    }



    public void setTokenSetRepository(TokenSetRepository tokenSetRepository) {
        this.tokenSetRepository = tokenSetRepository;
    }

    public void setTokenConsumptionRepository(TokenConsumptionRepository tokenConsumptionRepository) {
        this.tokenConsumptionRepository = tokenConsumptionRepository;
    }

    public void setTokenItemRepository(TokenItemRepository tokenItemRepository) {
        this.tokenItemRepository = tokenItemRepository;
    }

    public void setTokenOrderRepository(TokenOrderRepository tokenOrderRepository) {
        this.tokenOrderRepository = tokenOrderRepository;
    }

    public void setTokenSetOfferRepository(TokenSetOfferRepository tokenSetOfferRepository) {
        this.tokenSetOfferRepository = tokenSetOfferRepository;
    }

}
