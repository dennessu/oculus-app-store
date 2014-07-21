/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.mapper;

import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.MappingContext;
import com.junbo.token.db.entity.*;
import com.junbo.token.spec.internal.TokenSet;
import com.junbo.token.spec.internal.TokenSetOffer;
import com.junbo.token.spec.model.TokenConsumption;
import com.junbo.token.spec.model.TokenItem;
import com.junbo.token.spec.internal.TokenOrder;

/**
 * oom mapper model.
 */
@Mapper(uses = {
        CommonMapper.class,
})
public interface TokenMapper {
    TokenSetEntity toTokenSetEntity(TokenSet set, MappingContext context);
    TokenSet toTokenSet(TokenSetEntity tokenSet, MappingContext context);

    TokenOrderEntity toTokenOrderEntity(TokenOrder order, MappingContext context);
    TokenOrder toTokenOrder(TokenOrderEntity tokenSet, MappingContext context);

    TokenItemEntity toTokenItemEntity(TokenItem tokenItem, MappingContext context);
    TokenItem toTokenItem(TokenItemEntity tokenItem, MappingContext context);

    TokenConsumptionEntity toTokenConsumptionEntity(TokenConsumption tokenConsumption, MappingContext context);
    TokenConsumption toTokenConsumption(TokenConsumptionEntity tokenConsumption, MappingContext context);

    TokenSetOfferEntity toTokenSetOfferEntity(TokenSetOffer tokenSetOffer, MappingContext context);
    TokenSetOffer toTokenSetOffer(TokenSetOfferEntity tokenSetOfferEntity, MappingContext context);
}
