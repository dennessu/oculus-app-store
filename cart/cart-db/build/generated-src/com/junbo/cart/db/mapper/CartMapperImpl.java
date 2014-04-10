

package com.junbo.cart.db.mapper;

import com.junbo.oom.core.filter.ElementMappingEvent;
import com.junbo.oom.core.filter.ElementMappingFilter;
import com.junbo.oom.core.filter.PropertyMappingEvent;
import com.junbo.oom.core.filter.PropertyMappingFilter;

import com.junbo.oom.core.builtin.BuiltinMapper;
import com.junbo.cart.db.mapper.CommonMapper;
import com.junbo.cart.spec.model.item.CouponItem;
import com.junbo.oom.core.MappingContext;
import com.junbo.cart.db.entity.CouponItemEntity;
import com.junbo.common.id.CartItemId;
import java.lang.Long;
import com.junbo.common.id.CouponId;
import java.lang.String;
import java.util.Date;
import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.cart.db.entity.OfferItemEntity;
import com.junbo.common.id.OfferId;
import java.lang.Boolean;
import com.junbo.cart.spec.model.Cart;
import com.junbo.cart.db.entity.CartEntity;
import com.junbo.common.id.UserId;
import com.junbo.common.id.CartId;

@org.springframework.stereotype.Component("com.junbo.cart.db.mapper.CartMapperImpl")
public class CartMapperImpl implements CartMapper {


    @org.springframework.beans.factory.annotation.Autowired
    public BuiltinMapper __builtinMapper;

    @org.springframework.beans.factory.annotation.Autowired
    public CommonMapper __commonMapper;


    
    public CouponItemEntity toCouponItemEntity(CouponItem couponItem, MappingContext context) {
    
        if (couponItem == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        CouponItemEntity __result = new CouponItemEntity();
    
        {
        
            CartItemId __sourceProperty = couponItem.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCartItemId(__commonMapper.fromCartItemIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CouponItem.class);
                __event.setSourcePropertyType(CartItemId.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CouponItemEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("cartItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCartItemId(__commonMapper.fromCartItemIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            CouponId __sourceProperty = couponItem.getCoupon();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCouponCode(__commonMapper.fromCouponToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CouponItem.class);
                __event.setSourcePropertyType(CouponId.class);
                __event.setSourcePropertyName("coupon");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CouponItemEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("couponCode");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCouponCode(__commonMapper.fromCouponToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = couponItem.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CouponItem.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CouponItemEntity.class);
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
        
            Date __sourceProperty = couponItem.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CouponItem.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CouponItemEntity.class);
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
        
        return __result;
    }

    
    public CouponItem toCouponItemModel(CouponItemEntity couponItemEntity, MappingContext context) {
    
        if (couponItemEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        CouponItem __result = new CouponItem();
    
        {
        
            Long __sourceProperty = couponItemEntity.getCartItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.fromLongToCartItemId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CouponItemEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("cartItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CouponItem.class);
                __event.setTargetPropertyType(CartItemId.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.fromLongToCartItemId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = couponItemEntity.getCouponCode();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCoupon(__commonMapper.fromStringToCouponId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CouponItemEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("couponCode");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CouponItem.class);
                __event.setTargetPropertyType(CouponId.class);
                __event.setTargetPropertyName("coupon");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCoupon(__commonMapper.fromStringToCouponId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = couponItemEntity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CouponItemEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CouponItem.class);
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
        
            Date __sourceProperty = couponItemEntity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CouponItemEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CouponItem.class);
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
        
        return __result;
    }

    
    public OfferItemEntity toOfferItemEntity(OfferItem offerItem, MappingContext context) {
    
        if (offerItem == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        OfferItemEntity __result = new OfferItemEntity();
    
        {
        
            CartItemId __sourceProperty = offerItem.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCartItemId(__commonMapper.fromCartItemIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OfferItem.class);
                __event.setSourcePropertyType(CartItemId.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OfferItemEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("cartItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCartItemId(__commonMapper.fromCartItemIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            OfferId __sourceProperty = offerItem.getOffer();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOfferId(__commonMapper.fromOfferToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OfferItem.class);
                __event.setSourcePropertyType(OfferId.class);
                __event.setSourcePropertyName("offer");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OfferItemEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("offerId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOfferId(__commonMapper.fromOfferToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = offerItem.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OfferItem.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OfferItemEntity.class);
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
        
            Date __sourceProperty = offerItem.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OfferItem.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OfferItemEntity.class);
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
        
            Long __sourceProperty = offerItem.getQuantity();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setQuantity(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OfferItem.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("quantity");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OfferItemEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("quantity");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setQuantity(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Boolean __sourceProperty = offerItem.getSelected();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setSelected(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OfferItem.class);
                __event.setSourcePropertyType(Boolean.class);
                __event.setSourcePropertyName("selected");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OfferItemEntity.class);
                __event.setTargetPropertyType(Boolean.class);
                __event.setTargetPropertyName("selected");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setSelected(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public OfferItem toOfferItemModel(OfferItemEntity offerItemEntity, MappingContext context) {
    
        if (offerItemEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        OfferItem __result = new OfferItem();
    
        {
        
            Long __sourceProperty = offerItemEntity.getCartItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.fromLongToCartItemId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OfferItemEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("cartItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OfferItem.class);
                __event.setTargetPropertyType(CartItemId.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.fromLongToCartItemId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = offerItemEntity.getOfferId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOffer(__commonMapper.fromLongToOffer(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OfferItemEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("offerId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OfferItem.class);
                __event.setTargetPropertyType(OfferId.class);
                __event.setTargetPropertyName("offer");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOffer(__commonMapper.fromLongToOffer(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = offerItemEntity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OfferItemEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OfferItem.class);
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
        
            Date __sourceProperty = offerItemEntity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OfferItemEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OfferItem.class);
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
        
            Long __sourceProperty = offerItemEntity.getQuantity();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setQuantity(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OfferItemEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("quantity");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OfferItem.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("quantity");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setQuantity(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Boolean __sourceProperty = offerItemEntity.getSelected();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setSelected(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(OfferItemEntity.class);
                __event.setSourcePropertyType(Boolean.class);
                __event.setSourcePropertyName("selected");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(OfferItem.class);
                __event.setTargetPropertyType(Boolean.class);
                __event.setTargetPropertyName("selected");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setSelected(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public CartEntity toCartEntity(Cart cart, MappingContext context) {
    
        if (cart == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        CartEntity __result = new CartEntity();
    
        {
        
            UserId __sourceProperty = cart.getUser();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__commonMapper.fromUserIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Cart.class);
                __event.setSourcePropertyType(UserId.class);
                __event.setSourcePropertyName("user");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CartEntity.class);
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
        
            CartId __sourceProperty = cart.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.fromCartIdToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Cart.class);
                __event.setSourcePropertyType(CartId.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CartEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.fromCartIdToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Boolean __sourceProperty = cart.getUserLoggedIn();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserLoggedIn(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Cart.class);
                __event.setSourcePropertyType(Boolean.class);
                __event.setSourcePropertyName("userLoggedIn");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CartEntity.class);
                __event.setTargetPropertyType(Boolean.class);
                __event.setTargetPropertyName("userLoggedIn");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserLoggedIn(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = cart.getClientId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setClientId(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Cart.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("clientId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CartEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("clientId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setClientId(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = cart.getCartName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCartName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Cart.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("cartName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CartEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("cartName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCartName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = cart.getResourceAge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResourceAge(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Cart.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("resourceAge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CartEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("resourceAge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResourceAge(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = cart.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Cart.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CartEntity.class);
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
        
            Date __sourceProperty = cart.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Cart.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CartEntity.class);
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
        
        return __result;
    }

    
    public Cart toCartModel(CartEntity cartEntity, MappingContext context) {
    
        if (cartEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        Cart __result = new Cart();
    
        {
        
            Long __sourceProperty = cartEntity.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUser(__commonMapper.fromLongToUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CartEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Cart.class);
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
        
            Long __sourceProperty = cartEntity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.fromLongToCartId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CartEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Cart.class);
                __event.setTargetPropertyType(CartId.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.fromLongToCartId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = cartEntity.getClientId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setClientId(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CartEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("clientId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Cart.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("clientId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setClientId(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Boolean __sourceProperty = cartEntity.getUserLoggedIn();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserLoggedIn(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CartEntity.class);
                __event.setSourcePropertyType(Boolean.class);
                __event.setSourcePropertyName("userLoggedIn");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Cart.class);
                __event.setTargetPropertyType(Boolean.class);
                __event.setTargetPropertyName("userLoggedIn");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserLoggedIn(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = cartEntity.getCartName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCartName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CartEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("cartName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Cart.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("cartName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCartName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = cartEntity.getResourceAge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResourceAge(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CartEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("resourceAge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Cart.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("resourceAge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResourceAge(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = cartEntity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CartEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Cart.class);
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
        
            Date __sourceProperty = cartEntity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CartEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Cart.class);
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
        
        return __result;
    }
}
