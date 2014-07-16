/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.attribute.OfferAttributesGetOptions;

import java.util.List;

/**
 * Offer repository.
 */
public interface OfferAttributeRepository extends AttributeRepository<OfferAttribute> {
    OfferAttribute create(OfferAttribute attribute);
    OfferAttribute get(String attributeId);
    List<OfferAttribute> getAttributes(OfferAttributesGetOptions options);
    OfferAttribute update(OfferAttribute attribute, OfferAttribute oldAttribute);
    void delete(String attributeId);
}
