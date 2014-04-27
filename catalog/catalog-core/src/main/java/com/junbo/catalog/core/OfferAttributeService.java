/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.attribute.OfferAttributesGetOptions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Offer Attribute service definition.
 */
@Transactional
public interface OfferAttributeService {
    OfferAttribute getAttribute(Long attributeId);
    List<OfferAttribute> getAttributes(OfferAttributesGetOptions options);
    OfferAttribute create(OfferAttribute attribute);
    OfferAttribute update(Long attributeId, OfferAttribute attribute);
    void deleteAttribute(Long attributeId);
}
