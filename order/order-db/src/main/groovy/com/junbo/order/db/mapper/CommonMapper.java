/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.*;
import com.junbo.order.spec.error.AppErrors;
import com.junbo.order.spec.model.enums.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by linyi on 14-2-13.
 */
@Component("orderCommonMapper")
public class CommonMapper {

    private ObjectMapper mapper = new ObjectMapper();

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

    public OrganizationId fromLongToOrganizationId(Long organizationId) {
        if (organizationId == null) {
            return null;
        }

        return new OrganizationId(organizationId);
    }

    public Long fromOrganizationIdToLong(OrganizationId organizationId) {
        if (organizationId == null) {
            return null;
        }

        return organizationId.getValue();
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

    public PreorderId fromLongToPreorderId(Long preorderId) {
        if (preorderId == null) {
            return null;
        }

        return new PreorderId(preorderId);
    }

    public Long fromPreorderIdToLong(PreorderId preorderId) {
        if (preorderId == null) {
            return null;
        }

        return preorderId.getValue();
    }

    public String fromOrderTypeToString(OrderType type) {
        if (type == null) {
            return null;
        }

        return type.toString();
    }

    public OrderType fromStringToOrderType(String type) {
        if (type == null) {
            return null;
        }

        try {
            return OrderType.valueOf(type);
        } catch (Exception e) {
            throw AppErrors.INSTANCE.enumConversionError(type, "OrderType").exception();
        }
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

        try {
            return DiscountType.valueOf(discountType);
        } catch (Exception e) {
            throw AppErrors.INSTANCE.enumConversionError(discountType, "DiscountType").exception();
        }
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

        try {
            return OrderStatus.valueOf(orderStatus);
        } catch (Exception e) {
            throw AppErrors.INSTANCE.enumConversionError(orderStatus, "OrderStatus").exception();
        }
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

        try {
            return EventStatus.valueOf(status);
        } catch (Exception e) {
            throw AppErrors.INSTANCE.enumConversionError(status, "EventStatus").exception();
        }
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

        try {
            return OrderActionType.valueOf(action);
        } catch (Exception e) {
            throw AppErrors.INSTANCE.enumConversionError(action, "OrderAction").exception();
        }
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

        try {
            return ItemType.valueOf(itemType);
        } catch (Exception e) {
            throw AppErrors.INSTANCE.enumConversionError(itemType, "ItemType").exception();
        }
    }

    public String fromItemTypeToString(ItemType itemType) {
        if (itemType == null) {
            return null;
        }

        return itemType.toString();
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

    public String fromFulfillmentEventTypeToString(FulfillmentEventType action) {
        if (action == null) {
            return null;
        }

        return action.toString();
    }

    public FulfillmentEventType fromStringToFulfillmentEventType(String action) {
        if (action == null) {
            return null;
        }

        try {
            return FulfillmentEventType.valueOf(action);
        } catch (Exception e) {
            throw AppErrors.INSTANCE.enumConversionError(action, "FulfillmentEventType").exception();
        }
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

        try {
            return BillingAction.valueOf(action);
        } catch (Exception e) {
            throw AppErrors.INSTANCE.enumConversionError(action, "BillingAction").exception();
        }
    }

    public UserPersonalInfoId fromLongToUserPersonalInfoId(Long shippingAddressId) {
        if (shippingAddressId == null) {
            return null;
        }

        return new UserPersonalInfoId(shippingAddressId);
    }

    public Long fromUserPersonalInfoIdToLong(UserPersonalInfoId shippingAddressId) {
        if (shippingAddressId == null) {
            return null;
        }

        return shippingAddressId.getValue();
    }

    public OfferId fromStringToOfferId(String offerId) {
        if (offerId == null) {
            return null;
        }

        return new OfferId(offerId);
    }

    public String fromOfferIdToString(OfferId offerId) {
        if (offerId == null) {
            return null;
        }

        return offerId.getValue().toString();
    }

    public PaymentInstrumentId fromStringToPaymentInstrumentId(String paymentInstrumentId) {
        if (paymentInstrumentId == null) {
            return null;
        }

        return new PaymentInstrumentId(Long.parseLong(paymentInstrumentId));
    }

    public String fromPaymentInstrumentIdToString(PaymentInstrumentId paymentInstrumentId) {
        if (paymentInstrumentId == null) {
            return null;
        }

        return paymentInstrumentId.getValue().toString();
    }

    public PromotionId fromStringToPromotionId(String promotionId) {
        if (promotionId == null) {
            return null;
        }

        return new PromotionId(promotionId);
    }

    public String fromPromotionIdToString(PromotionId promotionId) {
        if (promotionId == null) {
            return null;
        }

        return promotionId.getValue().toString();
    }

    public String fromSubledgerItemStatusToString(SubledgerItemStatus status) {
        if (status == null) {
            return null;
        }

        return status.toString();
    }

    public SubledgerItemStatus fromStringToSubledgerItemStatus(String status) {
        if (status == null) {
            return null;
        }
        try {
            return SubledgerItemStatus.valueOf(status);
        } catch (Exception e) {
            throw AppErrors.INSTANCE.enumConversionError(status, "status").exception();
        }
    }

    public String fromSubledgerPayoutStatusToString(PayoutStatus status) {
        if (status == null) {
            return null;
        }

        return status.toString();
    }

    public PayoutStatus fromStringToSubledgerPayoutStatus(String status) {
        if (status == null) {
            return null;
        }
        try {
            return PayoutStatus.valueOf(status);
        } catch (Exception e) {
            throw AppErrors.INSTANCE.enumConversionError(status, "status").exception();
        }
    }

    public Long fromSubledgerItemIdToLong(SubledgerItemId subledgerItemId) {
        if (subledgerItemId == null) {
            return null;
        }
        return subledgerItemId.getValue();
    }

    public SubledgerItemId fromLongToSubledgerItemId(Long id) {
        if (id == null) {
            return null;
        }
        return new SubledgerItemId(id);
    }

    public CurrencyId fromStringToCurrencyId(String from) {
        if (from == null) {
            return null;
        }
        return new CurrencyId(from);
    }

    public String fromCurrencyIdToString(CurrencyId from) {
        if (from == null) {
            return null;
        }
        return from.getValue();
    }

    public CountryId fromStringToCountryId(String from) {
        if (from == null) {
            return null;
        }
        return new CountryId(from);
    }

    public String fromCountryIdToString(CountryId from) {
        if (from == null) {
            return null;
        }
        return from.getValue();
    }

    public LocaleId fromStringToLocaleId(String from) {
        if (from == null) {
            return null;
        }
        return new LocaleId(from);
    }

    public String fromLocaleIdToString(LocaleId from) {
        if (from == null) {
            return null;
        }
        return from.getValue();
    }

    public OrderItemRevisionType fromStringToOrderItemRevisionType(String itemType) {
        if (itemType == null) {
            return null;
        }

        try {
            return OrderItemRevisionType.valueOf(itemType);
        } catch (Exception e) {
            throw AppErrors.INSTANCE.enumConversionError(itemType, "OrderItemRevisionType").exception();
        }
    }

    public String fromSubledgerItemActionToString(SubledgerItemAction subledgerItemAction) {
        if (subledgerItemAction == null) {
            return null;
        }
        return subledgerItemAction.toString();
    }

    public SubledgerItemAction fromStringToSubledgerItemAction(String subledgerItemAction) {
        if (subledgerItemAction == null) {
            return null;
        }

        try {
            return SubledgerItemAction.valueOf(subledgerItemAction);
        } catch (Exception e) {
            throw AppErrors.INSTANCE.enumConversionError(subledgerItemAction, "SubledgerItemAction").exception();
        }
    }

    public String fromOrderItemRevisionTypeToString(OrderItemRevisionType itemType) {
        if (itemType == null) {
            return null;
        }
        return itemType.toString();
    }

    public String explicitMethod_convertPropertySet(Map<String, String> propertySet) {
        try {
            return mapper.writeValueAsString(propertySet);
        }
        catch (JsonProcessingException ex) {
            return null;
        }
    }

    public Map<String, String> explicitMethod_convertPropertySet(String propertySet) {
        try {
            return mapper.readValue(propertySet, new TypeReference<HashMap<String,String>>(){});
        }
        catch (Exception ex) {
            return null;
        }
    }
}
