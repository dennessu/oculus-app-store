/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.spec.model.attribute.Attribute;

/**
 * Attribute repository.
 * @param <T> Attribute
 */
public interface AttributeRepository<T extends Attribute> {
    T create(T attribute);
    T get(String attributeId);
    T update(T attribute, T oldAttribute);
    void delete(String attributeId);
}
