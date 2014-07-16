/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.db.repo.OfferAttributeRepository;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.attribute.OfferAttributesGetOptions;
import com.junbo.common.cloudant.CloudantClient;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Offer repository.
 */
public class OfferAttributeRepositoryImpl extends CloudantClient<OfferAttribute> implements OfferAttributeRepository {

    public OfferAttribute create(OfferAttribute attribute) {
        return cloudantPostSync(attribute);
    }

    public OfferAttribute get(String attributeId) {
        if (attributeId == null) {
            return null;
        }
        return cloudantGetSync(attributeId);
    }

    public List<OfferAttribute> getAttributes(OfferAttributesGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getAttributeIds())) {
            List<OfferAttribute> attributes = new ArrayList<>();
            for (String attributeId : options.getAttributeIds()) {
                OfferAttribute attribute = cloudantGetSync(attributeId.toString());
                if (attribute != null) {
                    attributes.add(attribute);
                }
            }
            return attributes;
        } else if (!StringUtils.isEmpty(options.getAttributeType())){
            return queryView("by_type", options.getAttributeType(),
                    options.getValidSize(), options.getValidStart(), false).syncGet();
        } else {
            return queryView("by_attributeId", null, options.getValidSize(), options.getValidStart(), false).syncGet();
        }
    }

    public OfferAttribute update(OfferAttribute attribute, OfferAttribute oldAttribute) {
        return cloudantPutSync(attribute, oldAttribute);
    }


    public void delete(String attributeId) {
        cloudantDeleteSync(attributeId);
    }

}
