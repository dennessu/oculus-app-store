/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.catalog.spec.model.attribute.AttributesGetOptions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Attribute service definition.
 */
@Transactional
public interface AttributeService {
    Attribute getAttribute(Long attributeId);
    List<Attribute> getAttributes(AttributesGetOptions options);
    Attribute create(Attribute attribute);
}
