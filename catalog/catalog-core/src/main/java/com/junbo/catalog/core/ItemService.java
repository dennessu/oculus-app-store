/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import org.springframework.transaction.annotation.Transactional;

/**
 * Item service definition.
 */
@Transactional
public interface ItemService extends BaseRevisionedService<Item, ItemRevision> {
}
