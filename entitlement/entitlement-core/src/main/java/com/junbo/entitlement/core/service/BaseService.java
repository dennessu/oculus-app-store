/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core.service;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import com.junbo.common.id.UserId;
import com.junbo.entitlement.clientproxy.catalog.CatalogFacade;
import com.junbo.entitlement.clientproxy.identity.UserFacade;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.db.entity.def.EntitlementStatus;
import com.junbo.entitlement.spec.error.AppErrors;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Base service.
 */
public class BaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);
    @Autowired
    private UserFacade userFacade;
    @Autowired
    private CatalogFacade catalogFacade;

    protected void fillCreate(Entitlement entitlement) {
        if (entitlement.getType() == null) {
            entitlement.setType(EntitlementType.DEFAULT.toString());
        }

        if (entitlement.getManagedLifecycle() == null) {
            LOGGER.warn("managedLifecycle not found, set true as default.");
            entitlement.setManagedLifecycle(true);
        }

        if (entitlement.getManagedLifecycle()) {
            entitlement.setStatus(EntitlementStatus.MANAGED.toString());
        }

        if (entitlement.getConsumable() == null || !entitlement.getConsumable()) {
            LOGGER.warn("consumable not found, set false and set useCount to 0 as default.");
            entitlement.setConsumable(false);
            entitlement.setUseCount(0);
        }

        if (entitlement.getGrantTime() == null) {
            entitlement.setGrantTime(EntitlementContext.current().getNow());
        }
        if (entitlement.getPeriod() != null) {
            entitlement.setExpirationTime(new Date(entitlement.getGrantTime().getTime()
                    + TimeUnit.SECONDS.toMillis(entitlement.getPeriod())));
        }

        if (entitlement.getGroup() == null) {
            entitlement.setGroup("");
        }
        if (entitlement.getTag() == null) {
            entitlement.setTag("");
        }
    }

    protected void fillUpdate(Entitlement entitlement, Entitlement existingEntitlement) {
        if (entitlement.getPeriod() != null) {
            existingEntitlement.setExpirationTime(new Date(existingEntitlement.getGrantTime().getTime()
                    + TimeUnit.SECONDS.toMillis(entitlement.getPeriod())));
        } else {
            existingEntitlement.setExpirationTime(entitlement.getExpirationTime());
        }

        if (entitlement.getConsumable() == null || !entitlement.getConsumable()) {
            existingEntitlement.setConsumable(false);
            existingEntitlement.setUseCount(0);
        } else {
            existingEntitlement.setConsumable(entitlement.getConsumable());
            existingEntitlement.setUseCount(entitlement.getUseCount());
        }

        existingEntitlement.setManagedLifecycle(entitlement.getManagedLifecycle());
        existingEntitlement.setStatus(entitlement.getStatus());
        existingEntitlement.setStatusReason(entitlement.getStatusReason());

        if (existingEntitlement.getStatus() != null) {
            if (EntitlementStatus.LIFECYCLE_NOT_MANAGED_STATUS
                    .contains(EntitlementStatus.valueOf(existingEntitlement.getStatus().toUpperCase()))) {
                LOGGER.info("Delete or ban entitlement [{}], set managedLifecycle" +
                        " and consumable to false and set useCount to 0.", existingEntitlement.getEntitlementId());
                existingEntitlement.setManagedLifecycle(false);
                existingEntitlement.setConsumable(false);
                existingEntitlement.setUseCount(0);
            }
        }
    }

    protected void validateCreate(Entitlement entitlement) {
        checkUser(entitlement.getUserId());
        checkOffer(entitlement.getOfferId());
        checkDeveloper(entitlement.getDeveloperId());
        if (EntitlementStatus.LIFECYCLE_NOT_MANAGED_STATUS.contains(
                EntitlementStatus.valueOf(entitlement.getStatus().toUpperCase()))) {
            LOGGER.error("Can not created {} entitlement.", entitlement.getStatus());
            throw AppErrors.INSTANCE.fieldNotCorrect("status",
                    "status can not be DELETED or BANNED when created").exception();
        }
        if (!entitlement.getManagedLifecycle()) {
            validateNotNull(entitlement.getStatus(), "status");
        }
        checkEntitlementDefinition(entitlement.getEntitlementDefinitionId());
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
        checkDeveloper(existingEntitlement.getDeveloperId());
        if (!entitlement.getManagedLifecycle()) {
            validateNotNull(existingEntitlement.getStatus(), "status");
        }
        validateEquals(existingEntitlement.getUserId(), entitlement.getUserId(), "user");
        validateEquals(existingEntitlement.getDeveloperId(), entitlement.getDeveloperId(), "developer");
        validateEquals(existingEntitlement.getOfferId(), entitlement.getOfferId(), "offer");
        validateEquals(existingEntitlement.getEntitlementDefinitionId(),
                entitlement.getEntitlementDefinitionId(), "definition");
        validateEquals(existingEntitlement.getType(), entitlement.getType(), "type");
        validateEquals(existingEntitlement.getGroup(), entitlement.getGroup(), "group");
        validateEquals(existingEntitlement.getTag(), entitlement.getTag(), "tag");
        validateEquals(existingEntitlement.getGrantTime(), entitlement.getGrantTime(), "grantTime");
        validateGrantTimeBeforeExpirationTime(existingEntitlement);
    }

    protected void validateTransfer(EntitlementTransfer transfer, Entitlement existingEntitlement) {
        validateNotNull(transfer.getEntitlementId(), "entitlement");
        if (existingEntitlement == null) {
            throw AppErrors.INSTANCE.notFound("entitlement", transfer.getEntitlementId()).exception();
        }
        checkUser(existingEntitlement.getUserId());
        checkTargetUser(transfer.getTargetUserId());
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

    private void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw AppErrors.INSTANCE.missingField(fieldName).exception();
        }
    }

    protected void checkUser(Long userId) {
        validateNotNull(userId, "user");
//        if (!userFacade.exists(userId)) {
//            throw new NotFoundException("user");
//        }
    }

    protected void checkUser(UserId userId) {
        validateNotNull(userId, "user");
//        if (!userFacade.exists(userId)) {
//            throw new NotFoundException("user");
//        }
    }

    private void checkTargetUser(Long userId) {
        validateNotNull(userId, "targetUser");
//        if (!userFacade.exists(userId)) {
//            throw new NotFoundException("targetUser");
//        }
    }

    private void checkOffer(Long itemId) {
//        if (!catalogFacade.exists(itemId)) {
//            throw new NotFoundException("offer");
//        }
    }

    protected void checkDeveloper(Long developerId) {
        validateNotNull(developerId, "developer");
//        if (!userFacade.exists(developerId)) {
//            throw new NotFoundException("developer");
//        }
    }

    protected void checkDeveloper(UserId developerId) {
        validateNotNull(developerId, "developer");
//        if (!userFacade.exists(developerId)) {
//            throw new NotFoundException("developer");
//        }
    }

    private void checkEntitlementDefinition(Long entitlementDefinitionId) {
    }
}
