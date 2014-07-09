/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.core.impl;

import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.common.error.AppErrorException;
import com.junbo.common.id.UserId;
import com.junbo.crypto.spec.model.CryptoMessage;
import com.junbo.crypto.spec.resource.CryptoResource;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import com.junbo.fulfilment.spec.resource.FulfilmentResource;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.option.model.UserGetOptions;
import com.junbo.identity.spec.v1.resource.UserResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.IdGenerator;
import com.junbo.token.common.CommonUtil;
import com.junbo.token.common.exception.AppClientExceptions;
import com.junbo.token.common.exception.AppServerExceptions;
import com.junbo.token.core.TokenService;
import com.junbo.token.core.mapper.ModelMapper;
import com.junbo.token.core.mapper.OrderWrapper;
import com.junbo.token.common.TokenUtil;
import com.junbo.token.db.repo.facade.TokenRepositoryFacade;
import com.junbo.token.spec.enums.*;
import com.junbo.token.spec.internal.TokenSet;
import com.junbo.token.spec.model.TokenRequest;
import com.junbo.token.spec.model.TokenConsumption;
import com.junbo.token.spec.model.TokenItem;
import com.junbo.token.spec.internal.TokenOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;


/**
 * token service implementation.
 */
public class TokenServiceImpl implements TokenService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);
    private static final Long MAX_QUANTITY = 100L;
    private static final String UNLIMIT_USE = "unlimited";

    private TokenRepositoryFacade tokenRepository;
    private CryptoResource cryptoResource;
    private OfferResource offerClient;
    private UserResource userClient;
    private FulfilmentResource fulfilmentClient;
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    @Override
    public Promise<TokenSet> createTokenSet(TokenSet request) {
        return Promise.pure(addSet(request));
    }

    @Override
    public Promise<TokenSet> getTokenSet(String tokenSetId) {
        return Promise.pure(tokenRepository.getTokenSet(tokenSetId));
    }

    @Override
    public Promise<TokenOrder> createTokenOrder(TokenOrder request) {
        return Promise.pure(addOrder(null, request));
    }

    @Override
    public Promise<TokenRequest> createOrderRequest(TokenRequest request) {
        OrderWrapper orderWrapper = ModelMapper.getOrderModel(request);
        TokenSet tokenSet = addSet(orderWrapper.getTokenSet());
        orderWrapper.getTokenOrder().setTokenSetId(tokenSet.getId());
        TokenOrder order = addOrder(tokenSet, orderWrapper.getTokenOrder());
        return Promise.pure(ModelMapper.getOrderRequest(tokenSet, order));
    }

    @Override
    public Promise<TokenOrder> getTokenOrder(String tokenOrderId) {
        return Promise.pure(tokenRepository.getTokenOrder(tokenOrderId));
    }

    @Override
    public Promise<TokenRequest> getOrderRequest(String tokenOrderId) {
        TokenOrder order = tokenRepository.getTokenOrder(tokenOrderId);
        if(order == null){
            throw AppClientExceptions.INSTANCE.resourceNotFound("orderId:" + tokenOrderId).exception();
        }
        TokenSet set = tokenRepository.getTokenSet(order.getTokenSetId());
        return Promise.pure(ModelMapper.getOrderRequest(set, order));
    }

    @Override
    public Promise<TokenConsumption> consumeToken(String token, TokenConsumption consumption) {
        validateTokenConsumption(consumption);
        //Require token string without encrypt
        //String decryptedToken = decrypt(token);
        Long hashValue = TokenUtil.computeHash(token).getHashValue();
        TokenItem item = tokenRepository.getTokenItem(hashValue);
        if(item == null){
            throw AppClientExceptions.INSTANCE.invalidToken().exception();
        }
        TokenOrder order= tokenRepository.getTokenOrder(item.getOrderId());
        if(order == null){
            throw AppServerExceptions.INSTANCE.InvalidTokenOrder(item.getOrderId().toString()).exception();
        }
        TokenSet tokenSet = tokenRepository.getTokenSet(order.getTokenSetId());
        if(tokenSet == null){
            throw AppServerExceptions.INSTANCE.InvalidTokenSet(order.getTokenSetId().toString()).exception();
        }
        validateTokenItem(item, order, tokenSet, consumption);
        updateTokenItem(item, order);
        consumption.setItemId(item.getId());
        TokenConsumption result = tokenRepository.addConsumption(consumption);
        FulfilmentRequest fulfilmentRequest = new FulfilmentRequest();
        fulfilmentRequest.setUserId(consumption.getUserId());
        fulfilmentRequest.setOrderId(idGenerator.nextId(consumption.getUserId()));
        fulfilmentRequest.setTrackingUuid(UUID.randomUUID().toString());
        FulfilmentItem fulfilItem = new FulfilmentItem();
        fulfilItem.setOfferId(consumption.getProduct());
        fulfilItem.setQuantity(1);
        fulfilItem.setItemReferenceId(item.getHashValue());
        fulfilmentRequest.setItems(Arrays.asList(fulfilItem));
        fulfilmentClient.fulfill(fulfilmentRequest).get();
        return Promise.pure(result);
    }

    @Override
    public Promise<TokenItem> updateToken(String tokenString, TokenItem token) {
        if(CommonUtil.isNullOrEmpty(token.getStatus())){
           throw AppClientExceptions.INSTANCE.missingField("status").exception();
        }
        try{
            ItemStatus.valueOf(token.getStatus());
        }catch(Exception ex){
            throw AppClientExceptions.INSTANCE.invalidTokenStatus(token.getStatus()).exception();
        }
        if(CommonUtil.isNullOrEmpty(token.getDisableReason())){
            throw AppClientExceptions.INSTANCE.missingField("disableReason").exception();
        }
        //Require token string without encryption
        //String decryptedToken = decrypt(tokenString);
        Long hashValue = TokenUtil.computeHash(tokenString).getHashValue();
        tokenRepository.updateTokenStatus(hashValue, ItemStatus.valueOf(token.getStatus()));
        return Promise.pure(token);
    }

    @Override
    public Promise<TokenItem> getToken(String token) {
        //Require token String without encrypt
        //String decryptedToken = decrypt(token);
        Long hashValue = TokenUtil.computeHash(token).getHashValue();
        TokenItem item = tokenRepository.getTokenItem(hashValue);
        if(item == null){
            throw AppClientExceptions.INSTANCE.resourceNotFound("token").exception();
        }
        item.setEncryptedString(token);
        return Promise.pure(item);
    }

    private TokenSet addSet(TokenSet request){
        validateTokenSet(request);
        request.setStatus(SetStatus.ACTIVE.toString());
        return tokenRepository.addTokenSet(request);
    }

    private TokenOrder addOrder(TokenSet tokenSet, TokenOrder request) {
        validateTokenOrder(request);
        request.setStatus(OrderStatus.COMPLETED.toString());
        if(request.getQuantity() == null){
            request.setQuantity((long)request.getTokenItems().size());
        }
        TokenOrder result = tokenRepository.addTokenOrder(request);
        if(tokenSet == null){
            tokenSet = tokenRepository.getTokenSet(request.getTokenSetId());
        }
        List<TokenItem> tokenItems = new ArrayList<TokenItem>();
        if(request.getCreateMethod().equalsIgnoreCase(CreateMethod.GENERATION.toString())){
            if(CommonUtil.isNullOrEmpty(tokenSet.getGenerationLength())){
                throw AppServerExceptions.INSTANCE.InvalidTokenSet(request.getTokenSetId().toString()).exception();
            }
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
                tokenItem.setEncryptedString(encrypt(item));
                tokenItems.add(tokenItem);
            }
        }else if(request.getCreateMethod().equalsIgnoreCase(CreateMethod.UPLOAD.toString())){
            for(TokenItem item : request.getTokenItems()){
                TokenItem tokenItem = new TokenItem();
                tokenItem.setOrderId(result.getId());
                tokenItem.setStatus(CommonUtil.toBool(request.getActivation())
                        ? ItemStatus.ACTIVATED.toString() : ItemStatus.DEACTIVATED.toString());
                String itemString = item.getEncryptedString();
                tokenItem.setHashValue(TokenUtil.computeHash(decrypt(itemString)).getHashValue());
                tokenItems.add(tokenItem);
            }
        }
        tokenRepository.addTokenItems(tokenItems);
        result.setTokenItems(tokenItems);
        return result;
    }

    private void validateTokenSet(TokenSet request){
        if(CommonUtil.isNullOrEmpty(request.getProductType())){
            throw AppClientExceptions.INSTANCE.missingField("productType").exception();
        }
        if(request.getProductDetail() == null){
            throw AppClientExceptions.INSTANCE.missingField("productDetail").exception();
        }
        if(request.getProductType().equalsIgnoreCase(ProductType.OFFER.toString())){
            if(CommonUtil.isNullOrEmpty(request.getProductDetail().getDefaultOffer())){
                throw AppClientExceptions.INSTANCE.missingField("defaultOffer").exception();
            }
            validateOffer(request.getProductDetail().getDefaultOffer());
        }else if(request.getProductType().equalsIgnoreCase(ProductType.PROMOTION.toString())){
            if(CommonUtil.isNullOrEmpty(request.getProductDetail().getDefaultPromotion())){
                throw AppClientExceptions.INSTANCE.missingField("defaultPromotion").exception();
            }
            //TODO: validate Promotion
        }else{
            throw AppClientExceptions.INSTANCE.invalidField("productType").exception();
        }
    }

    private void validateOffer(String offerId){
        Offer offer = null;
        try{
            offer = offerClient.getOffer(offerId).get();
        }catch(Exception ex){
            if(ex instanceof AppErrorException && ((AppErrorException) ex).getError().getHttpStatusCode() == 404){
                throw AppClientExceptions.INSTANCE.invalidProduct(offerId).exception();
            }else{
                LOGGER.error("error get catalog:" + ex.toString());
                throw AppServerExceptions.INSTANCE.catalogGatewayException().exception();
            }
        }
        if(offer == null){
            throw AppClientExceptions.INSTANCE.invalidProduct(offerId).exception();
        }
    }

    private void validateTokenOrder(TokenOrder request){
        if(CommonUtil.isNullOrEmpty(request.getActivation())){
            throw AppClientExceptions.INSTANCE.missingField("activation").exception();
        }
        if(CommonUtil.isNullOrEmpty(request.getCreateMethod())){
            throw AppClientExceptions.INSTANCE.missingField("creationMethod").exception();
        }
        if(request.getTokenSetId() == null){
            throw AppClientExceptions.INSTANCE.missingField("tokenSet").exception();
        }
        if(request.getCreateMethod().equalsIgnoreCase(CreateMethod.GENERATION.toString())){
            if(request.getQuantity() == null){
                throw AppClientExceptions.INSTANCE.missingField("quantity").exception();
            }
            if(request.getQuantity() < 0 || request.getQuantity() > MAX_QUANTITY){
                throw AppClientExceptions.INSTANCE.invalidField("quantity").exception();
            }
            if(request.getTokenItems() != null && !request.getTokenItems().isEmpty()){
                throw AppClientExceptions.INSTANCE.fieldNotNeeded("tokenItems").exception();
            }
        }else if(request.getCreateMethod().equalsIgnoreCase(CreateMethod.UPLOAD.toString())){
            if(request.getQuantity() != null){
                throw AppClientExceptions.INSTANCE.fieldNotNeeded("quantity").exception();
            }
            if(request.getTokenItems() == null || request.getTokenItems().isEmpty()){
                throw AppClientExceptions.INSTANCE.missingField("tokenItems").exception();
            }
        }else{
            throw AppClientExceptions.INSTANCE.invalidField("createMethod").exception();
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

    private void validateTokenConsumption(TokenConsumption consumption){
        if(consumption.getUserId() == null){
            throw AppClientExceptions.INSTANCE.missingField("user_id").exception();
        }
        User user = userClient.get(new UserId(consumption.getUserId()), new UserGetOptions()).get();
        if(user == null){
            throw AppClientExceptions.INSTANCE.invalidField("userId").exception();
        }
        if(consumption.getProduct() == null){
            throw AppClientExceptions.INSTANCE.missingField("product").exception();
        }
    }

    private void validateTokenItem(TokenItem item, TokenOrder order, TokenSet set, TokenConsumption consumption){
        if(!item.getStatus().equalsIgnoreCase(ItemStatus.ACTIVATED.toString())){
            throw AppClientExceptions.INSTANCE.invalidTokenStatus(item.getStatus()).exception();
        }
        if(order.getExpiredTime() != null && order.getExpiredTime().before(new Date())){
            throw AppClientExceptions.INSTANCE.tokenExpired().exception();
        }
        if(set.getProductType().equalsIgnoreCase(ProductType.OFFER.toString())){
            if(!set.getProductDetail().getDefaultOffer().equalsIgnoreCase(consumption.getProduct()) &&
                    !set.getProductDetail().getOptionalOffers().contains(consumption.getProduct())){
                throw AppClientExceptions.INSTANCE.invalidProduct(consumption.getProduct().toString()).exception();
            }
        }else if(set.getProductType().equalsIgnoreCase(ProductType.PROMOTION.toString())){
            if(!set.getProductDetail().getDefaultPromotion().equalsIgnoreCase(consumption.getProduct()) &&
                    !set.getProductDetail().getOptionalPromotion().contains(consumption.getProduct())){
                throw AppClientExceptions.INSTANCE.invalidProduct(consumption.getProduct().toString()).exception();
            }
        }
        if(!order.getUsageLimit().equalsIgnoreCase(UNLIMIT_USE)){
            Long usageLimit = TokenUtil.getUsage(order.getUsageLimit());
            if(item.getTokenConsumptions().size() > usageLimit){
                throw AppClientExceptions.INSTANCE.exceedTokenUsageLimit().exception();
            }
        }
    }

    private void updateTokenItem(TokenItem item, TokenOrder order){
        if(!order.getUsageLimit().equalsIgnoreCase(UNLIMIT_USE)){
            Long usageLimit = TokenUtil.getUsage(order.getUsageLimit());
            if(usageLimit <= item.getTokenConsumptions().size() + 1){
                tokenRepository.updateTokenStatus(item.getHashValue(), ItemStatus.USED);
                item.setStatus(ItemStatus.USED.toString());
            }
        }
    }

    private String encrypt(String data){
        CryptoMessage msg = new CryptoMessage();
        msg.setValue(data);
        return cryptoResource.encrypt(msg).get().getValue();
    }

    private String decrypt(String data){
        CryptoMessage msg = new CryptoMessage();
        msg.setValue(data);
        return cryptoResource.decrypt(msg).get().getValue();
    }

    @Required
    public void setTokenRepository(TokenRepositoryFacade tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public CryptoResource getCryptoResource() {
        return cryptoResource;
    }

    public void setCryptoResource(CryptoResource cryptoResource) {
        this.cryptoResource = cryptoResource;
    }

    public OfferResource getOfferClient() {
        return offerClient;
    }

    public void setOfferClient(OfferResource offerClient) {
        this.offerClient = offerClient;
    }

    public UserResource getUserClient() {
        return userClient;
    }

    public void setUserClient(UserResource userClient) {
        this.userClient = userClient;
    }

    public FulfilmentResource getFulfilmentClient() {
        return fulfilmentClient;
    }

    public void setFulfilmentClient(FulfilmentResource fulfilmentClient) {
        this.fulfilmentClient = fulfilmentClient;
    }
}
