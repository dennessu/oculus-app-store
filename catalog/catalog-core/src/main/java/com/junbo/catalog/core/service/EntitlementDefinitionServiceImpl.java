/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.core.EntitlementDefinitionService;
import com.junbo.catalog.db.repo.EntitlementDefinitionRepository;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Service for EntitlementDefinition.
 */
public class EntitlementDefinitionServiceImpl implements EntitlementDefinitionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntitlementDefinitionService.class);
    @Autowired
    private EntitlementDefinitionRepository entitlementDefinitionRepository;

    @Override
    public EntitlementDefinition getEntitlementDefinition(Long entitlementDefinitionId) {
        EntitlementDefinition entitlementDefinition = entitlementDefinitionRepository.get(entitlementDefinitionId);
        if (entitlementDefinition == null) {
            throw AppErrors.INSTANCE
                    .notFound("entitlementDefinition", Utils.encodeId(entitlementDefinitionId)).exception();
        }
        checkDeveloper(entitlementDefinition.getDeveloperId());
        return entitlementDefinition;
    }

    @Override
    public List<EntitlementDefinition> getEntitlementDefinitions(Long developerId, String clientId,
                                                                 Long itemId, Set<String> tags, Set<String> types,
                                                                 Boolean isConsumable, PageableGetOptions pageMetadata) {
        checkDeveloper(developerId);
        Set<EntitlementType> typeSet = new HashSet<>();
        if (!CollectionUtils.isEmpty(types)) {
            for (String type : types) {
                try {
                    typeSet.add(EntitlementType.valueOf(type));
                } catch (Exception e) {
                    throw AppErrors.INSTANCE.fieldNotCorrect("types", "type " + type + " not supported").exception();
                }
            }
        }
        return entitlementDefinitionRepository.getByParams(developerId, clientId, itemId, tags, typeSet, isConsumable, pageMetadata);
    }

    @Override
    public Long createEntitlementDefinition(EntitlementDefinition entitlementDefinition) {
        if (entitlementDefinition.getTag() == null) {
            entitlementDefinition.setTag("");
        }
        if (entitlementDefinition.getConsumable() == null) {
            entitlementDefinition.setConsumable(false);
        }
        if (!StringUtils.isEmpty(entitlementDefinition.getRev())) {
            throw AppErrors.INSTANCE.unnecessaryField("rev").exception();
        }
        checkDeveloper(entitlementDefinition.getDeveloperId());
        checkInAppContext(entitlementDefinition.getInAppContext());
        return entitlementDefinitionRepository.create(entitlementDefinition);
    }

    @Override
    @Transactional
    public Long updateEntitlementDefinition(Long entitlementDefinitionId,
                                            EntitlementDefinition entitlementDefinition) {
        if (entitlementDefinition.getEntitlementDefId() == null) {
            throw AppErrors.INSTANCE.missingField("id").exception();
        }
        if (!entitlementDefinitionId.equals(entitlementDefinition.getEntitlementDefId())) {
            throw AppErrors.INSTANCE.fieldNotMatch("id", entitlementDefinition.getEntitlementDefId(),
                    entitlementDefinitionId).exception();
        }

        EntitlementDefinition existingEntitlementDefinition =
                entitlementDefinitionRepository.get(entitlementDefinitionId);
        if (existingEntitlementDefinition == null) {
            throw AppErrors.INSTANCE
                    .notFound("entitlementDefinition", Utils.encodeId(entitlementDefinitionId)).exception();
        }

        checkDeveloper(existingEntitlementDefinition.getDeveloperId());
        checkInAppContext(entitlementDefinition.getInAppContext());

        validateEquals(entitlementDefinition.getRev(),
                existingEntitlementDefinition.getRev(), "rev");
        validateEquals(Utils.encodeId(entitlementDefinition.getDeveloperId()),
                Utils.encodeId(existingEntitlementDefinition.getDeveloperId()), "developer");
        validateEquals(entitlementDefinition.getType(),
                existingEntitlementDefinition.getType(), "type");
        validateEquals(entitlementDefinition.getConsumable(),
                existingEntitlementDefinition.getConsumable(), "isConsumable");

        existingEntitlementDefinition.setTag(entitlementDefinition.getTag());
        existingEntitlementDefinition.setItemId(entitlementDefinition.getItemId());
        existingEntitlementDefinition.setInAppContext(entitlementDefinition.getInAppContext());

        return entitlementDefinitionRepository.update(existingEntitlementDefinition);
    }

    @Override
    @Transactional
    public void deleteEntitlement(Long entitlementDefinitionId) {
        EntitlementDefinition existingEntitlementDefinition =
                entitlementDefinitionRepository.get(entitlementDefinitionId);
        if (existingEntitlementDefinition == null) {
            throw AppErrors.INSTANCE
                    .notFound("entitlementDefinition", Utils.encodeId(entitlementDefinitionId)).exception();
        }
        checkDeveloper(existingEntitlementDefinition.getDeveloperId());
        entitlementDefinitionRepository.delete(existingEntitlementDefinition);
    }

    @Override
    public EntitlementDefinition getByTrackingUuid(UUID trackingUuid) {
        return entitlementDefinitionRepository.getByTrackingUuid(trackingUuid);
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

    private void checkInAppContext(List<String> inAppContext) {
    }

    private void checkDeveloper(Long developerId) {
    }
}
