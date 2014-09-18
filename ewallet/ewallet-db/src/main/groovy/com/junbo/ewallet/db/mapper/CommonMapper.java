/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.util.EnumRegistry;
import com.junbo.ewallet.spec.def.Status;
import com.junbo.ewallet.spec.def.TransactionType;
import com.junbo.ewallet.spec.def.WalletLotType;
import com.junbo.ewallet.spec.def.WalletType;
import org.springframework.util.StringUtils;

/**
 * Created by haomin on 14-6-20.
 */
public class CommonMapper {

    private ObjectMapper mapper = new ObjectMapper();

    public Integer explicitMethod_convertStatus(String str) {
        if (!StringUtils.isEmpty(str)) {
            Status status = Status.valueOf(str);
            return status.getId();
        }
        return null;
    }

    public String explicitMethod_convertStatus(Integer statusId) {
        Status status = EnumRegistry.resolve(statusId, Status.class);
        return status.toString();
    }

    public Integer explicitMethod_convertTransactionType(String str) {
        if (!StringUtils.isEmpty(str)) {
            TransactionType type = TransactionType.valueOf(str);
            return type.getId();
        }
        return null;
    }

    public String explicitMethod_convertTransactionType(Integer typeId) {
        TransactionType type = EnumRegistry.resolve(typeId, TransactionType.class);
        return type.toString();
    }

    public Integer explicitMethod_convertLotType(String lotType) {
        if (!StringUtils.isEmpty(lotType)) {
            WalletLotType t = WalletLotType.valueOf(lotType);
            return t.getId();
        }
        return null;
    }

    public String explicitMethod_convertLotType(Integer lotTypeId) {
        WalletLotType type = EnumRegistry.resolve(lotTypeId, WalletLotType.class);
        return type.toString();
    }


    public Integer explicitMethod_convertWalletType(String type) {
        if (!StringUtils.isEmpty(type)) {
            WalletType wallet = WalletType.valueOf(type);
            return wallet.getId();
        }
        return null;
    }

    public String explicitMethod_convertWalletType(Integer typeId) {
        WalletType balanceStatus = EnumRegistry.resolve(typeId, WalletType.class);
        return balanceStatus.toString();
    }

    public Integer toInteger(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return Integer.parseInt(value);
    }

    public String toString(Integer value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public Long fromStringToLong(String source) {
        return StringUtils.isEmpty(source) ? null : Long.parseLong(source);
    }

    public String fromLongToString(Long source) {
        return source == null ? null : source.toString();
    }

    public String fromShortToString(Short id) {
        if (id == null) {
            return null;
        }

        return id.toString();
    }

    public Short fromStringToShort(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }

        return Short.valueOf(id);
    }

    public Short fromShortToShort(Short source) {
        return source == null ? null : new Short(source);
    }
}
