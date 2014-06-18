/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.db.repo.ItemAttributeRepository;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.ItemAttributesGetOptions;
import com.junbo.common.cloudant.CloudantClient;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Item repository.
 */
public class ItemAttributeRepositoryImpl extends CloudantClient<ItemAttribute> implements ItemAttributeRepository {

    public ItemAttribute create(ItemAttribute attribute) {
        return cloudantPost(attribute).get();
    }

    public ItemAttribute get(String attributeId) {
        if (attributeId == null) {
            return null;
        }
        return cloudantGet(attributeId).get();
    }

    public List<ItemAttribute> getAttributes(ItemAttributesGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getAttributeIds())) {
            List<ItemAttribute> attributes = new ArrayList<>();
            for (String attributeId : options.getAttributeIds()) {
                ItemAttribute attribute = cloudantGet(attributeId).get();
                if (attribute != null) {
                    attributes.add(attribute);
                }
            }
            return attributes;
        } else if (!StringUtils.isEmpty(options.getAttributeType())){
            return queryView("by_type", options.getAttributeType(),
                    options.getValidSize(), options.getValidStart(), false).get();
        } else {
            return queryView("by_attributeId", null, options.getValidSize(), options.getValidStart(), false).get();
        }
    }

    public ItemAttribute update(ItemAttribute attribute) {
        return cloudantPut(attribute).get();
    }

    public void delete(String attributeId) {
        cloudantDelete(attributeId).get();
    }

}
