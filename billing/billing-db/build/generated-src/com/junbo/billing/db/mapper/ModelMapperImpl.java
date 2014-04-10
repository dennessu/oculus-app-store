

package com.junbo.billing.db.mapper;

import com.junbo.oom.core.filter.ElementMappingEvent;
import com.junbo.oom.core.filter.ElementMappingFilter;
import com.junbo.oom.core.filter.PropertyMappingEvent;
import com.junbo.oom.core.filter.PropertyMappingFilter;

import com.junbo.oom.core.builtin.BuiltinMapper;
import com.junbo.billing.db.mapper.CommonMapper;
import com.junbo.billing.spec.model.Transaction;
import com.junbo.oom.core.MappingContext;
import com.junbo.billing.db.entity.TransactionEntity;
import java.lang.String;
import java.lang.Short;
import com.junbo.common.id.TransactionId;
import java.lang.Long;
import com.junbo.common.id.BalanceId;
import com.junbo.common.id.PaymentInstrumentId;
import java.math.BigDecimal;
import java.util.Date;
import com.junbo.billing.db.entity.DiscountItemEntity;
import com.junbo.billing.spec.model.DiscountItem;
import com.junbo.billing.db.entity.TaxItemEntity;
import com.junbo.billing.spec.model.TaxItem;
import com.junbo.billing.db.entity.BalanceItemEntity;
import com.junbo.billing.spec.model.BalanceItem;
import com.junbo.common.id.OrderItemId;
import java.lang.Boolean;
import com.junbo.billing.db.entity.BalanceEntity;
import com.junbo.billing.spec.model.Balance;
import java.util.UUID;
import com.junbo.common.id.UserId;
import com.junbo.common.id.ShippingAddressId;
import com.junbo.billing.spec.model.ShippingAddress;
import com.junbo.billing.db.entity.ShippingAddressEntity;
import com.junbo.billing.db.entity.CurrencyEntity;
import com.junbo.billing.spec.model.Currency;
import java.lang.Integer;
import java.lang.Double;

@org.springframework.stereotype.Component("com.junbo.billing.db.mapper.ModelMapperImpl")
public class ModelMapperImpl implements ModelMapper {


    @org.springframework.beans.factory.annotation.Autowired
    public BuiltinMapper __builtinMapper;

    @org.springframework.beans.factory.annotation.Autowired
    public CommonMapper __commonMapper;


    
    public TransactionEntity toTransactionEntity(Transaction transaction, MappingContext context) {
    
        if (transaction == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        TransactionEntity __result = new TransactionEntity();
    
        {
        
            String __sourceProperty = transaction.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTypeId(__commonMapper.explicitMethod_convertTransactionType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Transaction.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TransactionEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("typeId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTypeId(__commonMapper.explicitMethod_convertTransactionType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = transaction.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatusId(__commonMapper.explicitMethod_convertTransactionStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Transaction.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TransactionEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("statusId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatusId(__commonMapper.explicitMethod_convertTransactionStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = transaction.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTypeId(__commonMapper.explicitMethod_convertTransactionType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Transaction.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TransactionEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("typeId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTypeId(__commonMapper.explicitMethod_convertTransactionType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = transaction.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatusId(__commonMapper.explicitMethod_convertTransactionStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Transaction.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TransactionEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("statusId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatusId(__commonMapper.explicitMethod_convertTransactionStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            TransactionId __sourceProperty = transaction.getTransactionId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTransactionId(__commonMapper.toTransactionIdLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Transaction.class);
                __event.setSourcePropertyType(TransactionId.class);
                __event.setSourcePropertyName("transactionId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TransactionEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("transactionId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTransactionId(__commonMapper.toTransactionIdLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BalanceId __sourceProperty = transaction.getBalanceId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setBalanceId(__commonMapper.toBalanceIdLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Transaction.class);
                __event.setSourcePropertyType(BalanceId.class);
                __event.setSourcePropertyName("balanceId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TransactionEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("balanceId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setBalanceId(__commonMapper.toBalanceIdLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            PaymentInstrumentId __sourceProperty = transaction.getPiId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPiId(__commonMapper.toPaymentInstrumentIdLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Transaction.class);
                __event.setSourcePropertyType(PaymentInstrumentId.class);
                __event.setSourcePropertyName("piId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TransactionEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("piId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPiId(__commonMapper.toPaymentInstrumentIdLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = transaction.getPaymentRefId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPaymentRefId(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Transaction.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("paymentRefId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TransactionEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("paymentRefId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPaymentRefId(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = transaction.getAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Transaction.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("amount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TransactionEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("amount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = transaction.getCurrency();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCurrency(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Transaction.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("currency");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TransactionEntity.class);
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
        
        return __result;
    }

    
    public Transaction toTransaction(TransactionEntity entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        Transaction __result = new Transaction();
    
        {
        
            Short __sourceProperty = entity.getTypeId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.explicitMethod_convertTransactionType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TransactionEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("typeId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Transaction.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.explicitMethod_convertTransactionType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Short __sourceProperty = entity.getStatusId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.explicitMethod_convertTransactionStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TransactionEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("statusId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Transaction.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.explicitMethod_convertTransactionStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = entity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTransactionTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TransactionEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Transaction.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("transactionTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTransactionTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Short __sourceProperty = entity.getTypeId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.explicitMethod_convertTransactionType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TransactionEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("typeId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Transaction.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.explicitMethod_convertTransactionType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Short __sourceProperty = entity.getStatusId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.explicitMethod_convertTransactionStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TransactionEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("statusId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Transaction.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.explicitMethod_convertTransactionStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getTransactionId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTransactionId(__commonMapper.toTransactionId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TransactionEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("transactionId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Transaction.class);
                __event.setTargetPropertyType(TransactionId.class);
                __event.setTargetPropertyName("transactionId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTransactionId(__commonMapper.toTransactionId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getBalanceId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setBalanceId(__commonMapper.toBalanceId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TransactionEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("balanceId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Transaction.class);
                __event.setTargetPropertyType(BalanceId.class);
                __event.setTargetPropertyName("balanceId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setBalanceId(__commonMapper.toBalanceId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getPiId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPiId(__commonMapper.toPaymentInstrumentId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TransactionEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("piId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Transaction.class);
                __event.setTargetPropertyType(PaymentInstrumentId.class);
                __event.setTargetPropertyName("piId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPiId(__commonMapper.toPaymentInstrumentId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getPaymentRefId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPaymentRefId(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TransactionEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("paymentRefId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Transaction.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("paymentRefId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPaymentRefId(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = entity.getAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TransactionEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("amount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Transaction.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("amount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getCurrency();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCurrency(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TransactionEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("currency");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Transaction.class);
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
        
        return __result;
    }

    
    public DiscountItem toDiscountItem(DiscountItemEntity entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        DiscountItem __result = new DiscountItem();
    
        {
        
            BigDecimal __sourceProperty = entity.getDiscountAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDiscountAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(DiscountItemEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("discountAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(DiscountItem.class);
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
        
            BigDecimal __sourceProperty = entity.getDiscountRate();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDiscountRate(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(DiscountItemEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("discountRate");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(DiscountItem.class);
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
        
        return __result;
    }

    
    public TaxItem toTaxItem(TaxItemEntity entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        TaxItem __result = new TaxItem();
    
        {
        
            Short __sourceProperty = entity.getTaxAuthorityId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxAuthority(__commonMapper.explicitMethod_convertTaxAuthority(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TaxItemEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("taxAuthorityId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TaxItem.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("taxAuthority");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxAuthority(__commonMapper.explicitMethod_convertTaxAuthority(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Short __sourceProperty = entity.getTaxAuthorityId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxAuthority(__commonMapper.explicitMethod_convertTaxAuthority(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TaxItemEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("taxAuthorityId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TaxItem.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("taxAuthority");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxAuthority(__commonMapper.explicitMethod_convertTaxAuthority(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = entity.getTaxAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TaxItemEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("taxAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TaxItem.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("taxAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = entity.getTaxRate();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxRate(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TaxItemEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("taxRate");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TaxItem.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("taxRate");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxRate(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public BalanceItem toBalanceItem(BalanceItemEntity entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        BalanceItem __result = new BalanceItem();
    
        {
        
            Long __sourceProperty = entity.getBalanceItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setBalanceItemId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceItemEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("balanceItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceItem.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("balanceItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setBalanceItemId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getOrderItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemId(__commonMapper.toOrderItemId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceItemEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("orderItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceItem.class);
                __event.setTargetPropertyType(OrderItemId.class);
                __event.setTargetPropertyName("orderItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemId(__commonMapper.toOrderItemId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = entity.getAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceItemEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("amount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceItem.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("amount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = entity.getTaxAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceItemEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("taxAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceItem.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("taxAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = entity.getDiscountAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDiscountAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceItemEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("discountAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceItem.class);
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
        
            String __sourceProperty = entity.getFinanceId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setFinanceId(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceItemEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("financeId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceItem.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("financeId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setFinanceId(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Boolean __sourceProperty = entity.getIsTaxExempt();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setIsTaxExempt(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceItemEntity.class);
                __event.setSourcePropertyType(Boolean.class);
                __event.setSourcePropertyName("isTaxExempt");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceItem.class);
                __event.setTargetPropertyType(Boolean.class);
                __event.setTargetPropertyName("isTaxExempt");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setIsTaxExempt(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getOriginalBalanceItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOriginalBalanceItemId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceItemEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("originalBalanceItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceItem.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("originalBalanceItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOriginalBalanceItemId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public Balance toBalance(BalanceEntity entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        Balance __result = new Balance();
    
        {
        
            Short __sourceProperty = entity.getTypeId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.explicitMethod_convertBalanceType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("typeId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.explicitMethod_convertBalanceType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Short __sourceProperty = entity.getStatusId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.explicitMethod_convertBalanceStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("statusId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.explicitMethod_convertBalanceStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Short __sourceProperty = entity.getTaxStatusId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxStatus(__commonMapper.explicitMethod_convertTaxStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("taxStatusId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("taxStatus");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxStatus(__commonMapper.explicitMethod_convertTaxStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Short __sourceProperty = entity.getTypeId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.explicitMethod_convertBalanceType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("typeId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.explicitMethod_convertBalanceType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Short __sourceProperty = entity.getStatusId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.explicitMethod_convertBalanceStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("statusId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.explicitMethod_convertBalanceStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Short __sourceProperty = entity.getTaxStatusId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxStatus(__commonMapper.explicitMethod_convertTaxStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("taxStatusId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("taxStatus");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxStatus(__commonMapper.explicitMethod_convertTaxStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getBalanceId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setBalanceId(__commonMapper.toBalanceId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("balanceId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
                __event.setTargetPropertyType(BalanceId.class);
                __event.setTargetPropertyName("balanceId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setBalanceId(__commonMapper.toBalanceId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UUID __sourceProperty = entity.getTrackingUuid();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTrackingUuid(__builtinMapper.fromUUIDToUUID(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(UUID.class);
                __event.setSourcePropertyName("trackingUuid");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
                __event.setTargetPropertyType(UUID.class);
                __event.setTargetPropertyName("trackingUuid");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTrackingUuid(__builtinMapper.fromUUIDToUUID(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__commonMapper.toUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
                __event.setTargetPropertyType(UserId.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__commonMapper.toUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getPiId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPiId(__commonMapper.toPaymentInstrumentId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("piId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
                __event.setTargetPropertyType(PaymentInstrumentId.class);
                __event.setTargetPropertyName("piId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPiId(__commonMapper.toPaymentInstrumentId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = entity.getTotalAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
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
        
            BigDecimal __sourceProperty = entity.getDiscountAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDiscountAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("discountAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
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
        
            BigDecimal __sourceProperty = entity.getTaxAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("taxAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("taxAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Boolean __sourceProperty = entity.getTaxIncluded();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxIncluded(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(Boolean.class);
                __event.setSourcePropertyName("taxIncluded");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
                __event.setTargetPropertyType(Boolean.class);
                __event.setTargetPropertyName("taxIncluded");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxIncluded(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getCurrency();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCurrency(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("currency");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
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
        
            String __sourceProperty = entity.getCountry();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCountry(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("country");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
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
        
            Date __sourceProperty = entity.getDueDate();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDueDate(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("dueDate");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("dueDate");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDueDate(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getOriginalBalanceId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOriginalBalanceId(__commonMapper.toBalanceId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("originalBalanceId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
                __event.setTargetPropertyType(BalanceId.class);
                __event.setTargetPropertyName("originalBalanceId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOriginalBalanceId(__commonMapper.toBalanceId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getShippingAddressId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setShippingAddressId(__commonMapper.toShippingAddressId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("shippingAddressId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
                __event.setTargetPropertyType(ShippingAddressId.class);
                __event.setTargetPropertyName("shippingAddressId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setShippingAddressId(__commonMapper.toShippingAddressId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Boolean __sourceProperty = entity.getIsAsyncCharge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setIsAsyncCharge(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceEntity.class);
                __event.setSourcePropertyType(Boolean.class);
                __event.setSourcePropertyName("isAsyncCharge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Balance.class);
                __event.setTargetPropertyType(Boolean.class);
                __event.setTargetPropertyName("isAsyncCharge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setIsAsyncCharge(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public DiscountItemEntity toDiscountItemEntity(DiscountItem discountItem, MappingContext context) {
    
        if (discountItem == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        DiscountItemEntity __result = new DiscountItemEntity();
    
        {
        
            BigDecimal __sourceProperty = discountItem.getDiscountAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDiscountAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(DiscountItem.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("discountAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(DiscountItemEntity.class);
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
        
            BigDecimal __sourceProperty = discountItem.getDiscountRate();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDiscountRate(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(DiscountItem.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("discountRate");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(DiscountItemEntity.class);
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
        
        return __result;
    }

    
    public TaxItemEntity toTaxItemEntity(TaxItem taxItem, MappingContext context) {
    
        if (taxItem == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        TaxItemEntity __result = new TaxItemEntity();
    
        {
        
            String __sourceProperty = taxItem.getTaxAuthority();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxAuthorityId(__commonMapper.explicitMethod_convertTaxAuthority(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TaxItem.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("taxAuthority");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TaxItemEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("taxAuthorityId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxAuthorityId(__commonMapper.explicitMethod_convertTaxAuthority(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = taxItem.getTaxAuthority();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxAuthorityId(__commonMapper.explicitMethod_convertTaxAuthority(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TaxItem.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("taxAuthority");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TaxItemEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("taxAuthorityId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxAuthorityId(__commonMapper.explicitMethod_convertTaxAuthority(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = taxItem.getTaxAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TaxItem.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("taxAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TaxItemEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("taxAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = taxItem.getTaxRate();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxRate(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(TaxItem.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("taxRate");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(TaxItemEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("taxRate");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxRate(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public BalanceItemEntity toBalanceItemEntity(BalanceItem balanceItem, MappingContext context) {
    
        if (balanceItem == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        BalanceItemEntity __result = new BalanceItemEntity();
    
        {
        
            Long __sourceProperty = balanceItem.getBalanceItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setBalanceItemId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceItem.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("balanceItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceItemEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("balanceItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setBalanceItemId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            OrderItemId __sourceProperty = balanceItem.getOrderItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrderItemId(__commonMapper.toOrderItemIdLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceItem.class);
                __event.setSourcePropertyType(OrderItemId.class);
                __event.setSourcePropertyName("orderItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceItemEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("orderItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrderItemId(__commonMapper.toOrderItemIdLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = balanceItem.getAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceItem.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("amount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceItemEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("amount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = balanceItem.getTaxAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceItem.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("taxAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceItemEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("taxAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = balanceItem.getDiscountAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDiscountAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceItem.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("discountAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceItemEntity.class);
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
        
            String __sourceProperty = balanceItem.getFinanceId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setFinanceId(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceItem.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("financeId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceItemEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("financeId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setFinanceId(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Boolean __sourceProperty = balanceItem.getIsTaxExempt();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setIsTaxExempt(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceItem.class);
                __event.setSourcePropertyType(Boolean.class);
                __event.setSourcePropertyName("isTaxExempt");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceItemEntity.class);
                __event.setTargetPropertyType(Boolean.class);
                __event.setTargetPropertyName("isTaxExempt");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setIsTaxExempt(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = balanceItem.getOriginalBalanceItemId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOriginalBalanceItemId(__builtinMapper.fromLongToLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(BalanceItem.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("originalBalanceItemId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceItemEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("originalBalanceItemId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOriginalBalanceItemId(__builtinMapper.fromLongToLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public BalanceEntity toBalanceEntity(Balance balance, MappingContext context) {
    
        if (balance == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        BalanceEntity __result = new BalanceEntity();
    
        {
        
            String __sourceProperty = balance.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTypeId(__commonMapper.explicitMethod_convertBalanceType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("typeId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTypeId(__commonMapper.explicitMethod_convertBalanceType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = balance.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatusId(__commonMapper.explicitMethod_convertBalanceStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("statusId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatusId(__commonMapper.explicitMethod_convertBalanceStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = balance.getTaxStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxStatusId(__commonMapper.explicitMethod_convertTaxStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("taxStatus");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("taxStatusId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxStatusId(__commonMapper.explicitMethod_convertTaxStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = balance.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTypeId(__commonMapper.explicitMethod_convertBalanceType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("typeId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTypeId(__commonMapper.explicitMethod_convertBalanceType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = balance.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatusId(__commonMapper.explicitMethod_convertBalanceStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("statusId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatusId(__commonMapper.explicitMethod_convertBalanceStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = balance.getTaxStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxStatusId(__commonMapper.explicitMethod_convertTaxStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("taxStatus");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("taxStatusId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxStatusId(__commonMapper.explicitMethod_convertTaxStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BalanceId __sourceProperty = balance.getBalanceId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setBalanceId(__commonMapper.toBalanceIdLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(BalanceId.class);
                __event.setSourcePropertyName("balanceId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("balanceId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setBalanceId(__commonMapper.toBalanceIdLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UUID __sourceProperty = balance.getTrackingUuid();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTrackingUuid(__builtinMapper.fromUUIDToUUID(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(UUID.class);
                __event.setSourcePropertyName("trackingUuid");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
                __event.setTargetPropertyType(UUID.class);
                __event.setTargetPropertyName("trackingUuid");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTrackingUuid(__builtinMapper.fromUUIDToUUID(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UserId __sourceProperty = balance.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__commonMapper.toUserIdLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(UserId.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__commonMapper.toUserIdLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            PaymentInstrumentId __sourceProperty = balance.getPiId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPiId(__commonMapper.toPaymentInstrumentIdLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(PaymentInstrumentId.class);
                __event.setSourcePropertyName("piId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("piId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPiId(__commonMapper.toPaymentInstrumentIdLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BigDecimal __sourceProperty = balance.getTotalAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTotalAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("totalAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
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
        
            BigDecimal __sourceProperty = balance.getDiscountAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDiscountAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("discountAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
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
        
            BigDecimal __sourceProperty = balance.getTaxAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(BigDecimal.class);
                __event.setSourcePropertyName("taxAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
                __event.setTargetPropertyType(BigDecimal.class);
                __event.setTargetPropertyName("taxAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxAmount(__builtinMapper.fromBigDecimalToBigDecimal(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Boolean __sourceProperty = balance.getTaxIncluded();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTaxIncluded(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(Boolean.class);
                __event.setSourcePropertyName("taxIncluded");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
                __event.setTargetPropertyType(Boolean.class);
                __event.setTargetPropertyName("taxIncluded");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTaxIncluded(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = balance.getCurrency();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCurrency(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("currency");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
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
        
            String __sourceProperty = balance.getCountry();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCountry(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("country");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
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
        
            Date __sourceProperty = balance.getDueDate();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDueDate(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("dueDate");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("dueDate");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDueDate(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            BalanceId __sourceProperty = balance.getOriginalBalanceId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOriginalBalanceId(__commonMapper.toBalanceIdLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(BalanceId.class);
                __event.setSourcePropertyName("originalBalanceId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("originalBalanceId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOriginalBalanceId(__commonMapper.toBalanceIdLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            ShippingAddressId __sourceProperty = balance.getShippingAddressId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setShippingAddressId(__commonMapper.toShippingAddressIdLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(ShippingAddressId.class);
                __event.setSourcePropertyName("shippingAddressId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("shippingAddressId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setShippingAddressId(__commonMapper.toShippingAddressIdLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Boolean __sourceProperty = balance.getIsAsyncCharge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setIsAsyncCharge(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(Balance.class);
                __event.setSourcePropertyType(Boolean.class);
                __event.setSourcePropertyName("isAsyncCharge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(BalanceEntity.class);
                __event.setTargetPropertyType(Boolean.class);
                __event.setTargetPropertyName("isAsyncCharge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setIsAsyncCharge(__builtinMapper.fromBooleanToBoolean(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public ShippingAddressEntity toShippingAddressEntity(ShippingAddress address, MappingContext context) {
    
        if (address == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        ShippingAddressEntity __result = new ShippingAddressEntity();
    
        {
        
            ShippingAddressId __sourceProperty = address.getAddressId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAddressId(__commonMapper.toShippingAddressIdLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddress.class);
                __event.setSourcePropertyType(ShippingAddressId.class);
                __event.setSourcePropertyName("addressId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddressEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("addressId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAddressId(__commonMapper.toShippingAddressIdLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UserId __sourceProperty = address.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__commonMapper.toUserIdLong(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddress.class);
                __event.setSourcePropertyType(UserId.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddressEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__commonMapper.toUserIdLong(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = address.getStreet();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStreet(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddress.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("street");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("street");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStreet(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = address.getStreet1();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStreet1(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddress.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("street1");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("street1");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStreet1(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = address.getStreet2();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStreet2(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddress.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("street2");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("street2");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStreet2(__builtinMapper.fromStringToString(__sourceProperty));
        
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
        
                __event.setSourceType(ShippingAddress.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("city");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddressEntity.class);
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
        
            String __sourceProperty = address.getState();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setState(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddress.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("state");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddressEntity.class);
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
        
            String __sourceProperty = address.getPostalCode();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPostalCode(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddress.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("postalCode");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddressEntity.class);
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
            {
        
            String __sourceProperty = address.getCountry();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCountry(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddress.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("country");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddressEntity.class);
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
        
            String __sourceProperty = address.getCompanyName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCompanyName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddress.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("companyName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("companyName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCompanyName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = address.getFirstName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setFirstName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddress.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("firstName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("firstName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setFirstName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = address.getMiddleName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setMiddleName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddress.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("middleName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("middleName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setMiddleName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = address.getLastName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setLastName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddress.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("lastName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("lastName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setLastName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = address.getPhoneNumber();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPhoneNumber(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddress.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("phoneNumber");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("phoneNumber");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPhoneNumber(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = address.getDescription();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDescription(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddress.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("description");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddressEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("description");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDescription(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public ShippingAddress toShippingAddress(ShippingAddressEntity entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        ShippingAddress __result = new ShippingAddress();
    
        {
        
            Long __sourceProperty = entity.getAddressId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAddressId(__commonMapper.toShippingAddressId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddressEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("addressId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddress.class);
                __event.setTargetPropertyType(ShippingAddressId.class);
                __event.setTargetPropertyName("addressId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAddressId(__commonMapper.toShippingAddressId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__commonMapper.toUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddressEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddress.class);
                __event.setTargetPropertyType(UserId.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__commonMapper.toUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getStreet();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStreet(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("street");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddress.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("street");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStreet(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getStreet1();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStreet1(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("street1");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddress.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("street1");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStreet1(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getStreet2();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStreet2(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("street2");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddress.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("street2");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStreet2(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getCity();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCity(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("city");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddress.class);
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
        
            String __sourceProperty = entity.getState();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setState(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("state");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddress.class);
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
        
            String __sourceProperty = entity.getPostalCode();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPostalCode(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("postalCode");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddress.class);
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
            {
        
            String __sourceProperty = entity.getCountry();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCountry(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("country");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddress.class);
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
        
            String __sourceProperty = entity.getCompanyName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCompanyName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("companyName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddress.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("companyName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCompanyName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getFirstName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setFirstName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("firstName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddress.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("firstName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setFirstName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getMiddleName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setMiddleName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("middleName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddress.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("middleName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setMiddleName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getLastName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setLastName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("lastName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddress.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("lastName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setLastName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getPhoneNumber();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPhoneNumber(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("phoneNumber");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddress.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("phoneNumber");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPhoneNumber(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getDescription();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDescription(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(ShippingAddressEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("description");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(ShippingAddress.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("description");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDescription(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public Currency toCurrency(CurrencyEntity entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        Currency __result = new Currency();
    
        {
        
            String __sourceProperty = entity.getName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CurrencyEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("name");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Currency.class);
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
        
            String __sourceProperty = entity.getNumericCode();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setNumericCode(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CurrencyEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("numericCode");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Currency.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("numericCode");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setNumericCode(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = entity.getBaseUnits();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setBaseUnits(__builtinMapper.fromIntegerToInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CurrencyEntity.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("baseUnits");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Currency.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("baseUnits");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setBaseUnits(__builtinMapper.fromIntegerToInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getDecimalPattern();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDecimalPattern(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CurrencyEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("decimalPattern");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Currency.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("decimalPattern");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDecimalPattern(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Double __sourceProperty = entity.getMaxBalance();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setMaxBalance(__builtinMapper.fromDoubleToDouble(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CurrencyEntity.class);
                __event.setSourcePropertyType(Double.class);
                __event.setSourcePropertyName("maxBalance");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Currency.class);
                __event.setTargetPropertyType(Double.class);
                __event.setTargetPropertyName("maxBalance");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setMaxBalance(__builtinMapper.fromDoubleToDouble(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Double __sourceProperty = entity.getSpendingLimit();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setSpendingLimit(__builtinMapper.fromDoubleToDouble(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CurrencyEntity.class);
                __event.setSourcePropertyType(Double.class);
                __event.setSourcePropertyName("spendingLimit");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Currency.class);
                __event.setTargetPropertyType(Double.class);
                __event.setTargetPropertyName("spendingLimit");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setSpendingLimit(__builtinMapper.fromDoubleToDouble(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Double __sourceProperty = entity.getPreauthAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPreauthAmount(__builtinMapper.fromDoubleToDouble(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CurrencyEntity.class);
                __event.setSourcePropertyType(Double.class);
                __event.setSourcePropertyName("preauthAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Currency.class);
                __event.setTargetPropertyType(Double.class);
                __event.setTargetPropertyName("preauthAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPreauthAmount(__builtinMapper.fromDoubleToDouble(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Double __sourceProperty = entity.getPaypalPreauthAmount();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPaypalPreauthAmount(__builtinMapper.fromDoubleToDouble(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CurrencyEntity.class);
                __event.setSourcePropertyType(Double.class);
                __event.setSourcePropertyName("paypalPreauthAmount");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Currency.class);
                __event.setTargetPropertyType(Double.class);
                __event.setTargetPropertyName("paypalPreauthAmount");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPaypalPreauthAmount(__builtinMapper.fromDoubleToDouble(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getSymbol();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setSymbol(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CurrencyEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("symbol");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Currency.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("symbol");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setSymbol(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getOrientation();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOrientation(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CurrencyEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("orientation");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Currency.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("orientation");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOrientation(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getDescription();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDescription(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(CurrencyEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("description");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(Currency.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("description");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDescription(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }
}
