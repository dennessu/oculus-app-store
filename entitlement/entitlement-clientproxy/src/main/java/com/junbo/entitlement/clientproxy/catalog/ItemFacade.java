/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.clientproxy.catalog;

import com.junbo.catalog.spec.model.item.ItemRevision;

/**
 * Interface wrapper to call from catalog.
 */
public interface ItemFacade {
    ItemRevision getItem(Long itemId);
}
