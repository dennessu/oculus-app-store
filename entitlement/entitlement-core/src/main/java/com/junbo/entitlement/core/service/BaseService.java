/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core.service;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.entitlement.clientproxy.catalog.EntitlementDefinitionFacade;
import com.junbo.entitlement.common.cache.PermanentCache;
import com.junbo.entitlement.common.def.EntitlementConsts;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.spec.error.AppErrors;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Base service.
 */
public class BaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);
    @Autowired
    private EntitlementDefinitionFacade definitionFacade;

    protected void fillCreate(Entitlement entitlement) {
        if (entitlement.getIsBanned() == null) {
            entitlement.setIsBanned(false);
        }
        if (entitlement.getGrantTime() == null) {
            entitlement.setGrantTime(EntitlementContext.current().getNow());
        }
    }

    protected void fillUpdate(Entitlement entitlement, Entitlement existingEntitlement) {
        existingEntitlement.setUseCount(entitlement.getUseCount());
        existingEntitlement.setExpirationTime(entitlement.getExpirationTime());
        existingEntitlement.setRev(entitlement.getRev());
        existingEntitlement.setIsBanned(entitlement.getIsBanned());
    }

    protected void validateCreate(Entitlement entitlement) {
        checkOauth(entitlement);
        if (Boolean.TRUE.equals(entitlement.getIsBanned())) {
            throw AppErrors.INSTANCE.fieldNotCorrect("isSuspended",
                    "isSuspended can not be true when created").exception();
        }
        validateNotNull(entitlement.getGrantTime(), "grantTime");
        if (entitlement.getUseCount() != null && entitlement.getUseCount() < 1) {
            throw AppErrors.INSTANCE.fieldNotCorrect("useCount", "useCount should not be negative").exception();
        }
        validateGrantTimeBeforeExpirationTime(entitlement);
    }

    protected void validateUpdateId(Long entitlementId, Entitlement entitlement) {
        if (entitlement.getEntitlementId() == null) {
            throw AppErrors.INSTANCE.missingField("id").exception();
        }
        if (!entitlementId.equals(entitlement.getEntitlementId())) {
            throw AppErrors.INSTANCE.fieldNotMatch("id", entitlement.getEntitlementId(),
                    entitlementId).exception();
        }
    }

    protected void validateUpdate(Entitlement entitlement, Entitlement existingEntitlement) {
        checkOauth(existingEntitlement);
        validateEquals(existingEntitlement.getUserId(), entitlement.getUserId(), "user");
        validateEquals(existingEntitlement.getEntitlementDefinitionId(),
                entitlement.getEntitlementDefinitionId(), "definition");
        validateEquals(existingEntitlement.getGrantTime(), entitlement.getGrantTime(), "grantTime");
        if (entitlement.getUseCount() != null && entitlement.getUseCount() < 1) {
            throw AppErrors.INSTANCE.fieldNotCorrect("useCount", "useCount should not be negative").exception();
        }
        validateGrantTimeBeforeExpirationTime(existingEntitlement);
    }

    protected void validateTransfer(EntitlementTransfer transfer, Entitlement existingEntitlement) {
        validateNotNull(transfer.getEntitlementId(), "entitlement");
        if (existingEntitlement == null) {
            throw AppErrors.INSTANCE.notFound("entitlement", transfer.getEntitlementId()).exception();
        }
        checkUser(existingEntitlement.getUserId());
        checkTargetUser(transfer.getTargetUserId());
        if (existingEntitlement.getIsBanned()) {
            LOGGER.error("Entitlement [{}] can not be transferred.", existingEntitlement.getEntitlementId());
            throw AppErrors.INSTANCE.notTransferable(existingEntitlement.getEntitlementId(),
                    "Banned entitlement can not be transferred.")
                    .exception();
        }
    }

    private void validateGrantTimeBeforeExpirationTime(Entitlement entitlement) {
        if (entitlement.getExpirationTime() != null) {
            if (entitlement.getGrantTime().after(entitlement.getExpirationTime())) {
                throw AppErrors.INSTANCE.expirationTimeBeforeGrantTime().exception();
            }
        }
    }

    private void validateEquals(Object actual, Object expected, String fieldName) {
        if (expected == actual) {
            return;
        } else if (expected == null || actual == null) {
            throw AppErrors.INSTANCE.fieldNotMatch(fieldName, actual, expected).exception();
        }
        Boolean equals = true;
        if (actual instanceof String) {
            if (!((String) expected).equalsIgnoreCase((String) actual)) {
                equals = false;
            }
        } else if (actual instanceof Date) {
            if (Math.abs(((Date) actual).getTime() - ((Date) expected).getTime()) > 1000) {
                equals = false;
            }
        } else if (!expected.equals(actual)) {
            equals = false;
        }

        if (!equals) {
            throw AppErrors.INSTANCE.fieldNotMatch(fieldName, actual, expected).exception();
        }
    }

    protected void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw AppErrors.INSTANCE.missingField(fieldName).exception();
        }
    }

    protected void checkUser(Long userId) {
        validateNotNull(userId, "user");
        //TODO: check userId
    }

    protected void checkTargetUser(Long userId) {
        validateNotNull(userId, "targetUser");
    }

    protected void checkOauth(final Entitlement entitlement) {
        EntitlementDefinition definition = (EntitlementDefinition) PermanentCache.ENTITLEMENT_DEFINITION.get(
                entitlement.getEntitlementDefinitionId(), new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return definitionFacade.getDefinition(entitlement.getEntitlementDefinitionId());
            }
        });
        //TODO: check clientId
    }

    protected void checkDateFormat(String date) {
        if (!StringUtils.isEmpty(date)) {
            try {
                EntitlementConsts.DATE_FORMAT.parse(date);
            } catch (ParseException e) {
                throw AppErrors.INSTANCE.
                        common("date should be in yyyy-MM-ddTHH:mm:ssZ format").exception();
            }
        }
    }
}
