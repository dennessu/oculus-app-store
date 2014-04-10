

package com.junbo.payment.db.mapper;

import com.junbo.oom.core.filter.ElementMappingEvent;
import com.junbo.oom.core.filter.ElementMappingFilter;
import com.junbo.oom.core.filter.PropertyMappingEvent;
import com.junbo.oom.core.filter.PropertyMappingFilter;

import com.junbo.oom.core.builtin.BuiltinMapper;
import com.junbo.payment.db.mapper.CommonMapper;
import com.junbo.payment.db.entity.PaymentInstrumentTypeEntity;
import com.junbo.oom.core.MappingContext;
import com.junbo.payment.spec.model.PaymentInstrumentType;
import java.lang.String;
import com.junbo.payment.db.entity.TrackingUuidEntity;
import com.junbo.payment.db.mapper.TrackingUuid;
import java.lang.Long;
import java.util.UUID;
import com.junbo.payment.db.mapper.PaymentAPI;
import com.junbo.payment.db.entity.paymentinstrument.PaymentInstrumentEntity;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PIId;
import com.junbo.payment.spec.enums.PIType;
import com.junbo.payment.spec.enums.PIStatus;
import java.util.Date;
import com.junbo.payment.db.entity.payment.PaymentEntity;
import com.junbo.payment.spec.model.PaymentTransaction;
import java.lang.Integer;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.enums.PaymentType;
import com.junbo.payment.db.entity.payment.PaymentEventEntity;
import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.payment.spec.enums.PaymentEventType;
import com.junbo.payment.db.entity.CurrencyTypeEntity;
import com.junbo.payment.db.mapper.Currency;
import java.math.BigDecimal;
import com.junbo.payment.db.entity.CountryTypeEntity;
import com.junbo.payment.db.mapper.Country;
import com.junbo.payment.db.entity.paymentinstrument.PhoneEntity;
import com.junbo.payment.spec.model.Phone;
import com.junbo.payment.spec.enums.PhoneType;
import com.junbo.payment.db.entity.paymentinstrument.CreditCardPaymentInstrumentEntity;
import com.junbo.payment.spec.model.CreditCardRequest;
import com.junbo.payment.spec.enums.CreditCardType;
import com.junbo.payment.db.entity.paymentinstrument.AddressEntity;
import com.junbo.payment.spec.model.Address;

@org.springframework.stereotype.Component("com.junbo.payment.db.mapper.PaymentMapperImpl")
public class PaymentMapperImpl implements PaymentMapper {


    @org.springframework.beans.factory.annotation.Autowired
    public BuiltinMapper __builtinMapper;

    @org.springframework.beans.factory.annotation.Autowired
    public CommonMapper __commonMapper;


    
    public PaymentInstrumentType toPIType(PaymentInstrumentTypeEntity entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        PaymentInstrumentType __result = new PaymentInstrumentType();
    
        {
        
            String __sourceProperty = entity.getDefaultable();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDefaultable(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentTypeEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("defaultable");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentType.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("defaultable");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDefaultable(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentTypeEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("name");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentType.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("name");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getRecurring();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setRecurring(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentTypeEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("recurring");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentType.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("recurring");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setRecurring(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getRefundable();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setRefundable(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentTypeEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("refundable");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentType.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("refundable");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setRefundable(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getAuthorizable();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAuthorizable(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentTypeEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("authorizable");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentType.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("authorizable");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAuthorizable(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public PaymentInstrumentTypeEntity toPITypeEntity(PaymentInstrumentType piType, MappingContext context) {
    
        if (piType == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        PaymentInstrumentTypeEntity __result = new PaymentInstrumentTypeEntity();
    
        {
        
            String __sourceProperty = piType.getDefaultable();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDefaultable(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentType.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("defaultable");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentTypeEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("defaultable");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDefaultable(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = piType.getName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentType.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("name");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentTypeEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("name");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = piType.getRecurring();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setRecurring(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentType.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("recurring");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentTypeEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("recurring");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setRecurring(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = piType.getRefundable();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setRefundable(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentType.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("refundable");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentTypeEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("refundable");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setRefundable(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = piType.getAuthorizable();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAuthorizable(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentType.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("authorizable");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentTypeEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("authorizable");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAuthorizable(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public TrackingUuid toTrackingUuid(TrackingUuidEntity entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        TrackingUuid __result = new TrackingUuid();
    
        {
        
            Long __sourceProperty = entity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TrackingUuidEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TrackingUuid.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UUID __sourceProperty = entity.getTrackingUuid();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTrackingUuid(__commonMapper.toUUID(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TrackingUuidEntity.class);
                __event.setSourcePropertyType(UUID.class);
                __event.setSourcePropertyName("trackingUuid");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TrackingUuid.class);
                __event.setTargetPropertyType(UUID.class);
                __event.setTargetPropertyName("trackingUuid");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTrackingUuid(__commonMapper.toUUID(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TrackingUuidEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TrackingUuid.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            PaymentAPI __sourceProperty = entity.getApi();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setApi(__commonMapper.toPaymentAPIEunm(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TrackingUuidEntity.class);
                __event.setSourcePropertyType(PaymentAPI.class);
                __event.setSourcePropertyName("api");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TrackingUuid.class);
                __event.setTargetPropertyType(PaymentAPI.class);
                __event.setTargetPropertyName("api");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setApi(__commonMapper.toPaymentAPIEunm(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getPaymentInstrumentId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPaymentInstrumentId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TrackingUuidEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("paymentInstrumentId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TrackingUuid.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("paymentInstrumentId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPaymentInstrumentId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getPaymentId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPaymentId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TrackingUuidEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("paymentId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TrackingUuid.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("paymentId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPaymentId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getResponse();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResponse(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TrackingUuidEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("response");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TrackingUuid.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("response");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResponse(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public TrackingUuidEntity toTrackingUuidEntity(TrackingUuid trackingUuid, MappingContext context) {
    
        if (trackingUuid == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        TrackingUuidEntity __result = new TrackingUuidEntity();
    
        {
        
            Long __sourceProperty = trackingUuid.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TrackingUuid.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TrackingUuidEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UUID __sourceProperty = trackingUuid.getTrackingUuid();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTrackingUuid(__commonMapper.toUUID(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TrackingUuid.class);
                __event.setSourcePropertyType(UUID.class);
                __event.setSourcePropertyName("trackingUuid");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TrackingUuidEntity.class);
                __event.setTargetPropertyType(UUID.class);
                __event.setTargetPropertyName("trackingUuid");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTrackingUuid(__commonMapper.toUUID(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = trackingUuid.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TrackingUuid.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TrackingUuidEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            PaymentAPI __sourceProperty = trackingUuid.getApi();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setApi(__commonMapper.toPaymentAPIEunm(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TrackingUuid.class);
                __event.setSourcePropertyType(PaymentAPI.class);
                __event.setSourcePropertyName("api");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TrackingUuidEntity.class);
                __event.setTargetPropertyType(PaymentAPI.class);
                __event.setTargetPropertyName("api");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setApi(__commonMapper.toPaymentAPIEunm(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = trackingUuid.getPaymentInstrumentId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPaymentInstrumentId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TrackingUuid.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("paymentInstrumentId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TrackingUuidEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("paymentInstrumentId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPaymentInstrumentId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = trackingUuid.getPaymentId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPaymentId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TrackingUuid.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("paymentId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TrackingUuidEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("paymentId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPaymentId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = trackingUuid.getResponse();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResponse(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TrackingUuid.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("response");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TrackingUuidEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("response");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResponse(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public PaymentInstrument toPaymentInstrumentRaw(PaymentInstrumentEntity piEntity, MappingContext context) {
    
        if (piEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        PaymentInstrument __result = new PaymentInstrument();
    
        {
        
            Long __sourceProperty = piEntity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toPaymentInstrument(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrument.class);
                __event.setTargetPropertyType(PIId.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toPaymentInstrument(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            PIType __sourceProperty = piEntity.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.toPIType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentEntity.class);
                __event.setSourcePropertyType(PIType.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrument.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.toPIType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = piEntity.getAccountName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAccountName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("accountName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrument.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("accountName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAccountName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = piEntity.getAccountNum();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAccountNum(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("accountNum");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrument.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("accountNum");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAccountNum(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = piEntity.getEmail();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setEmail(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("email");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrument.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("email");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setEmail(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = piEntity.getRelationToHolder();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setRelationToHolder(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("relationToHolder");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrument.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("relationToHolder");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setRelationToHolder(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            PIStatus __sourceProperty = piEntity.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.toPIStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentEntity.class);
                __event.setSourcePropertyType(PIStatus.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrument.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.toPIStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = piEntity.getLastValidatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setLastValidatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("lastValidatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrument.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("lastValidatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setLastValidatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = piEntity.getIsDefault();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setIsDefault(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrumentEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("isDefault");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrument.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("isDefault");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setIsDefault(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public PaymentInstrumentEntity toPIEntityRaw(PaymentInstrument piRequest, MappingContext context) {
    
        if (piRequest == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        PaymentInstrumentEntity __result = new PaymentInstrumentEntity();
    
        {
        
            PIId __sourceProperty = piRequest.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toPaymentInstrumentId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrument.class);
                __event.setSourcePropertyType(PIId.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toPaymentInstrumentId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = piRequest.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.toPITypeEnum(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrument.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentEntity.class);
                __event.setTargetPropertyType(PIType.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.toPITypeEnum(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = piRequest.getAccountName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAccountName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrument.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("accountName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("accountName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAccountName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = piRequest.getAccountNum();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAccountNum(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrument.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("accountNum");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("accountNum");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAccountNum(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = piRequest.getEmail();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setEmail(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrument.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("email");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("email");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setEmail(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = piRequest.getRelationToHolder();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setRelationToHolder(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrument.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("relationToHolder");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("relationToHolder");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setRelationToHolder(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = piRequest.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.toPIStatusEnum(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrument.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentEntity.class);
                __event.setTargetPropertyType(PIStatus.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.toPIStatusEnum(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = piRequest.getLastValidatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setLastValidatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrument.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("lastValidatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("lastValidatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setLastValidatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = piRequest.getIsDefault();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setIsDefault(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentInstrument.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("isDefault");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentInstrumentEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("isDefault");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setIsDefault(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public PaymentTransaction toPaymentRaw(PaymentEntity paymentEntity, MappingContext context) {
    
        if (paymentEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        PaymentTransaction __result = new PaymentTransaction();
    
        {
        
            Integer __sourceProperty = paymentEntity.getMerchantAccountId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setMerchantAccount(__commonMapper.explicitMethod_toMerchantName(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEntity.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("merchantAccountId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentTransaction.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("merchantAccount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setMerchantAccount(__commonMapper.explicitMethod_toMerchantName(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = paymentEntity.getPaymentProviderId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPaymentProvider(__commonMapper.explicitMethod_toProviderName(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEntity.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("paymentProviderId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentTransaction.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("paymentProvider");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPaymentProvider(__commonMapper.explicitMethod_toProviderName(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = paymentEntity.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentTransaction.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = paymentEntity.getPaymentInstrumentId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPaymentInstrumentId(__commonMapper.toPaymentInstrument(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("paymentInstrumentId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentTransaction.class);
                __event.setTargetPropertyType(PIId.class);
                __event.setTargetPropertyName("paymentInstrumentId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPaymentInstrumentId(__commonMapper.toPaymentInstrument(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = paymentEntity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentTransaction.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            PaymentStatus __sourceProperty = paymentEntity.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.toPaymentStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEntity.class);
                __event.setSourcePropertyType(PaymentStatus.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentTransaction.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.toPaymentStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = paymentEntity.getExternalToken();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setExternalToken(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("externalToken");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentTransaction.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("externalToken");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setExternalToken(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = paymentEntity.getBillingRefId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setBillingRefId(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("billingRefId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentTransaction.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("billingRefId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setBillingRefId(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            PaymentType __sourceProperty = paymentEntity.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.toPaymentType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEntity.class);
                __event.setSourcePropertyType(PaymentType.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentTransaction.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.toPaymentType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public PaymentEntity toPaymentEntityRaw(PaymentTransaction request, MappingContext context) {
    
        if (request == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        PaymentEntity __result = new PaymentEntity();
    
        {
        
            String __sourceProperty = request.getMerchantAccount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setMerchantAccountId(__commonMapper.explicitMethod_toMerchantId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentTransaction.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("merchantAccount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEntity.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("merchantAccountId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setMerchantAccountId(__commonMapper.explicitMethod_toMerchantId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = request.getPaymentProvider();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPaymentProviderId(__commonMapper.explicitMethod_toProviderId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentTransaction.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("paymentProvider");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEntity.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("paymentProviderId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPaymentProviderId(__commonMapper.explicitMethod_toProviderId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = request.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentTransaction.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            PIId __sourceProperty = request.getPaymentInstrumentId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPaymentInstrumentId(__commonMapper.toPaymentInstrumentId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentTransaction.class);
                __event.setSourcePropertyType(PIId.class);
                __event.setSourcePropertyName("paymentInstrumentId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("paymentInstrumentId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPaymentInstrumentId(__commonMapper.toPaymentInstrumentId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = request.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentTransaction.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = request.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.toPaymentStatusEnum(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentTransaction.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEntity.class);
                __event.setTargetPropertyType(PaymentStatus.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.toPaymentStatusEnum(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = request.getExternalToken();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setExternalToken(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentTransaction.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("externalToken");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("externalToken");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setExternalToken(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = request.getBillingRefId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setBillingRefId(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentTransaction.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("billingRefId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("billingRefId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setBillingRefId(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = request.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.toPaymentEnum(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentTransaction.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEntity.class);
                __event.setTargetPropertyType(PaymentType.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.toPaymentEnum(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public PaymentEvent toPaymentEventRaw(PaymentEventEntity eventEntity, MappingContext context) {
    
        if (eventEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        PaymentEvent __result = new PaymentEvent();
    
        {
        
            Long __sourceProperty = eventEntity.getPaymentId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPaymentId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEventEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("paymentId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEvent.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("paymentId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPaymentId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            PaymentEventType __sourceProperty = eventEntity.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.toPaymentEventType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEventEntity.class);
                __event.setSourcePropertyType(PaymentEventType.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.toPaymentEventType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            PaymentStatus __sourceProperty = eventEntity.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.toPaymentStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEventEntity.class);
                __event.setSourcePropertyType(PaymentStatus.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.toPaymentStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = eventEntity.getRequest();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setRequest(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEventEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("request");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("request");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setRequest(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = eventEntity.getResponse();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResponse(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEventEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("response");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEvent.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("response");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResponse(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public PaymentEventEntity toPaymentEventEntityRaw(PaymentEvent event, MappingContext context) {
    
        if (event == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        PaymentEventEntity __result = new PaymentEventEntity();
    
        {
        
            Long __sourceProperty = event.getPaymentId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPaymentId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEvent.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("paymentId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEventEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("paymentId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPaymentId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = event.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.toPaymentEventTypeEnum(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEventEntity.class);
                __event.setTargetPropertyType(PaymentEventType.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.toPaymentEventTypeEnum(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = event.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.toPaymentStatusEnum(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEventEntity.class);
                __event.setTargetPropertyType(PaymentStatus.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.toPaymentStatusEnum(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = event.getRequest();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setRequest(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("request");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEventEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("request");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setRequest(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = event.getResponse();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResponse(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PaymentEvent.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("response");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PaymentEventEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("response");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResponse(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public Currency toCurrency(CurrencyTypeEntity currencyEntity, MappingContext context) {
    
        if (currencyEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        Currency __result = new Currency();
    
        {
        
            Integer __sourceProperty = currencyEntity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__builtinMapper.fromIntegerToInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CurrencyTypeEntity.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Currency.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__builtinMapper.fromIntegerToInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = currencyEntity.getCurrencyCode();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCurrencyCode(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CurrencyTypeEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("currencyCode");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Currency.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("currencyCode");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCurrencyCode(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = currencyEntity.getMinAuthAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setMinAuthAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CurrencyTypeEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("minAuthAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Currency.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("minAuthAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setMinAuthAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = currencyEntity.getBaseUnit();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setBaseUnit(__builtinMapper.fromIntegerToInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CurrencyTypeEntity.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("baseUnit");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Currency.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("baseUnit");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setBaseUnit(__builtinMapper.fromIntegerToInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public CurrencyTypeEntity toCurrencyEntity(Currency currency, MappingContext context) {
    
        if (currency == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        CurrencyTypeEntity __result = new CurrencyTypeEntity();
    
        {
        
            Integer __sourceProperty = currency.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__builtinMapper.fromIntegerToInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Currency.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CurrencyTypeEntity.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__builtinMapper.fromIntegerToInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = currency.getCurrencyCode();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCurrencyCode(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Currency.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("currencyCode");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CurrencyTypeEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("currencyCode");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCurrencyCode(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = currency.getMinAuthAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setMinAuthAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Currency.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("minAuthAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CurrencyTypeEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("minAuthAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setMinAuthAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = currency.getBaseUnit();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setBaseUnit(__builtinMapper.fromIntegerToInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Currency.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("baseUnit");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CurrencyTypeEntity.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("baseUnit");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setBaseUnit(__builtinMapper.fromIntegerToInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public Country toCountry(CountryTypeEntity countryEntity, MappingContext context) {
    
        if (countryEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        Country __result = new Country();
    
        {
        
            Integer __sourceProperty = countryEntity.getDefaultCurrencyId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDefaultCurrencyId(__builtinMapper.fromIntegerToInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CountryTypeEntity.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("defaultCurrencyId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Country.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("defaultCurrencyId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDefaultCurrencyId(__builtinMapper.fromIntegerToInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = countryEntity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__builtinMapper.fromIntegerToInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CountryTypeEntity.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Country.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__builtinMapper.fromIntegerToInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = countryEntity.getCountryCode();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCountryCode(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CountryTypeEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("countryCode");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Country.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("countryCode");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCountryCode(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = countryEntity.getCountry3Code();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCountry3Code(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CountryTypeEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("country3Code");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Country.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("country3Code");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCountry3Code(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = countryEntity.getCountryName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCountryName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CountryTypeEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("countryName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Country.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("countryName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCountryName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public CountryTypeEntity toCountryEntity(Country country, MappingContext context) {
    
        if (country == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        CountryTypeEntity __result = new CountryTypeEntity();
    
        {
        
            Integer __sourceProperty = country.getDefaultCurrencyId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDefaultCurrencyId(__builtinMapper.fromIntegerToInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Country.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("defaultCurrencyId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CountryTypeEntity.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("defaultCurrencyId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDefaultCurrencyId(__builtinMapper.fromIntegerToInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = country.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__builtinMapper.fromIntegerToInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Country.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CountryTypeEntity.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__builtinMapper.fromIntegerToInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = country.getCountryCode();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCountryCode(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Country.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("countryCode");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CountryTypeEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("countryCode");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCountryCode(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = country.getCountry3Code();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCountry3Code(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Country.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("country3Code");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CountryTypeEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("country3Code");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCountry3Code(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = country.getCountryName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCountryName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Country.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("countryName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CountryTypeEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("countryName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCountryName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public Phone toPhone(PhoneEntity phoneEntity, MappingContext context) {
    
        if (phoneEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        Phone __result = new Phone();
    
        {
        
            Long __sourceProperty = phoneEntity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PhoneEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Phone.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            PhoneType __sourceProperty = phoneEntity.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.toPhoneType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PhoneEntity.class);
                __event.setSourcePropertyType(PhoneType.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Phone.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.toPhoneType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = phoneEntity.getAreaCode();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAreaCode(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PhoneEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("areaCode");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Phone.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("areaCode");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAreaCode(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = phoneEntity.getNumber();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setNumber(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PhoneEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("number");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Phone.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("number");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setNumber(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public PhoneEntity toPhoneEntity(Phone phone, MappingContext context) {
    
        if (phone == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        PhoneEntity __result = new PhoneEntity();
    
        {
        
            Long __sourceProperty = phone.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Phone.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PhoneEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = phone.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.toPhoneTypeEnum(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Phone.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PhoneEntity.class);
                __event.setTargetPropertyType(PhoneType.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.toPhoneTypeEnum(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = phone.getAreaCode();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAreaCode(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Phone.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("areaCode");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PhoneEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("areaCode");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAreaCode(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = phone.getNumber();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setNumber(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Phone.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("number");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PhoneEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("number");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setNumber(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public CreditCardRequest toCreditCardRequest(CreditCardPaymentInstrumentEntity ccEntity, MappingContext context) {
    
        if (ccEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        CreditCardRequest __result = new CreditCardRequest();
    
        {
        
            Long __sourceProperty = ccEntity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardPaymentInstrumentEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardRequest.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = ccEntity.getExpireDate();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setExpireDate(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardPaymentInstrumentEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("expireDate");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardRequest.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("expireDate");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setExpireDate(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = ccEntity.getExternalToken();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setExternalToken(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardPaymentInstrumentEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("externalToken");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardRequest.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("externalToken");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setExternalToken(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            CreditCardType __sourceProperty = ccEntity.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.toCreditCardType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardPaymentInstrumentEntity.class);
                __event.setSourcePropertyType(CreditCardType.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardRequest.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.toCreditCardType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = ccEntity.getPrepaid();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPrepaid(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardPaymentInstrumentEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("prepaid");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardRequest.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("prepaid");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPrepaid(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = ccEntity.getDebit();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDebit(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardPaymentInstrumentEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("debit");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardRequest.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("debit");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDebit(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = ccEntity.getCommercial();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCommercial(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardPaymentInstrumentEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("commercial");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardRequest.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("commercial");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCommercial(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = ccEntity.getIssueCountry();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setIssueCountry(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardPaymentInstrumentEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("issueCountry");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardRequest.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("issueCountry");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setIssueCountry(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = ccEntity.getLastBillingDate();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setLastBillingDate(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardPaymentInstrumentEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("lastBillingDate");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardRequest.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("lastBillingDate");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setLastBillingDate(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public CreditCardPaymentInstrumentEntity toCreditCardEntity(CreditCardRequest ccRequest, MappingContext context) {
    
        if (ccRequest == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        CreditCardPaymentInstrumentEntity __result = new CreditCardPaymentInstrumentEntity();
    
        {
        
            Long __sourceProperty = ccRequest.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardRequest.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardPaymentInstrumentEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = ccRequest.getExpireDate();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setExpireDate(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardRequest.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("expireDate");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardPaymentInstrumentEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("expireDate");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setExpireDate(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = ccRequest.getExternalToken();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setExternalToken(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardRequest.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("externalToken");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardPaymentInstrumentEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("externalToken");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setExternalToken(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = ccRequest.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.toCreditCardTypeEnum(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardRequest.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardPaymentInstrumentEntity.class);
                __event.setTargetPropertyType(CreditCardType.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.toCreditCardTypeEnum(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = ccRequest.getPrepaid();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPrepaid(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardRequest.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("prepaid");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardPaymentInstrumentEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("prepaid");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPrepaid(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = ccRequest.getDebit();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDebit(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardRequest.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("debit");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardPaymentInstrumentEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("debit");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDebit(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = ccRequest.getCommercial();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCommercial(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardRequest.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("commercial");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardPaymentInstrumentEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("commercial");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCommercial(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = ccRequest.getIssueCountry();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setIssueCountry(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardRequest.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("issueCountry");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardPaymentInstrumentEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("issueCountry");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setIssueCountry(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = ccRequest.getLastBillingDate();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setLastBillingDate(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CreditCardRequest.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("lastBillingDate");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(CreditCardPaymentInstrumentEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("lastBillingDate");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setLastBillingDate(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public Address toAddress(AddressEntity addressEntity, MappingContext context) {
    
        if (addressEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        Address __result = new Address();
    
        {
        
            Long __sourceProperty = addressEntity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AddressEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Address.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = addressEntity.getUnitNumber();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUnitNumber(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("unitNumber");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Address.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("unitNumber");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUnitNumber(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = addressEntity.getAddressLine1();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAddressLine1(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("addressLine1");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Address.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("addressLine1");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAddressLine1(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = addressEntity.getAddressLine2();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAddressLine2(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("addressLine2");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Address.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("addressLine2");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAddressLine2(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = addressEntity.getAddressLine3();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAddressLine3(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("addressLine3");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Address.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("addressLine3");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAddressLine3(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = addressEntity.getCity();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCity(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("city");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Address.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("city");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCity(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = addressEntity.getDistrict();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDistrict(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("district");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Address.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("district");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDistrict(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = addressEntity.getState();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setState(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("state");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Address.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("state");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setState(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = addressEntity.getCountry();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCountry(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("country");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Address.class);
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
        
            String __sourceProperty = addressEntity.getPostalCode();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPostalCode(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("postalCode");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Address.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("postalCode");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPostalCode(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public AddressEntity toAddressEntity(Address address, MappingContext context) {
    
        if (address == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        AddressEntity __result = new AddressEntity();
    
        {
        
            Long __sourceProperty = address.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Address.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AddressEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = address.getUnitNumber();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUnitNumber(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Address.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("unitNumber");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("unitNumber");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUnitNumber(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = address.getAddressLine1();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAddressLine1(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Address.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("addressLine1");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("addressLine1");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAddressLine1(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = address.getAddressLine2();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAddressLine2(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Address.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("addressLine2");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("addressLine2");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAddressLine2(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = address.getAddressLine3();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAddressLine3(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Address.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("addressLine3");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("addressLine3");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAddressLine3(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = address.getCity();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCity(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Address.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("city");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("city");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCity(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = address.getDistrict();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDistrict(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Address.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("district");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("district");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDistrict(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = address.getState();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setState(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Address.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("state");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("state");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setState(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = address.getCountry();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCountry(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Address.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("country");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AddressEntity.class);
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
        
            String __sourceProperty = address.getPostalCode();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPostalCode(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Address.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("postalCode");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("postalCode");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPostalCode(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }
}
