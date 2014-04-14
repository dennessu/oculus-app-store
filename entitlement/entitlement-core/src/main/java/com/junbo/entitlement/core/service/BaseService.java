/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core.service;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.entitlement.clientproxy.catalog.EntitlementDefinitionFacade;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.spec.def.EntitlementStatus;
import com.junbo.entitlement.spec.error.AppErrors;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Base service.
 */
public class BaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);
    @Autowired
    private EntitlementDefinitionFacade definitionFacade;

    protected void fillCreate(Entitlement entitlement) {
        if (CollectionUtils.isEmpty(entitlement.getInAppContext())
                && entitlement.getEntitlementDefinitionId() != null) {
            fillDefinition(entitlement);
        }
        if (entitlement.getGroup() == null) {
            entitlement.setGroup("");
        }
        if (entitlement.getTag() == null) {
            entitlement.setTag("");
        }
        if (entitlement.getGrantTime() == null) {
            entitlement.setGrantTime(EntitlementContext.current().getNow());
        }
    }

    private void fillDefinition(Entitlement entitlement) {
        EntitlementDefinition definition = definitionFacade.getDefinition(entitlement.getEntitlementDefinitionId());
        if (definition == null) {
            throw AppErrors.INSTANCE.fieldNotCorrect(
                    "entitlementDefinition", "EntitlementDefinition [" +
                    entitlement.getEntitlementDefinitionId() +
                    "] not found.").exception();
        }
        entitlement.setType(definition.getType());
        entitlement.setGroup(definition.getGroup());
        entitlement.setTag(definition.getTag());
        entitlement.setInAppContext(Collections.singletonList(definition.getDeveloperId().toString()));
    }

    protected void fillUpdate(Entitlement entitlement, Entitlement existingEntitlement) {
        existingEntitlement.setUseCount(entitlement.getUseCount());
        existingEntitlement.setExpirationTime(entitlement.getExpirationTime());
        existingEntitlement.setRev(entitlement.getRev());
    }

    protected void validateCreate(Entitlement entitlement) {
        checkUser(entitlement.getUserId());
        if (CollectionUtils.isEmpty(entitlement.getInAppContext())
                && entitlement.getEntitlementDefinitionId() == null) {
            throw AppErrors.INSTANCE.common(
                    "One of developer and entitlementDefinition should not be null.")
                    .exception();
        }
        checkInAppContext(entitlement.getInAppContext());
        if (entitlement.getStatus() != null) {
            throw AppErrors.INSTANCE.fieldNotCorrect("status",
                    "status can not be set").exception();
        }
        validateNotNull(entitlement.getType(), "type");
        validateNotNull(entitlement.getGrantTime(), "grantTime");
        validateNotNull(entitlement.getGroup(), "group");
        validateNotNull(entitlement.getTag(), "tag");
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
        checkUser(existingEntitlement.getUserId());
        checkInAppContext(existingEntitlement.getInAppContext());
        validateEquals(existingEntitlement.getUserId(), entitlement.getUserId(), "user");
        validateEquals(existingEntitlement.getInAppContext(), entitlement.getInAppContext(), "inAppContext");
        validateEquals(existingEntitlement.getEntitlementDefinitionId(),
                entitlement.getEntitlementDefinitionId(), "definition");
        validateEquals(existingEntitlement.getType(), entitlement.getType(), "type");
        validateEquals(existingEntitlement.getGroup(), entitlement.getGroup(), "group");
        validateEquals(existingEntitlement.getTag(), entitlement.getTag(), "tag");
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
        checktargetUser(transfer.getTargetUserId());
        if (EntitlementStatus.NOT_TRANSFERABLE.contains(existingEntitlement.getStatus())) {
            LOGGER.error("Entitlement [{}] can not be transferred.", existingEntitlement.getEntitlementId());
            throw AppErrors.INSTANCE.notTransferable(existingEntitlement.getEntitlementId(),
                    "Entitlement with status " +
                            existingEntitlement.getStatus() +
                            " can not be transferred")
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

    protected void checktargetUser(Long userId) {
        validateNotNull(userId, "targetUser");
    }

    protected void checkInAppContext(List<String> clientIds) {
        //TODO: check clientId
    }
}
