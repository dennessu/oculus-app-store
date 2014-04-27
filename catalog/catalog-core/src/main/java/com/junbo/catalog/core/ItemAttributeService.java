/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.ItemAttributesGetOptions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Item Attribute service definition.
 */
@Transactional
public interface ItemAttributeService {
    ItemAttribute getAttribute(Long attributeId);
    List<ItemAttribute> getAttributes(ItemAttributesGetOptions options);
    ItemAttribute create(ItemAttribute attribute);
    ItemAttribute update(Long attributeId, ItemAttribute attribute);
    void deleteAttribute(Long attributeId);
}
