/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.mapper;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.Mapping;
import com.junbo.oom.core.MappingContext;
import com.junbo.oom.core.Mappings;
import com.junbo.order.db.entity.*;
import com.junbo.order.spec.model.*;

/**
 * Created by linyi on 14-2-12.
 */

@Mapper(uses = {
        CommonMapper.class
})
public interface ModelMapper {

    @Mappings({
            @Mapping(source = "status", target = "orderStatusId", excluded = false, bidirectional = false),
            @Mapping(source = "user", target = "userId", excluded = false, bidirectional = false),
            @Mapping(source = "id", target = "orderId", excluded = false, bidirectional = false),
            @Mapping(source = "shippingAddress", target = "shippingAddressId", excluded = false, bidirectional = false),
            @Mapping(source = "shippingMethod", target = "shippingMethodId", excluded = false, bidirectional = false)
    })
    OrderEntity toOrderEntity(Order order, MappingContext context);

    @Mappings({
            @Mapping(source = "orderStatusId", target = "status", excluded = false, bidirectional = false),
            @Mapping(source = "userId", target = "user", excluded = false, bidirectional = false),
            @Mapping(source = "orderId", target = "id", excluded = false, bidirectional = false),
            @Mapping(source = "shippingAddressId", target = "shippingAddress", excluded = false, bidirectional = false),
            @Mapping(source = "shippingMethodId", target = "shippingMethod", excluded = false, bidirectional = false)
    })
    Order toOrderModel(OrderEntity orderEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "type", target = "orderItemType", excluded = false, bidirectional = false),
            @Mapping(source = "offer", target = "productItemId", excluded = false, bidirectional = false),
    })
    OrderItemEntity toOrderItemEntity(OrderItem orderItem, MappingContext context);

    @Mappings({
            @Mapping(source = "orderItemType", target = "type", excluded = false, bidirectional = false),
            @Mapping(source = "productItemId", target = "offer", excluded = false, bidirectional = false),
    })
    OrderItem toOrderItemModel(OrderItemEntity orderItemEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "promotion", target = "promotionId", excluded = false, bidirectional = false)
    })
    OrderDiscountInfoEntity toDiscountEntity(Discount discount, MappingContext context);

    @Mappings({
            @Mapping(source = "promotionId", target = "promotion", excluded = false, bidirectional = false)
    })
    Discount toDiscountModel(OrderDiscountInfoEntity discountEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "action", target = "actionId", excluded = false, bidirectional = false),
            @Mapping(source = "status", target = "statusId", excluded = false, bidirectional = false),
            @Mapping(source = "order", target = "orderId", excluded = false, bidirectional = false)
    })
    OrderEventEntity toOrderEventEntity(OrderEvent orderEvent, MappingContext context);

    @Mappings({
            @Mapping(source = "actionId", target = "action", excluded = false, bidirectional = false),
            @Mapping(source = "statusId", target = "status", excluded = false, bidirectional = false),
            @Mapping(source = "orderId", target = "order", excluded = false, bidirectional = false),
    })
    OrderEvent toOrderEventModel(OrderEventEntity orderEventEntity, MappingContext context);

    OrderPaymentInfoEntity toOrderPaymentInfoEntity(PaymentInstrumentId paymentInstrumentId, MappingContext context);
    PaymentInstrumentId toPaymentInstrumentId(OrderPaymentInfoEntity orderPaymentInfoEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "id", target = "eventId", excluded = false, bidirectional = false),
            @Mapping(source = "orderItem", target = "orderItemId", excluded = false, bidirectional = false)
    })
    OrderItemFulfillmentEventEntity toOrderItemFulfillmentEventEntity(FulfillmentEvent fulfillmentEvent,
                                                                      MappingContext context);

    @Mappings({
            @Mapping(source = "eventId", target = "id", excluded = false, bidirectional = false),
            @Mapping(source = "orderItemId", target = "orderItem", excluded = false, bidirectional = false)

    })
    FulfillmentEvent toFulfillmentEventModel(OrderItemFulfillmentEventEntity orderItemFulfillmentEventEntity,
                                             MappingContext context);

    OrderBillingEventEntity toOrderBillingEventEntity(BillingEvent billingEvent,
                                                                      MappingContext context);

    BillingEvent toOrderBillingEventModel(OrderBillingEventEntity orderBillingEventEntity,
                                             MappingContext context);
    @Mappings({
            @Mapping(source = "orderItemPreorderInfoId", target = "preorderInfoId",
                    excluded = false, bidirectional = false),
            @Mapping(source = "billingDate", target = "billingTime", excluded = false, bidirectional = false),
            @Mapping(source = "preNotificationDate", target = "preNotificationTime",
                    excluded = false, bidirectional = false),
            @Mapping(source = "releaseDate", target = "releaseTime", excluded = false, bidirectional = false),
    })
    PreorderInfo toPreOrderInfoModel(OrderItemPreorderInfoEntity orderItemPreorderInfoEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "preorderInfoId", target = "orderItemPreorderInfoId",
                    excluded = false, bidirectional = false),
            @Mapping(source = "billingTime", target = "billingDate", excluded = false, bidirectional = false),
            @Mapping(source = "preNotificationTime", target = "preNotificationDate",
                    excluded = false, bidirectional = false),
            @Mapping(source = "releaseTime", target = "releaseDate", excluded = false, bidirectional = false),
    })
    OrderItemPreorderInfoEntity toOrderItemPreorderInfoEntity(PreorderInfo preorderInfo, MappingContext context);

    @Mappings({
            @Mapping(source = "updateTypeId", target = "updatedType", excluded = false, bidirectional = false),
            @Mapping(source = "updateBeforeValue", target = "beforeValue", excluded = false, bidirectional = false),
            @Mapping(source = "updateAfterValue", target = "afterValue", excluded = false, bidirectional = false),
            @Mapping(source = "updateColumn", target = "updatedColumn", excluded = false, bidirectional = false)
    })
    PreorderUpdateHistory toUpdateHistoryModel(OrderItemPreorderUpdateHistoryEntity updateHistoryEntity,
                                               MappingContext context);

    @Mappings({
            @Mapping(source = "updatedType", target = "updateTypeId", excluded = false, bidirectional = false),
            @Mapping(source = "beforeValue", target = "updateBeforeValue", excluded = false, bidirectional = false),
            @Mapping(source = "afterValue", target = "updateAfterValue", excluded = false, bidirectional = false),
            @Mapping(source = "updatedColumn", target = "updateColumn", excluded = false, bidirectional = false)
    })
    OrderItemPreorderUpdateHistoryEntity toUpdateHistoryEntity(PreorderUpdateHistory updateHistory,
                                                               MappingContext context);

    @Mappings({
            @Mapping(source = "productItemId", target = "offerId", excluded = false, bidirectional = false)
    })
    Subledger toSubledgerModel(SubledgerEntity subledgerEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "offerId", target = "productItemId", excluded = false, bidirectional = false)
    })
    SubledgerEntity toSubledgerEntity(Subledger subledger, MappingContext context);

    @Mappings({
            @Mapping(source = "productItemId", target = "offerId", excluded = false, bidirectional = false)
    })
    SubledgerItem toSubledgerItemModel(SubledgerItemEntity subledgerItemEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "offerId", target = "productItemId", excluded = false, bidirectional = false)
    })
    SubledgerItemEntity toSubledgerItemEntity(SubledgerItem subledgerItem, MappingContext context);
}


