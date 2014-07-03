/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.core.mapper;

import com.junbo.token.spec.internal.TokenOrder;
import com.junbo.token.spec.internal.TokenSet;
import com.junbo.token.spec.model.TokenRequest;

/**
 * Model mapper: map from API to Service.
 */
public final class ModelMapper {
    private ModelMapper(){

    }

    public static OrderWrapper getOrderModel(TokenRequest request){
        TokenSet tokenSet = new TokenSet();
        tokenSet.setProductType(request.getProductType());
        tokenSet.setDescription(request.getDescription());
        tokenSet.setGenerationLength(request.getGenerationLength());
        tokenSet.setProductDetail(request.getProductDetail());
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

    public static TokenRequest getOrderRequest(TokenSet tokenSet, TokenOrder tokenOrder){
        TokenRequest request = new TokenRequest();
        request.setProductType(tokenSet.getProductType());
        request.setDescription(tokenSet.getDescription());
        request.setGenerationLength(tokenSet.getGenerationLength());
        request.setProductDetail(tokenSet.getProductDetail());
        request.setActivation(tokenOrder.getActivation());
        request.setCreateMethod(tokenOrder.getCreateMethod());
        request.setDescription(tokenOrder.getDescription());
        request.setExpiredTime(tokenOrder.getExpiredTime());
        request.setQuantity(tokenOrder.getQuantity());
        request.setTokenItems(tokenOrder.getTokenItems());
        request.setUsageLimit(tokenOrder.getUsageLimit());
        request.setId(tokenOrder.getId());
        request.setStatus(tokenOrder.getStatus());
        return request;
    }
}
