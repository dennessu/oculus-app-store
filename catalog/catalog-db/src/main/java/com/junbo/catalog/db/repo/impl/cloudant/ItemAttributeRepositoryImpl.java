/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.repo.ItemAttributeRepository;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.ItemAttributesGetOptions;
import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantQueryResult;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Item repository.
 */
public class ItemAttributeRepositoryImpl extends CloudantClient<ItemAttribute> implements ItemAttributeRepository {

    public ItemAttribute create(ItemAttribute attribute) {
        return cloudantPostSync(attribute);
    }

    public ItemAttribute get(String attributeId) {
        if (attributeId == null) {
            return null;
        }
        return cloudantGetSync(attributeId);
    }

    public List<ItemAttribute> getAttributes(ItemAttributesGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getAttributeIds())) {
            List<ItemAttribute> attributes = new ArrayList<>();
            for (String attributeId : options.getAttributeIds()) {
                ItemAttribute attribute = cloudantGetSync(attributeId);
                if (attribute != null && (StringUtils.isEmpty(options.getAttributeType()) || options.getAttributeType().equals(attribute.getType()))) {
                    attributes.add(attribute);
                }
            }
            options.setTotal(Long.valueOf(attributes.size()));
            return attributes;
        } else if (!StringUtils.isEmpty(options.getAttributeType())){
            CloudantQueryResult queryResult = queryViewSync("by_type", options.getAttributeType(), options.getValidSize(), options.getValidStart(), false, true);
            options.setTotal(queryResult.getTotalRows());
            return Utils.getDocs(queryResult.getRows());
        } else {
            CloudantQueryResult queryResult = queryViewSync("by_attributeId", null, options.getValidSize(), options.getValidStart(), false, true);
            options.setTotal(queryResult.getTotalRows());
            return Utils.getDocs(queryResult.getRows());
        }
    }

    public ItemAttribute update(ItemAttribute attribute, ItemAttribute oldAttribute) {
        return cloudantPutSync(attribute, oldAttribute);
    }

    public void delete(String attributeId) {
        cloudantDeleteSync(attributeId);
    }

}
