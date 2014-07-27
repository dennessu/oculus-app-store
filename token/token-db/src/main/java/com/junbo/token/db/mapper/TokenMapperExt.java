/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.mapper;

import com.junbo.oom.core.MappingContext;
import com.junbo.token.db.entity.TokenItemEntity;
import com.junbo.token.spec.model.TokenItem;
import com.junbo.token.spec.model.TokenRequest;

/**
 * token mapper extention.
 */
public class TokenMapperExt extends TokenMapperImpl{

    @Override
    public TokenItemEntity toTokenItemEntity(TokenItem tokenItem, MappingContext context){
        TokenItemEntity entity = super.toTokenItemEntity(tokenItem, context);
        entity.setOrderId(tokenItem.getTokenRequest().getId());
        return entity;
    }

    @Override
    public TokenItem toTokenItem(TokenItemEntity tokenItem, MappingContext context){
        TokenItem item = super.toTokenItem(tokenItem, context);
        TokenRequest request = new TokenRequest();
        request.setId(tokenItem.getOrderId());
        item.setTokenRequest(request);
        return item;
    }

}
