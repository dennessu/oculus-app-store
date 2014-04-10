

package com.junbo.order.db.mapper;

import com.junbo.oom.core.filter.ElementMappingEvent;
import com.junbo.oom.core.filter.ElementMappingFilter;
import com.junbo.oom.core.filter.PropertyMappingEvent;
import com.junbo.oom.core.filter.PropertyMappingFilter;

import com.junbo.oom.core.builtin.BuiltinMapper;
import com.junbo.order.db.mapper.CommonMapper;
import com.junbo.order.spec.model.SubledgerItem;
import com.junbo.oom.core.MappingContext;
import com.junbo.order.db.entity.SubledgerItemEntity;
import java.util.Date;
import java.lang.String;
import com.junbo.common.id.SubledgerId;
import java.lang.Long;
import com.junbo.common.id.OrderItemId;
import com.junbo.order.spec.model.Subledger;
import com.junbo.order.db.entity.SubledgerEntity;
import java.lang.Short;
import java.math.BigDecimal;
import com.junbo.common.id.SellerId;
import com.junbo.common.id.SellerTaxProfileId;
import com.junbo.order.spec.model.PreorderUpdateHistory;
import com.junbo.order.db.entity.OrderItemPreorderUpdateHistoryEntity;
import com.junbo.order.spec.model.PreorderInfo;
import com.junbo.order.db.entity.OrderItemPreorderInfoEntity;
import com.junbo.common.id.PreorderId;
import com.junbo.order.db.entity.OrderBillingEventEntity;
import com.junbo.order.spec.model.BillingEvent;
import com.junbo.order.db.entity.enums.BillingAction;
import com.junbo.order.db.entity.enums.EventStatus;
import com.junbo.order.db.entity.OrderItemFulfillmentEventEntity;
import com.junbo.order.spec.model.FulfillmentEvent;
import com.junbo.common.id.FulfillmentEventId;
import com.junbo.order.db.entity.enums.FulfillmentAction;
import java.util.UUID;
import com.junbo.order.db.entity.OrderPaymentInfoEntity;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.order.db.entity.OrderEventEntity;
import com.junbo.order.spec.model.OrderEvent;
import com.junbo.order.db.entity.enums.OrderActionType;
import com.junbo.common.id.OrderId;
import com.junbo.order.db.entity.OrderDiscountInfoEntity;
import com.junbo.order.spec.model.Discount;
import com.junbo.common.id.PromotionId;
import com.junbo.order.db.entity.enums.DiscountType;
import com.junbo.order.db.entity.OrderItemEntity;
import com.junbo.order.spec.model.OrderItem;
import com.junbo.order.db.entity.enums.ItemType;
import com.junbo.common.id.OfferId;
import java.lang.Integer;
import com.junbo.common.id.ShippingAddressId;
import java.lang.Boolean;
import com.junbo.order.db.entity.OrderEntity;
import com.junbo.order.spec.model.Order;
import com.junbo.order.db.entity.enums.OrderType;
import com.junbo.order.db.entity.enums.OrderStatus;
import com.junbo.common.id.UserId;

@org.springframework.stereotype.Component("com.junbo.order.db.mapper.ModelMapperImpl")
public class ModelMapperImpl implements ModelMapper {


    @org.springframework.beans.factory.annotation.Autowired
    public BuiltinMapper __builtinMapper;

    @org.springframework.beans.factory.annotation.Autowired
    public CommonMapper __commonMapper;


    
    public SubledgerItemEntity toSubledgerItemEntity(SubledgerItem subledgerItem, MappingContext context) {
    
        if (subledgerItem == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        SubledgerItemEntity __result = new SubledgerItemEntity();
    
        {
        
            Date __sourceProperty = subledgerItem.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerItem.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerItemEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = subledgerItem.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerItem.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerItemEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = subledgerItem.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerItem.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerItemEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = subledgerItem.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerItem.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerItemEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            SubledgerId __sourceProperty = subledgerItem.getSubledgerId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setSubledgerId(__commonMapper.fromSubledgerIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerItem.class);
                __event.setSourcePropertyType(SubledgerId.class);
                __event.setSourcePropertyName("subledgerId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerItemEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("subledgerId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setSubledgerId(__commonMapper.fromSubledgerIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            OrderItemId __sourceProperty = subledgerItem.getOrderItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemId(__commonMapper.fromOrderItemIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerItem.class);
                __event.setSourcePropertyType(OrderItemId.class);
                __event.setSourcePropertyName("orderItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerItemEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("orderItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemId(__commonMapper.fromOrderItemIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public SubledgerItem toSubledgerItemModel(SubledgerItemEntity subledgerItemEntity, MappingContext context) {
    
        if (subledgerItemEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        SubledgerItem __result = new SubledgerItem();
    
        {
        
            Date __sourceProperty = subledgerItemEntity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerItemEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerItem.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = subledgerItemEntity.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerItemEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerItem.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = subledgerItemEntity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerItemEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerItem.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = subledgerItemEntity.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerItemEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerItem.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = subledgerItemEntity.getSubledgerId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setSubledgerId(__commonMapper.fromLongToSubledgerId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerItemEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("subledgerId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerItem.class);
                __event.setTargetPropertyType(SubledgerId.class);
                __event.setTargetPropertyName("subledgerId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setSubledgerId(__commonMapper.fromLongToSubledgerId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = subledgerItemEntity.getOrderItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemId(__commonMapper.fromLongToOrderItemId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerItemEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("orderItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerItem.class);
                __event.setTargetPropertyType(OrderItemId.class);
                __event.setTargetPropertyName("orderItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemId(__commonMapper.fromLongToOrderItemId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public SubledgerEntity toSubledgerEntity(Subledger subledger, MappingContext context) {
    
        if (subledger == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        SubledgerEntity __result = new SubledgerEntity();
    
        {
        
            String __sourceProperty = subledger.getCurrency();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCurrencyId(__commonMapper.fromStringToShort(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Subledger.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("currency");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("currencyId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCurrencyId(__commonMapper.fromStringToShort(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = subledger.getPayoutAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Subledger.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("payoutAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = subledger.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Subledger.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = subledger.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Subledger.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = subledger.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Subledger.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = subledger.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Subledger.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            SubledgerId __sourceProperty = subledger.getSubledgerId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setSubledgerId(__commonMapper.fromSubledgerIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Subledger.class);
                __event.setSourcePropertyType(SubledgerId.class);
                __event.setSourcePropertyName("subledgerId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("subledgerId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setSubledgerId(__commonMapper.fromSubledgerIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            SellerId __sourceProperty = subledger.getSellerId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setSellerId(__commonMapper.fromSellerIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Subledger.class);
                __event.setSourcePropertyType(SellerId.class);
                __event.setSourcePropertyName("sellerId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("sellerId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setSellerId(__commonMapper.fromSellerIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            SellerTaxProfileId __sourceProperty = subledger.getSellerTaxProfileId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setSellerTaxProfileId(__commonMapper.fromSellerTaxProfileIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Subledger.class);
                __event.setSourcePropertyType(SellerTaxProfileId.class);
                __event.setSourcePropertyName("sellerTaxProfileId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(SubledgerEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("sellerTaxProfileId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setSellerTaxProfileId(__commonMapper.fromSellerTaxProfileIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public Subledger toSubledgerModel(SubledgerEntity subledgerEntity, MappingContext context) {
    
        if (subledgerEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        Subledger __result = new Subledger();
    
        {
        
            Short __sourceProperty = subledgerEntity.getCurrencyId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCurrency(__commonMapper.fromShortToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("currencyId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Subledger.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("currency");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCurrency(__commonMapper.fromShortToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = subledgerEntity.getTotalAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPayoutAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Subledger.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("payoutAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPayoutAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = subledgerEntity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Subledger.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = subledgerEntity.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Subledger.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = subledgerEntity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Subledger.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = subledgerEntity.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Subledger.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = subledgerEntity.getSubledgerId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setSubledgerId(__commonMapper.fromLongToSubledgerId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("subledgerId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Subledger.class);
                __event.setTargetPropertyType(SubledgerId.class);
                __event.setTargetPropertyName("subledgerId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setSubledgerId(__commonMapper.fromLongToSubledgerId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = subledgerEntity.getSellerId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setSellerId(__commonMapper.fromLongToSellerId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("sellerId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Subledger.class);
                __event.setTargetPropertyType(SellerId.class);
                __event.setTargetPropertyName("sellerId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setSellerId(__commonMapper.fromLongToSellerId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = subledgerEntity.getSellerTaxProfileId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setSellerTaxProfileId(__commonMapper.fromLongToSellerTaxProfileId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(SubledgerEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("sellerTaxProfileId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Subledger.class);
                __event.setTargetPropertyType(SellerTaxProfileId.class);
                __event.setTargetPropertyName("sellerTaxProfileId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setSellerTaxProfileId(__commonMapper.fromLongToSellerTaxProfileId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public OrderItemPreorderUpdateHistoryEntity toUpdateHistoryEntity(PreorderUpdateHistory updateHistory, MappingContext context) {
    
        if (updateHistory == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        OrderItemPreorderUpdateHistoryEntity __result = new OrderItemPreorderUpdateHistoryEntity();
    
        {
        
            String __sourceProperty = updateHistory.getUpdatedType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdateTypeId(__commonMapper.fromStringToShort(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderUpdateHistory.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedType");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderUpdateHistoryEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("updateTypeId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdateTypeId(__commonMapper.fromStringToShort(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = updateHistory.getBeforeValue();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdateBeforeValue(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderUpdateHistory.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("beforeValue");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderUpdateHistoryEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updateBeforeValue");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdateBeforeValue(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = updateHistory.getAfterValue();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdateAfterValue(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderUpdateHistory.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("afterValue");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderUpdateHistoryEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updateAfterValue");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdateAfterValue(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = updateHistory.getUpdatedColumn();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdateColumn(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderUpdateHistory.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedColumn");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderUpdateHistoryEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updateColumn");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdateColumn(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = updateHistory.getOrderItemPreorderUpdateHistoryId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemPreorderUpdateHistoryId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderUpdateHistory.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("orderItemPreorderUpdateHistoryId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderUpdateHistoryEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("orderItemPreorderUpdateHistoryId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemPreorderUpdateHistoryId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            OrderItemId __sourceProperty = updateHistory.getOrderItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemId(__commonMapper.fromOrderItemIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderUpdateHistory.class);
                __event.setSourcePropertyType(OrderItemId.class);
                __event.setSourcePropertyName("orderItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderUpdateHistoryEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("orderItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemId(__commonMapper.fromOrderItemIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = updateHistory.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderUpdateHistory.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderUpdateHistoryEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = updateHistory.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderUpdateHistory.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderUpdateHistoryEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public PreorderUpdateHistory toUpdateHistoryModel(OrderItemPreorderUpdateHistoryEntity updateHistoryEntity, MappingContext context) {
    
        if (updateHistoryEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        PreorderUpdateHistory __result = new PreorderUpdateHistory();
    
        {
        
            Short __sourceProperty = updateHistoryEntity.getUpdateTypeId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedType(__commonMapper.fromShortToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderUpdateHistoryEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("updateTypeId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderUpdateHistory.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedType");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedType(__commonMapper.fromShortToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = updateHistoryEntity.getUpdateBeforeValue();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setBeforeValue(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderUpdateHistoryEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updateBeforeValue");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderUpdateHistory.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("beforeValue");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setBeforeValue(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = updateHistoryEntity.getUpdateAfterValue();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAfterValue(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderUpdateHistoryEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updateAfterValue");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderUpdateHistory.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("afterValue");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAfterValue(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = updateHistoryEntity.getUpdateColumn();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedColumn(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderUpdateHistoryEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updateColumn");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderUpdateHistory.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedColumn");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedColumn(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = updateHistoryEntity.getOrderItemPreorderUpdateHistoryId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemPreorderUpdateHistoryId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderUpdateHistoryEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("orderItemPreorderUpdateHistoryId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderUpdateHistory.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("orderItemPreorderUpdateHistoryId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemPreorderUpdateHistoryId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = updateHistoryEntity.getOrderItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemId(__commonMapper.fromLongToOrderItemId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderUpdateHistoryEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("orderItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderUpdateHistory.class);
                __event.setTargetPropertyType(OrderItemId.class);
                __event.setTargetPropertyName("orderItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemId(__commonMapper.fromLongToOrderItemId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = updateHistoryEntity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderUpdateHistoryEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderUpdateHistory.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = updateHistoryEntity.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderUpdateHistoryEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderUpdateHistory.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public OrderItemPreorderInfoEntity toOrderItemPreorderInfoEntity(PreorderInfo preorderInfo, MappingContext context) {
    
        if (preorderInfo == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        OrderItemPreorderInfoEntity __result = new OrderItemPreorderInfoEntity();
    
        {
        
            PreorderId __sourceProperty = preorderInfo.getPreorderInfoId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemPreorderInfoId(__commonMapper.fromPreorderIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderInfo.class);
                __event.setSourcePropertyType(PreorderId.class);
                __event.setSourcePropertyName("preorderInfoId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderInfoEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("orderItemPreorderInfoId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemPreorderInfoId(__commonMapper.fromPreorderIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = preorderInfo.getBillingTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setBillingDate(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderInfo.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("billingTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderInfoEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("billingDate");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setBillingDate(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = preorderInfo.getPreNotificationTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPreNotificationDate(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderInfo.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("preNotificationTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderInfoEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("preNotificationDate");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPreNotificationDate(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = preorderInfo.getReleaseTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setReleaseDate(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderInfo.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("releaseTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderInfoEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("releaseDate");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setReleaseDate(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = preorderInfo.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderInfo.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderInfoEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = preorderInfo.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderInfo.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderInfoEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = preorderInfo.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderInfo.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderInfoEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = preorderInfo.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderInfo.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderInfoEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            OrderItemId __sourceProperty = preorderInfo.getOrderItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemId(__commonMapper.fromOrderItemIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PreorderInfo.class);
                __event.setSourcePropertyType(OrderItemId.class);
                __event.setSourcePropertyName("orderItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemPreorderInfoEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("orderItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemId(__commonMapper.fromOrderItemIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public PreorderInfo toPreOrderInfoModel(OrderItemPreorderInfoEntity orderItemPreorderInfoEntity, MappingContext context) {
    
        if (orderItemPreorderInfoEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        PreorderInfo __result = new PreorderInfo();
    
        {
        
            Long __sourceProperty = orderItemPreorderInfoEntity.getOrderItemPreorderInfoId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPreorderInfoId(__commonMapper.fromLongToPreorderId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderInfoEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("orderItemPreorderInfoId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderInfo.class);
                __event.setTargetPropertyType(PreorderId.class);
                __event.setTargetPropertyName("preorderInfoId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPreorderInfoId(__commonMapper.fromLongToPreorderId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderItemPreorderInfoEntity.getBillingDate();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setBillingTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderInfoEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("billingDate");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderInfo.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("billingTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setBillingTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderItemPreorderInfoEntity.getPreNotificationDate();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPreNotificationTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderInfoEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("preNotificationDate");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderInfo.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("preNotificationTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPreNotificationTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderItemPreorderInfoEntity.getReleaseDate();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setReleaseTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderInfoEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("releaseDate");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderInfo.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("releaseTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setReleaseTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderItemPreorderInfoEntity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderInfoEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderInfo.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderItemPreorderInfoEntity.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderInfoEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderInfo.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderItemPreorderInfoEntity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderInfoEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderInfo.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderItemPreorderInfoEntity.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderInfoEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderInfo.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = orderItemPreorderInfoEntity.getOrderItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemId(__commonMapper.fromLongToOrderItemId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemPreorderInfoEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("orderItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PreorderInfo.class);
                __event.setTargetPropertyType(OrderItemId.class);
                __event.setTargetPropertyName("orderItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemId(__commonMapper.fromLongToOrderItemId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public BillingEvent toOrderBillingEventModel(OrderBillingEventEntity orderBillingEventEntity, MappingContext context) {
    
        if (orderBillingEventEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        BillingEvent __result = new BillingEvent();
    
        {
        
            Date __sourceProperty = orderBillingEventEntity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderBillingEventEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BillingEvent.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderBillingEventEntity.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderBillingEventEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BillingEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderBillingEventEntity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderBillingEventEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BillingEvent.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderBillingEventEntity.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderBillingEventEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BillingEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BillingAction __sourceProperty = orderBillingEventEntity.getAction();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAction(__commonMapper.fromBillingActionToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderBillingEventEntity.class);
                __event.setSourcePropertyType(BillingAction.class);
                __event.setSourcePropertyName("action");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BillingEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("action");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAction(__commonMapper.fromBillingActionToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            EventStatus __sourceProperty = orderBillingEventEntity.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.fromEventStatusToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderBillingEventEntity.class);
                __event.setSourcePropertyType(EventStatus.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BillingEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.fromEventStatusToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderBillingEventEntity.getBalanceId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setBalanceId(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderBillingEventEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("balanceId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BillingEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("balanceId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setBalanceId(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public OrderBillingEventEntity toOrderBillingEventEntity(BillingEvent billingEvent, MappingContext context) {
    
        if (billingEvent == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        OrderBillingEventEntity __result = new OrderBillingEventEntity();
    
        {
        
            Date __sourceProperty = billingEvent.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BillingEvent.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderBillingEventEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = billingEvent.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BillingEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderBillingEventEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = billingEvent.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BillingEvent.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderBillingEventEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = billingEvent.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BillingEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderBillingEventEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = billingEvent.getBalanceId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setBalanceId(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BillingEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("balanceId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderBillingEventEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("balanceId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setBalanceId(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = billingEvent.getAction();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAction(__commonMapper.fromStringToBillingAction(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BillingEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("action");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderBillingEventEntity.class);
                __event.setTargetPropertyType(BillingAction.class);
                __event.setTargetPropertyName("action");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAction(__commonMapper.fromStringToBillingAction(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = billingEvent.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.fromStringToEventStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BillingEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderBillingEventEntity.class);
                __event.setTargetPropertyType(EventStatus.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.fromStringToEventStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public FulfillmentEvent toFulfillmentEventModel(OrderItemFulfillmentEventEntity orderItemFulfillmentEventEntity, MappingContext context) {
    
        if (orderItemFulfillmentEventEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        FulfillmentEvent __result = new FulfillmentEvent();
    
        {
        
            Long __sourceProperty = orderItemFulfillmentEventEntity.getEventId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.fromLongToFulfillmentEventId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemFulfillmentEventEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("eventId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(FulfillmentEvent.class);
                __event.setTargetPropertyType(FulfillmentEventId.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.fromLongToFulfillmentEventId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = orderItemFulfillmentEventEntity.getOrderItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItem(__commonMapper.fromLongToOrderItemId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemFulfillmentEventEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("orderItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(FulfillmentEvent.class);
                __event.setTargetPropertyType(OrderItemId.class);
                __event.setTargetPropertyName("orderItem");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItem(__commonMapper.fromLongToOrderItemId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderItemFulfillmentEventEntity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemFulfillmentEventEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(FulfillmentEvent.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderItemFulfillmentEventEntity.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemFulfillmentEventEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(FulfillmentEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderItemFulfillmentEventEntity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemFulfillmentEventEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(FulfillmentEvent.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderItemFulfillmentEventEntity.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemFulfillmentEventEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(FulfillmentEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            FulfillmentAction __sourceProperty = orderItemFulfillmentEventEntity.getAction();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAction(__commonMapper.fromFulfillmentActionToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemFulfillmentEventEntity.class);
                __event.setSourcePropertyType(FulfillmentAction.class);
                __event.setSourcePropertyName("action");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(FulfillmentEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("action");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAction(__commonMapper.fromFulfillmentActionToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            EventStatus __sourceProperty = orderItemFulfillmentEventEntity.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.fromEventStatusToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemFulfillmentEventEntity.class);
                __event.setSourcePropertyType(EventStatus.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(FulfillmentEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.fromEventStatusToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UUID __sourceProperty = orderItemFulfillmentEventEntity.getTrackingUuid();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTrackingUuid(__commonMapper.fromUuidToUuid(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemFulfillmentEventEntity.class);
                __event.setSourcePropertyType(UUID.class);
                __event.setSourcePropertyName("trackingUuid");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(FulfillmentEvent.class);
                __event.setTargetPropertyType(UUID.class);
                __event.setTargetPropertyName("trackingUuid");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTrackingUuid(__commonMapper.fromUuidToUuid(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderItemFulfillmentEventEntity.getFulfillmentId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setFulfillmentId(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemFulfillmentEventEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("fulfillmentId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(FulfillmentEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("fulfillmentId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setFulfillmentId(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public OrderItemFulfillmentEventEntity toOrderItemFulfillmentEventEntity(FulfillmentEvent fulfillmentEvent, MappingContext context) {
    
        if (fulfillmentEvent == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        OrderItemFulfillmentEventEntity __result = new OrderItemFulfillmentEventEntity();
    
        {
        
            FulfillmentEventId __sourceProperty = fulfillmentEvent.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setEventId(__commonMapper.fromFulfillmentEventIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(FulfillmentEvent.class);
                __event.setSourcePropertyType(FulfillmentEventId.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemFulfillmentEventEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("eventId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setEventId(__commonMapper.fromFulfillmentEventIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            OrderItemId __sourceProperty = fulfillmentEvent.getOrderItem();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemId(__commonMapper.fromOrderItemIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(FulfillmentEvent.class);
                __event.setSourcePropertyType(OrderItemId.class);
                __event.setSourcePropertyName("orderItem");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemFulfillmentEventEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("orderItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemId(__commonMapper.fromOrderItemIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = fulfillmentEvent.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(FulfillmentEvent.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemFulfillmentEventEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = fulfillmentEvent.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(FulfillmentEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemFulfillmentEventEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = fulfillmentEvent.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(FulfillmentEvent.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemFulfillmentEventEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = fulfillmentEvent.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(FulfillmentEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemFulfillmentEventEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UUID __sourceProperty = fulfillmentEvent.getTrackingUuid();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTrackingUuid(__commonMapper.fromUuidToUuid(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(FulfillmentEvent.class);
                __event.setSourcePropertyType(UUID.class);
                __event.setSourcePropertyName("trackingUuid");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemFulfillmentEventEntity.class);
                __event.setTargetPropertyType(UUID.class);
                __event.setTargetPropertyName("trackingUuid");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTrackingUuid(__commonMapper.fromUuidToUuid(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = fulfillmentEvent.getFulfillmentId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setFulfillmentId(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(FulfillmentEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("fulfillmentId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemFulfillmentEventEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("fulfillmentId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setFulfillmentId(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = fulfillmentEvent.getAction();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAction(__commonMapper.fromStringToFulfillmentAction(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(FulfillmentEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("action");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemFulfillmentEventEntity.class);
                __event.setTargetPropertyType(FulfillmentAction.class);
                __event.setTargetPropertyName("action");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAction(__commonMapper.fromStringToFulfillmentAction(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = fulfillmentEvent.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.fromStringToEventStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(FulfillmentEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemFulfillmentEventEntity.class);
                __event.setTargetPropertyType(EventStatus.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.fromStringToEventStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public PaymentInstrumentId toPaymentInstrumentId(OrderPaymentInfoEntity orderPaymentInfoEntity, MappingContext context) {
    
        if (orderPaymentInfoEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        PaymentInstrumentId __result = new PaymentInstrumentId();
    
    
        return __result;
    }

    
    public OrderPaymentInfoEntity toOrderPaymentInfoEntity(PaymentInstrumentId paymentInstrumentId, MappingContext context) {
    
        if (paymentInstrumentId == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        OrderPaymentInfoEntity __result = new OrderPaymentInfoEntity();
    
    
        return __result;
    }

    
    public OrderEvent toOrderEventModel(OrderEventEntity orderEventEntity, MappingContext context) {
    
        if (orderEventEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        OrderEvent __result = new OrderEvent();
    
        {
        
            OrderActionType __sourceProperty = orderEventEntity.getActionId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAction(__commonMapper.fromOrderActionTypeToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEventEntity.class);
                __event.setSourcePropertyType(OrderActionType.class);
                __event.setSourcePropertyName("actionId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("action");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAction(__commonMapper.fromOrderActionTypeToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            EventStatus __sourceProperty = orderEventEntity.getStatusId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.fromEventStatusToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEventEntity.class);
                __event.setSourcePropertyType(EventStatus.class);
                __event.setSourcePropertyName("statusId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.fromEventStatusToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = orderEventEntity.getOrderId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrder(__commonMapper.fromLongToOrderId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEventEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("orderId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEvent.class);
                __event.setTargetPropertyType(OrderId.class);
                __event.setTargetPropertyName("order");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrder(__commonMapper.fromLongToOrderId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderEventEntity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEventEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEvent.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderEventEntity.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEventEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderEventEntity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEventEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEvent.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderEventEntity.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEventEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UUID __sourceProperty = orderEventEntity.getTrackingUuid();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTrackingUuid(__commonMapper.fromUuidToUuid(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEventEntity.class);
                __event.setSourcePropertyType(UUID.class);
                __event.setSourcePropertyName("trackingUuid");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEvent.class);
                __event.setTargetPropertyType(UUID.class);
                __event.setTargetPropertyName("trackingUuid");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTrackingUuid(__commonMapper.fromUuidToUuid(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UUID __sourceProperty = orderEventEntity.getEventTrackingUuid();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setEventTrackingUuid(__commonMapper.fromUuidToUuid(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEventEntity.class);
                __event.setSourcePropertyType(UUID.class);
                __event.setSourcePropertyName("eventTrackingUuid");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEvent.class);
                __event.setTargetPropertyType(UUID.class);
                __event.setTargetPropertyName("eventTrackingUuid");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setEventTrackingUuid(__commonMapper.fromUuidToUuid(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderEventEntity.getFlowType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setFlowType(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEventEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("flowType");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("flowType");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setFlowType(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public OrderEventEntity toOrderEventEntity(OrderEvent orderEvent, MappingContext context) {
    
        if (orderEvent == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        OrderEventEntity __result = new OrderEventEntity();
    
        {
        
            String __sourceProperty = orderEvent.getAction();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setActionId(__commonMapper.fromStringToOrderActionType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("action");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEventEntity.class);
                __event.setTargetPropertyType(OrderActionType.class);
                __event.setTargetPropertyName("actionId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setActionId(__commonMapper.fromStringToOrderActionType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderEvent.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatusId(__commonMapper.fromStringToEventStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEventEntity.class);
                __event.setTargetPropertyType(EventStatus.class);
                __event.setTargetPropertyName("statusId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatusId(__commonMapper.fromStringToEventStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            OrderId __sourceProperty = orderEvent.getOrder();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderId(__commonMapper.fromOrderIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEvent.class);
                __event.setSourcePropertyType(OrderId.class);
                __event.setSourcePropertyName("order");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEventEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("orderId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderId(__commonMapper.fromOrderIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderEvent.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEvent.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEventEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderEvent.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEventEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderEvent.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEvent.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEventEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderEvent.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEventEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UUID __sourceProperty = orderEvent.getTrackingUuid();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTrackingUuid(__commonMapper.fromUuidToUuid(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEvent.class);
                __event.setSourcePropertyType(UUID.class);
                __event.setSourcePropertyName("trackingUuid");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEventEntity.class);
                __event.setTargetPropertyType(UUID.class);
                __event.setTargetPropertyName("trackingUuid");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTrackingUuid(__commonMapper.fromUuidToUuid(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UUID __sourceProperty = orderEvent.getEventTrackingUuid();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setEventTrackingUuid(__commonMapper.fromUuidToUuid(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEvent.class);
                __event.setSourcePropertyType(UUID.class);
                __event.setSourcePropertyName("eventTrackingUuid");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEventEntity.class);
                __event.setTargetPropertyType(UUID.class);
                __event.setTargetPropertyName("eventTrackingUuid");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setEventTrackingUuid(__commonMapper.fromUuidToUuid(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderEvent.getFlowType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setFlowType(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("flowType");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEventEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("flowType");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setFlowType(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public Discount toDiscountModel(OrderDiscountInfoEntity discountEntity, MappingContext context) {
    
        if (discountEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        Discount __result = new Discount();
    
        {
        
            String __sourceProperty = discountEntity.getPromotionId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPromotion(__commonMapper.fromStringToPromotionId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderDiscountInfoEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("promotionId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Discount.class);
                __event.setTargetPropertyType(PromotionId.class);
                __event.setTargetPropertyName("promotion");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPromotion(__commonMapper.fromStringToPromotionId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = discountEntity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderDiscountInfoEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Discount.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = discountEntity.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderDiscountInfoEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Discount.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = discountEntity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderDiscountInfoEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Discount.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = discountEntity.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderDiscountInfoEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Discount.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = discountEntity.getDiscountInfoId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDiscountInfoId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderDiscountInfoEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("discountInfoId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Discount.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("discountInfoId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDiscountInfoId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = discountEntity.getOrderId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderId(__commonMapper.fromLongToOrderId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderDiscountInfoEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("orderId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Discount.class);
                __event.setTargetPropertyType(OrderId.class);
                __event.setTargetPropertyName("orderId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderId(__commonMapper.fromLongToOrderId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = discountEntity.getOrderItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemId(__commonMapper.fromLongToOrderItemId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderDiscountInfoEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("orderItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Discount.class);
                __event.setTargetPropertyType(OrderItemId.class);
                __event.setTargetPropertyName("orderItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemId(__commonMapper.fromLongToOrderItemId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            DiscountType __sourceProperty = discountEntity.getDiscountType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDiscountType(__commonMapper.fromDiscountTypeToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderDiscountInfoEntity.class);
                __event.setSourcePropertyType(DiscountType.class);
                __event.setSourcePropertyName("discountType");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Discount.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("discountType");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDiscountType(__commonMapper.fromDiscountTypeToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = discountEntity.getDiscountAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDiscountAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderDiscountInfoEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("discountAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Discount.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("discountAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDiscountAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = discountEntity.getDiscountRate();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDiscountRate(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderDiscountInfoEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("discountRate");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Discount.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("discountRate");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDiscountRate(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = discountEntity.getCoupon();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCoupon(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderDiscountInfoEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("coupon");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Discount.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("coupon");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCoupon(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public OrderDiscountInfoEntity toDiscountEntity(Discount discount, MappingContext context) {
    
        if (discount == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        OrderDiscountInfoEntity __result = new OrderDiscountInfoEntity();
    
        {
        
            PromotionId __sourceProperty = discount.getPromotion();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPromotionId(__commonMapper.fromPromotionIdToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Discount.class);
                __event.setSourcePropertyType(PromotionId.class);
                __event.setSourcePropertyName("promotion");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderDiscountInfoEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("promotionId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPromotionId(__commonMapper.fromPromotionIdToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = discount.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Discount.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderDiscountInfoEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = discount.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Discount.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderDiscountInfoEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = discount.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Discount.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderDiscountInfoEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = discount.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Discount.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderDiscountInfoEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = discount.getDiscountInfoId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDiscountInfoId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Discount.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("discountInfoId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderDiscountInfoEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("discountInfoId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDiscountInfoId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            OrderId __sourceProperty = discount.getOrderId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderId(__commonMapper.fromOrderIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Discount.class);
                __event.setSourcePropertyType(OrderId.class);
                __event.setSourcePropertyName("orderId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderDiscountInfoEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("orderId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderId(__commonMapper.fromOrderIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            OrderItemId __sourceProperty = discount.getOrderItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemId(__commonMapper.fromOrderItemIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Discount.class);
                __event.setSourcePropertyType(OrderItemId.class);
                __event.setSourcePropertyName("orderItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderDiscountInfoEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("orderItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemId(__commonMapper.fromOrderItemIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = discount.getDiscountType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDiscountType(__commonMapper.fromStringToDiscountType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Discount.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("discountType");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderDiscountInfoEntity.class);
                __event.setTargetPropertyType(DiscountType.class);
                __event.setTargetPropertyName("discountType");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDiscountType(__commonMapper.fromStringToDiscountType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = discount.getDiscountRate();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDiscountRate(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Discount.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("discountRate");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderDiscountInfoEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("discountRate");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDiscountRate(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = discount.getDiscountAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDiscountAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Discount.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("discountAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderDiscountInfoEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("discountAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDiscountAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = discount.getCoupon();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCoupon(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Discount.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("coupon");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderDiscountInfoEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("coupon");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCoupon(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public OrderItem toOrderItemModel(OrderItemEntity orderItemEntity, MappingContext context) {
    
        if (orderItemEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        OrderItem __result = new OrderItem();
    
        {
        
            ItemType __sourceProperty = orderItemEntity.getOrderItemType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.fromItemTypeToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(ItemType.class);
                __event.setSourcePropertyName("orderItemType");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.fromItemTypeToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderItemEntity.getProductItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOffer(__commonMapper.fromStringToOfferId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("productItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(OfferId.class);
                __event.setTargetPropertyName("offer");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOffer(__commonMapper.fromStringToOfferId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderItemEntity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderItemEntity.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderItemEntity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderItemEntity.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = orderItemEntity.getOrderItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemId(__commonMapper.fromLongToOrderItemId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("orderItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(OrderItemId.class);
                __event.setTargetPropertyName("orderItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemId(__commonMapper.fromLongToOrderItemId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = orderItemEntity.getOrderId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderId(__commonMapper.fromLongToOrderId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("orderId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(OrderId.class);
                __event.setTargetPropertyName("orderId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderId(__commonMapper.fromLongToOrderId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderItemEntity.getUnitPrice();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUnitPrice(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("unitPrice");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("unitPrice");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUnitPrice(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = orderItemEntity.getQuantity();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setQuantity(__builtinMapper.fromIntegerToInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("quantity");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("quantity");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setQuantity(__builtinMapper.fromIntegerToInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderItemEntity.getFederatedId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setFederatedId(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("federatedId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("federatedId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setFederatedId(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderItemEntity.getProperties();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setProperties(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("properties");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("properties");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setProperties(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = orderItemEntity.getShippingAddressId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setShippingAddressId(__commonMapper.fromLongToShippingAddressId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("shippingAddressId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(ShippingAddressId.class);
                __event.setTargetPropertyName("shippingAddressId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setShippingAddressId(__commonMapper.fromLongToShippingAddressId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = orderItemEntity.getShippingMethodId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setShippingMethodId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("shippingMethodId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("shippingMethodId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setShippingMethodId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderItemEntity.getTotalAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderItemEntity.getTotalTax();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalTax(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalTax");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalTax");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalTax(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Boolean __sourceProperty = orderItemEntity.getIsTaxExempted();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setIsTaxExempted(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(Boolean.class);
                __event.setSourcePropertyName("isTaxExempted");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(Boolean.class);
                __event.setTargetPropertyName("isTaxExempted");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setIsTaxExempted(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderItemEntity.getTotalDiscount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalDiscount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalDiscount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalDiscount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalDiscount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderItemEntity.getTotalPreorderAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalPreorderAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalPreorderAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalPreorderAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalPreorderAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderItemEntity.getTotalPreorderTax();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalPreorderTax(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalPreorderTax");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalPreorderTax");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalPreorderTax(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderItemEntity.getHonorUntilTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setHonorUntilTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("honorUntilTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("honorUntilTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setHonorUntilTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderItemEntity.getHonoredTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setHonoredTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItemEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("honoredTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItem.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("honoredTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setHonoredTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public OrderItemEntity toOrderItemEntity(OrderItem orderItem, MappingContext context) {
    
        if (orderItem == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        OrderItemEntity __result = new OrderItemEntity();
    
        {
        
            String __sourceProperty = orderItem.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemType(__commonMapper.fromStringToItemType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(ItemType.class);
                __event.setTargetPropertyName("orderItemType");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemType(__commonMapper.fromStringToItemType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            OfferId __sourceProperty = orderItem.getOffer();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setProductItemId(__commonMapper.fromOfferIdToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(OfferId.class);
                __event.setSourcePropertyName("offer");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("productItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setProductItemId(__commonMapper.fromOfferIdToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderItem.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderItem.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderItem.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderItem.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            OrderItemId __sourceProperty = orderItem.getOrderItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemId(__commonMapper.fromOrderItemIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(OrderItemId.class);
                __event.setSourcePropertyName("orderItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("orderItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemId(__commonMapper.fromOrderItemIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            OrderId __sourceProperty = orderItem.getOrderId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderId(__commonMapper.fromOrderIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(OrderId.class);
                __event.setSourcePropertyName("orderId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("orderId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderId(__commonMapper.fromOrderIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderItem.getUnitPrice();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUnitPrice(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("unitPrice");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("unitPrice");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUnitPrice(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = orderItem.getQuantity();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setQuantity(__builtinMapper.fromIntegerToInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("quantity");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("quantity");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setQuantity(__builtinMapper.fromIntegerToInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderItem.getFederatedId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setFederatedId(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("federatedId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("federatedId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setFederatedId(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderItem.getProperties();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setProperties(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("properties");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("properties");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setProperties(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            ShippingAddressId __sourceProperty = orderItem.getShippingAddressId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setShippingAddressId(__commonMapper.fromShippingAddressIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(ShippingAddressId.class);
                __event.setSourcePropertyName("shippingAddressId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("shippingAddressId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setShippingAddressId(__commonMapper.fromShippingAddressIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = orderItem.getShippingMethodId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setShippingMethodId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("shippingMethodId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("shippingMethodId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setShippingMethodId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderItem.getTotalAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderItem.getTotalTax();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalTax(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalTax");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalTax");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalTax(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Boolean __sourceProperty = orderItem.getIsTaxExempted();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setIsTaxExempted(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(Boolean.class);
                __event.setSourcePropertyName("isTaxExempted");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(Boolean.class);
                __event.setTargetPropertyName("isTaxExempted");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setIsTaxExempted(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderItem.getTotalDiscount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalDiscount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalDiscount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalDiscount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalDiscount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderItem.getTotalPreorderAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalPreorderAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalPreorderAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalPreorderAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalPreorderAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderItem.getTotalPreorderTax();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalPreorderTax(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalPreorderTax");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalPreorderTax");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalPreorderTax(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderItem.getHonorUntilTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setHonorUntilTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("honorUntilTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("honorUntilTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setHonorUntilTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderItem.getHonoredTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setHonoredTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderItem.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("honoredTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderItemEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("honoredTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setHonoredTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public Order toOrderModel(OrderEntity orderEntity, MappingContext context) {
    
        if (orderEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        Order __result = new Order();
    
        {
        
            OrderType __sourceProperty = orderEntity.getOrderTypeId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.fromOrderTypeToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(OrderType.class);
                __event.setSourcePropertyName("orderTypeId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.fromOrderTypeToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            OrderStatus __sourceProperty = orderEntity.getOrderStatusId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.fromOrderStatusToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(OrderStatus.class);
                __event.setSourcePropertyName("orderStatusId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.fromOrderStatusToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = orderEntity.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUser(__commonMapper.fromLongToUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(UserId.class);
                __event.setTargetPropertyName("user");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUser(__commonMapper.fromLongToUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = orderEntity.getOriginalOrderId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOriginalOrder(__commonMapper.fromLongToOrderId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("originalOrderId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(OrderId.class);
                __event.setTargetPropertyName("originalOrder");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOriginalOrder(__commonMapper.fromLongToOrderId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = orderEntity.getOrderId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.fromLongToOrderId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("orderId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(OrderId.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.fromLongToOrderId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderEntity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderEntity.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderEntity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderEntity.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UUID __sourceProperty = orderEntity.getTrackingUuid();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTrackingUuid(__commonMapper.fromUuidToUuid(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(UUID.class);
                __event.setSourcePropertyName("trackingUuid");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(UUID.class);
                __event.setTargetPropertyName("trackingUuid");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTrackingUuid(__commonMapper.fromUuidToUuid(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderEntity.getCurrency();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCurrency(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("currency");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("currency");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCurrency(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = orderEntity.getShippingAddressId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setShippingAddressId(__commonMapper.fromLongToShippingAddressId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("shippingAddressId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(ShippingAddressId.class);
                __event.setTargetPropertyName("shippingAddressId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setShippingAddressId(__commonMapper.fromLongToShippingAddressId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = orderEntity.getShippingMethodId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setShippingMethodId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("shippingMethodId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("shippingMethodId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setShippingMethodId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = orderEntity.getCountry();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCountry(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("country");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("country");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCountry(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Boolean __sourceProperty = orderEntity.getTentative();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTentative(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(Boolean.class);
                __event.setSourcePropertyName("tentative");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(Boolean.class);
                __event.setTargetPropertyName("tentative");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTentative(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderEntity.getTotalAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderEntity.getTotalTax();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalTax(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalTax");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalTax");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalTax(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Boolean __sourceProperty = orderEntity.getIsTaxInclusive();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setIsTaxInclusive(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(Boolean.class);
                __event.setSourcePropertyName("isTaxInclusive");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(Boolean.class);
                __event.setTargetPropertyName("isTaxInclusive");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setIsTaxInclusive(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderEntity.getTotalDiscount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalDiscount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalDiscount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalDiscount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalDiscount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderEntity.getTotalShippingFee();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalShippingFee(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalShippingFee");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalShippingFee");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalShippingFee(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderEntity.getTotalShippingFeeDiscount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalShippingFeeDiscount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalShippingFeeDiscount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalShippingFeeDiscount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalShippingFeeDiscount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderEntity.getTotalPreorderAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalPreorderAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalPreorderAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalPreorderAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalPreorderAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = orderEntity.getTotalPreorderTax();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalPreorderTax(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalPreorderTax");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalPreorderTax");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalPreorderTax(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderEntity.getHonorUntilTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setHonorUntilTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("honorUntilTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("honorUntilTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setHonorUntilTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = orderEntity.getHonoredTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setHonoredTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OrderEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("honoredTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Order.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("honoredTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setHonoredTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public OrderEntity toOrderEntity(Order order, MappingContext context) {
    
        if (order == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        OrderEntity __result = new OrderEntity();
    
        {
        
            String __sourceProperty = order.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderTypeId(__commonMapper.fromStringToOrderType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(OrderType.class);
                __event.setTargetPropertyName("orderTypeId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderTypeId(__commonMapper.fromStringToOrderType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = order.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderStatusId(__commonMapper.fromStringToOrderStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(OrderStatus.class);
                __event.setTargetPropertyName("orderStatusId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderStatusId(__commonMapper.fromStringToOrderStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UserId __sourceProperty = order.getUser();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__commonMapper.fromUserIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(UserId.class);
                __event.setSourcePropertyName("user");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__commonMapper.fromUserIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            OrderId __sourceProperty = order.getOriginalOrder();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOriginalOrderId(__commonMapper.fromOrderIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(OrderId.class);
                __event.setSourcePropertyName("originalOrder");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("originalOrderId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOriginalOrderId(__commonMapper.fromOrderIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            OrderId __sourceProperty = order.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderId(__commonMapper.fromOrderIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(OrderId.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("orderId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderId(__commonMapper.fromOrderIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = order.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = order.getCreatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("createdBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("createdBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = order.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = order.getUpdatedBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("updatedBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("updatedBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedBy(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UUID __sourceProperty = order.getTrackingUuid();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTrackingUuid(__commonMapper.fromUuidToUuid(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(UUID.class);
                __event.setSourcePropertyName("trackingUuid");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(UUID.class);
                __event.setTargetPropertyName("trackingUuid");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTrackingUuid(__commonMapper.fromUuidToUuid(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = order.getCurrency();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCurrency(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("currency");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("currency");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCurrency(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            ShippingAddressId __sourceProperty = order.getShippingAddressId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setShippingAddressId(__commonMapper.fromShippingAddressIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(ShippingAddressId.class);
                __event.setSourcePropertyName("shippingAddressId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("shippingAddressId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setShippingAddressId(__commonMapper.fromShippingAddressIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = order.getShippingMethodId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setShippingMethodId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("shippingMethodId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("shippingMethodId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setShippingMethodId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = order.getCountry();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCountry(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("country");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("country");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCountry(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Boolean __sourceProperty = order.getTentative();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTentative(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(Boolean.class);
                __event.setSourcePropertyName("tentative");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(Boolean.class);
                __event.setTargetPropertyName("tentative");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTentative(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = order.getTotalAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = order.getTotalTax();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalTax(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalTax");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalTax");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalTax(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Boolean __sourceProperty = order.getIsTaxInclusive();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setIsTaxInclusive(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(Boolean.class);
                __event.setSourcePropertyName("isTaxInclusive");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(Boolean.class);
                __event.setTargetPropertyName("isTaxInclusive");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setIsTaxInclusive(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = order.getTotalDiscount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalDiscount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalDiscount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalDiscount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalDiscount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = order.getTotalShippingFee();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalShippingFee(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalShippingFee");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalShippingFee");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalShippingFee(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = order.getTotalShippingFeeDiscount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalShippingFeeDiscount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalShippingFeeDiscount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalShippingFeeDiscount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalShippingFeeDiscount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = order.getTotalPreorderAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalPreorderAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalPreorderAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalPreorderAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalPreorderAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = order.getTotalPreorderTax();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalPreorderTax(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalPreorderTax");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("totalPreorderTax");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTotalPreorderTax(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = order.getHonorUntilTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setHonorUntilTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("honorUntilTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("honorUntilTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setHonorUntilTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = order.getHonoredTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setHonoredTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Order.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("honoredTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OrderEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("honoredTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setHonoredTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }
}
