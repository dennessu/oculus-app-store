/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.core.impl;

import com.junbo.langur.core.promise.Promise;
import com.junbo.token.common.CommonUtil;
import com.junbo.token.common.exception.AppClientExceptions;
import com.junbo.token.core.TokenService;
import com.junbo.token.core.mapper.ModelMapper;
import com.junbo.token.core.mapper.OrderWrapper;
import com.junbo.token.common.TokenUtil;
import com.junbo.token.db.repository.TokenRepository;
import com.junbo.token.spec.enums.*;
import com.junbo.token.spec.internal.TokenSet;
import com.junbo.token.spec.model.OrderRequest;
import com.junbo.token.spec.model.TokenItem;
import com.junbo.token.spec.internal.TokenOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


/**
 * token service implementation.
 */
public class TokenServiceImpl implements TokenService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);
    private static final Long MAX_QUANTITY = 100L;
    private static final String UNLIMIT_USE = "unlimited";

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public Promise<TokenSet> createTokenSet(TokenSet request) {
        validateTokenSet(request);
        return Promise.pure(addSet(request));
    }

    @Override
    public Promise<TokenSet> getTokenSet(Long tokenSetId) {
        return Promise.pure(tokenRepository.getTokenSet(tokenSetId));
    }

    @Override
    public Promise<TokenOrder> createTokenOrder(TokenOrder request) {
        return Promise.pure(addOrder(request));
    }

    @Override
    public Promise<OrderRequest> createOrderRequest(OrderRequest request) {
        OrderWrapper orderWrapper = ModelMapper.getOrderModel(request);
        TokenSet tokenSet = addSet(orderWrapper.getTokenSet());
        orderWrapper.getTokenOrder().setTokenSetId(tokenSet.getId());
        TokenOrder order = addOrder(orderWrapper.getTokenOrder());
        return Promise.pure(ModelMapper.getOrderRequest(tokenSet, order));
    }

    @Override
    public Promise<TokenOrder> getTokenOrder(Long tokenOrderId) {
        return Promise.pure(tokenRepository.getTokenOrder(tokenOrderId));
    }

    @Override
    public Promise<OrderRequest> getOrderRequest(Long tokenOrderId) {
        TokenOrder order = tokenRepository.getTokenOrder(tokenOrderId);
        if(order == null){
            throw AppClientExceptions.INSTANCE.resourceNotFound("orderId:" + tokenOrderId).exception();
        }
        TokenSet set = tokenRepository.getTokenSet(order.getTokenSetId());
        return Promise.pure(ModelMapper.getOrderRequest(set, order));
    }

    @Override
    public Promise<TokenItem> consumeToken(String token) {
        return null;
    }

    @Override
    public Promise<TokenItem> updateToken(TokenItem token) {
        return null;
    }

    @Override
    public Promise<TokenItem> getToken(String token) {
        return null;
    }

    private TokenSet addSet(TokenSet request){
        request.setStatus(SetStatus.ACTIVE.toString());
        return tokenRepository.addTokenSet(request);
    }

    private TokenOrder addOrder(TokenOrder request) {
        validateTokenOrder(request);
        request.setStatus(OrderStatus.COMPLETED.toString());
        TokenOrder result = tokenRepository.addTokenOrder(request);
        TokenSet tokenSet = tokenRepository.getTokenSet(request.getTokenSetId());
        List<TokenItem> tokenItems = new ArrayList<TokenItem>();
        if(request.getCreateMethod().equalsIgnoreCase(CreateMethod.GENERATION.toString())){
            int genLen = tokenSet.getGenerationLength().equalsIgnoreCase(TokenLength.LEN16.toString()) ? 16 : (
                    tokenSet.getGenerationLength().equalsIgnoreCase(TokenLength.LEN20.toString()) ? 20 : (
                    tokenSet.getGenerationLength().equalsIgnoreCase(TokenLength.LEN25.toString()) ? 25 : 0)
                    );
            for(String item : TokenUtil.generateToken(genLen, request.getQuantity())){
                TokenItem tokenItem = new TokenItem();
                tokenItem.setOrderId(result.getId());
                tokenItem.setStatus(CommonUtil.toBool(request.getActivation())
                        ? ItemStatus.ACTIVATED.toString() : ItemStatus.DEACTIVATED.toString());
                tokenItem.setHashValue(TokenUtil.computeHash(item).getHashValue());
                tokenItem.setEncryptedString(CommonUtil.encrypt(item));
                tokenItems.add(tokenItem);
            }
        }else if(request.getCreateMethod().equalsIgnoreCase(CreateMethod.UPLOAD.toString())){
            for(TokenItem item : request.getTokenItems()){
                TokenItem tokenItem = new TokenItem();
                tokenItem.setOrderId(result.getId());
                tokenItem.setStatus(CommonUtil.toBool(request.getActivation())
                        ? ItemStatus.ACTIVATED.toString() : ItemStatus.DEACTIVATED.toString());
                String itemString = item.getEncryptedString();
                tokenItem.setHashValue(TokenUtil.computeHash(CommonUtil.decrypt(itemString)).getHashValue());
                tokenItems.add(tokenItem);
            }
        }
        tokenRepository.addTokenItems(tokenItems);
        result.setTokenItems(tokenItems);
        return result;
    }

    private void validateTokenSet(TokenSet request){
        if(CommonUtil.isNullOrEmpty(request.getGenerationLength())){
            throw AppClientExceptions.INSTANCE.missingField("generationLength").exception();
        }
        if(request.getOfferIds() == null || request.getOfferIds().isEmpty()){
            throw AppClientExceptions.INSTANCE.missingField("offers").exception();
        }
        if(CommonUtil.isNullOrEmpty(request.getProductType())){
            throw AppClientExceptions.INSTANCE.missingField("productType").exception();
        }
    }

    private void validateTokenOrder(TokenOrder request){
        if(CommonUtil.isNullOrEmpty(request.getCreateMethod())){
            throw AppClientExceptions.INSTANCE.missingField("creationMethod").exception();
        }
        if(CommonUtil.isNullOrEmpty(request.getActivation())){
            throw AppClientExceptions.INSTANCE.missingField("activation").exception();
        }
        if(request.getQuantity() == null){
            throw AppClientExceptions.INSTANCE.missingField("activation").exception();
        }
        if(request.getQuantity() < 0 || request.getQuantity() > MAX_QUANTITY){
            throw AppClientExceptions.INSTANCE.invalidField("quantity").exception();
        }
        if(request.getTokenSetId() == null){
            throw AppClientExceptions.INSTANCE.missingField("tokenSet").exception();
        }
        if(request.getUsageLimit() == null){
            throw AppClientExceptions.INSTANCE.missingField("usageLimit").exception();
        }
        if(!request.getUsageLimit().equalsIgnoreCase(UNLIMIT_USE)){
            Long usage = TokenUtil.getUsage(request.getUsageLimit());
            if(usage < 0){
                throw AppClientExceptions.INSTANCE.invalidField("usageLimit").exception();
            }
        }
    }
}
