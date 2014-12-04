/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.clientproxy.entitlement;

import com.junbo.catalog.spec.enums.EntitlementType;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.UserId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.ApiContext;
import com.junbo.store.spec.model.Entitlement;

import java.util.List;
import java.util.Set;

/**
 * The EntitlementFacade class.
 */
public interface EntitlementFacade {

    Promise<List<Entitlement>> getEntitlements(EntitlementType entitlementType, Set<String> itemTypes, ItemId hostItemId, boolean includeItemDetails, ApiContext apiContext);

    Promise<List<Entitlement>> getEntitlementsByIds(Set<EntitlementId> entitlementIdList, boolean includeItemDetails, ApiContext apiContext);

    Promise<Boolean> checkEntitlements(UserId userId, ItemId itemId, EntitlementType entitlementType);

    Promise<Set<ItemId>> checkEntitlements(UserId userId, Set<ItemId> itemIds, EntitlementType entitlementType);

    Promise updateEntitlementDeveloperPayload(EntitlementId entitlementId, String developerPayload);

    Promise consumeEntitlement(EntitlementId entitlementId);

}
