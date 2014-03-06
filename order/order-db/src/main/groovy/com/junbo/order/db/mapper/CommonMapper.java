/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.mapper;

import com.junbo.common.id.*;
import com.junbo.order.spec.model.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by linyi on 14-2-13.
 */
@Component("commonMapper")
public class CommonMapper {

    public String fromLongToString(Long id) {
        if (id == null) {
            return null;
        }

        return id.toString();
    }

    public Long fromStringToLong(String id) {
        if (id == null) {
            return null;
        }

        return Long.valueOf(id);
    }

    public UUID fromUuidToUuid(UUID uuid) {
        return uuid;
    }

    public String fromShortToString(Short id) {
        if (id == null) {
            return null;
        }

        return id.toString();
    }

    public Short fromStringToShort(String id) {
        if (id == null) {
            return null;
        }

        return Short.valueOf(id);
    }

    public UserId fromLongToUserId(Long userId) {
        if (userId == null) {
            return null;
        }

        return new UserId(userId);
    }

    public Long fromUserIdToLong(UserId userId) {
        if (userId == null) {
            return null;
        }

        return userId.getValue();
    }

    public OrderId fromLongToOrderId(Long orderId) {
        if (orderId == null) {
            return null;
        }

        return new OrderId(orderId);
    }

    public Long fromOrderIdToLong(OrderId orderId) {
        if (orderId == null) {
            return null;
        }

        return orderId.getValue();
    }

    public String fromOrderTypeToString(OrderType type) {
        if (type == null) {
            return null;
        }

        return type.toString();
    }

    public OrderType fromStringToOrderType(String type) {
        return OrderType.valueOf(type);
    }

    public Long fromOrderItemIdToLong(OrderItemId orderItemId) {
        if (orderItemId == null) {
            return null;
        }

        return orderItemId.getValue();
    }

    public OrderItemId fromLongToOrderItemId(Long orderItemId) {
        if (orderItemId == null) {
            return null;
        }

        return new OrderItemId(orderItemId);
    }

    public DiscountType fromStringToDiscountType(String discountType) {
        if (discountType == null) {
            return null;
        }

        return DiscountType.valueOf(discountType);
    }

    public String fromDiscountTypeToString(DiscountType discountType) {
        if (discountType == null) {
            return null;
        }

        return discountType.toString();
    }

    public String fromOrderStatusToString(OrderStatus orderStatus) {
        if (orderStatus == null) {
            return null;
        }

        return orderStatus.toString();
    }

    public OrderStatus fromStringToOrderStatus(String orderStatus) {
        if (orderStatus == null) {
            return null;
        }

        return OrderStatus.valueOf(orderStatus);
    }

    public String fromEventStatusToString(EventStatus status) {
        if (status == null) {
            return null;
        }

        return status.toString();
    }

    public EventStatus fromStringToEventStatus(String status) {
        if (status == null) {
            return null;
        }

        return EventStatus.valueOf(status);
    }

    public String fromOrderActionTypeToString(OrderActionType action) {
        if (action == null) {
            return null;
        }

        return action.toString();
    }

    public OrderActionType fromStringToOrderActionType(String action) {
        if (action == null) {
            return null;
        }

        return OrderActionType.valueOf(action);
    }

    public Long fromOrderEventIdToLong(OrderEventId orderEventId) {
        if (orderEventId == null) {
            return null;
        }

        return orderEventId.getValue();
    }

    public OrderEventId fromLongToOrderEventId(Long orderEventId) {
        if (orderEventId == null) {
            return null;
        }

        return new OrderEventId(orderEventId);
    }

    public ItemType fromStringToItemType(String itemType) {
        if (itemType == null) {
            return null;
        }

        return ItemType.valueOf(itemType);
    }

    public String fromItemTypeToString(ItemType itemType) {
        if (itemType == null) {
            return null;
        }

        return itemType.toString();
    }

    public Long fromDiscountIdToLong(DiscountId discountId) {
        if (discountId == null) {
            return null;
        }

        return discountId.getValue();
    }

    public DiscountId fromLongToDiscountId(Long discountId) {
        if (discountId == null) {
            return null;
        }

        return new DiscountId(discountId);
    }

    public FulfillmentEventId fromLongToFulfillmentEventId(Long fulfillmentEventId) {
        if (fulfillmentEventId == null) {
            return null;
        }

        return new FulfillmentEventId(fulfillmentEventId);
    }

    public Long fromFulfillmentEventIdToLong(FulfillmentEventId fulfillmentEventId) {
        if (fulfillmentEventId == null) {
            return null;
        }

        return fulfillmentEventId.getValue();
    }

    public SellerId fromLongToSellerId(Long sellerId) {
        if (sellerId == null) {
            return null;
        }

        return new SellerId(sellerId);
    }

    public Long fromSellerIdToLong(SellerId sellerId) {
        if (sellerId == null) {
            return null;
        }

        return sellerId.getValue();
    }

    public SellerTaxProfileId fromLongToSellerTaxProfileId(Long sellerTaxProfileId) {
        if (sellerTaxProfileId == null) {
            return null;
        }

        return new SellerTaxProfileId(sellerTaxProfileId);
    }

    public Long fromSellerTaxProfileIdToLong(SellerTaxProfileId sellerTaxProfileId) {
        if (sellerTaxProfileId == null) {
            return null;
        }

        return sellerTaxProfileId.getValue();
    }

    public SubledgerId fromLongToSubledgerId(Long subledgerId) {
        if (subledgerId == null) {
            return null;
        }

        return new SubledgerId(subledgerId);
    }

    public Long fromSubledgerIdToLong(SubledgerId subledgerId) {
        if (subledgerId == null) {
            return null;
        }

        return subledgerId.getValue();
    }

    public String fromFulfillmentActionToString(FulfillmentAction action) {
        if (action == null) {
            return null;
        }

        return action.toString();
    }

    public FulfillmentAction fromStringToFulfillmentAction(String action) {
        if (action == null) {
            return null;
        }

        return FulfillmentAction.valueOf(action);
    }

    public String fromBillingActionToString(BillingAction action) {
        if (action == null) {
            return null;
        }

        return action.toString();
    }

    public BillingAction fromStringToBillingAction(String action) {
        if (action == null) {
            return null;
        }

        return BillingAction.valueOf(action);
    }

    public ShippingAddressId fromLongToShippingAddressId(Long shippingAddressId) {
        if (shippingAddressId == null) {
            return null;
        }

        return new ShippingAddressId(shippingAddressId);
    }

    public Long fromShippingAddressIdToLong(ShippingAddressId shippingAddressId) {
        if (shippingAddressId == null) {
            return null;
        }

        return shippingAddressId.getValue();
    }

}
