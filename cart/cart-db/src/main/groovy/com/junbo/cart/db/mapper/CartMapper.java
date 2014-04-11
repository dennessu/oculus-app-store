/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.mapper;

import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.Mapping;
import com.junbo.oom.core.MappingContext;
import com.junbo.oom.core.Mappings;
import com.junbo.cart.db.entity.CartEntity;
import com.junbo.cart.db.entity.CouponItemEntity;
import com.junbo.cart.db.entity.OfferItemEntity;
import com.junbo.cart.spec.model.Cart;
import com.junbo.cart.spec.model.item.CouponItem;
import com.junbo.cart.spec.model.item.OfferItem;

/**
 * Created by fzhang@wan-san.com on 14-1-23.
 */
@Mapper(uses = {
        CommonMapper.class
})
public interface CartMapper {

    @Mappings({
            @Mapping(source = "userId", target = "user", bidirectional = false)
    })
    Cart toCartModel(CartEntity cartEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "user", target = "userId", bidirectional = false)
    })
    CartEntity toCartEntity(Cart cart, MappingContext context);

    @Mappings({
            @Mapping(source = "cartItemId", target = "id", bidirectional = false),
            @Mapping(source = "offerId", target = "offer", bidirectional = false)
    })
    OfferItem toOfferItemModel(OfferItemEntity offerItemEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "id", target = "cartItemId", bidirectional = false),
            @Mapping(source = "offer", target = "offerId", bidirectional = false)
    })
    OfferItemEntity toOfferItemEntity(OfferItem offerItem, MappingContext context);

    @Mappings({
            @Mapping(source = "cartItemId", target = "id", bidirectional = false)
    })
    CouponItem toCouponItemModel(CouponItemEntity couponItemEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "id", target = "cartItemId", bidirectional = false)
    })
    CouponItemEntity toCouponItemEntity(CouponItem couponItem, MappingContext context);
}
