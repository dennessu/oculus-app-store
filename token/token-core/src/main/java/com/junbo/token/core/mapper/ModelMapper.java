/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.core.mapper;

import com.junbo.token.spec.internal.TokenOrder;
import com.junbo.token.spec.internal.TokenSet;
import com.junbo.token.spec.model.OrderRequest;

/**
 * Model mapper: map from API to Service.
 */
public final class ModelMapper {
    private ModelMapper(){

    }

    public static OrderWrapper getOrderModel(OrderRequest request){
        TokenSet tokenSet = new TokenSet();
        tokenSet.setOfferType(request.getOfferType());
        tokenSet.setDescription(request.getDescription());
        tokenSet.setGenerationLength(request.getGenerationLength());
        tokenSet.setGenerationSeed(request.getGenerationSeed());
        tokenSet.setOfferIds(request.getOfferIds());
        TokenOrder tokenOrder = new TokenOrder();
        tokenOrder.setActivation(request.getActivation());
        tokenOrder.setCreateMethod(request.getCreateMethod());
        tokenOrder.setDescription(request.getDescription());
        tokenOrder.setExpiredTime(request.getExpiredTime());
        tokenOrder.setQuantity(request.getQuantity());
        tokenOrder.setTokenItems(request.getTokenItems());
        tokenOrder.setUsageLimit(request.getUsageLimit());
        return new OrderWrapper(tokenSet, tokenOrder);
    }

    public static OrderRequest getOrderRequest(TokenSet tokenSet, TokenOrder tokenOrder){
        OrderRequest request = new OrderRequest();
        request.setOfferType(tokenSet.getOfferType());
        request.setDescription(tokenSet.getDescription());
        request.setGenerationLength(tokenSet.getGenerationLength());
        request.setGenerationSeed(tokenSet.getGenerationSeed());
        request.setOfferIds(tokenSet.getOfferIds());
        request.setActivation(tokenOrder.getActivation());
        request.setCreateMethod(tokenOrder.getCreateMethod());
        request.setDescription(tokenOrder.getDescription());
        request.setExpiredTime(tokenOrder.getExpiredTime());
        request.setQuantity(tokenOrder.getQuantity());
        request.setTokenItems(tokenOrder.getTokenItems());
        request.setUsageLimit(tokenOrder.getUsageLimit());
        return request;
    }
}
