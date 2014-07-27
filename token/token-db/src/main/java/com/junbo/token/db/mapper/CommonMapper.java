/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.mapper;

import com.junbo.token.common.CommonUtil;
import com.junbo.token.spec.enums.*;



/**
 * common mapper for oom.
 */
public class CommonMapper {

    public Long fromStringToLong(String source) {
        return source == null ? null : Long.parseLong(source);
    }

    public String fromLongToString(Long source) {
        return source == null ? null : source.toString();
    }

    public CreateMethod toCreateMethodEnum(String createMethod) {
        return CreateMethod.valueOf(createMethod.toUpperCase());
    }

    public String toCreateMethod(CreateMethod createMethod) {
        return createMethod.toString();
    }

    public ItemStatus toItemStatusEnum(String status){
        return ItemStatus.valueOf(status.toUpperCase());
    }

    public String toItemStatus(ItemStatus status){
        return status.toString();
    }

    public OrderStatus toOrderStatusEnum(String status){
        return OrderStatus.valueOf(status.toUpperCase());
    }

    public String toOrderStatus(OrderStatus status){
        return status.toString();
    }

    public SetStatus toSetStatusEnum(String status){
        return SetStatus.valueOf(status.toUpperCase());
    }

    public String toSetStatus(SetStatus status){
        return status.toString();
    }

    public TokenLength toTokenLengthEnum(String length){
        if(CommonUtil.isNullOrEmpty(length)){
            return null;
        }
        return TokenLength.valueOf(length.toUpperCase());
    }

    public String toTokenLength(TokenLength length){
        return length == null ? null : length.toString();
    }

    public ProductType toProductTYpeEnum(String productType){
        return ProductType.valueOf(productType);
    }

    public String toProductType(ProductType productType){
        return productType.toString();
    }
}
