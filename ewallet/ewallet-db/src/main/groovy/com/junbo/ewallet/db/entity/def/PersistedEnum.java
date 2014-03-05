/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.entity.def;

/**
 * PersistedEnum. Used to map enum to a Integer when saving in hibernate.
 * @param <E> enum type.
 */
public interface PersistedEnum<E extends Enum<?>> {
    Integer getPersistedValue();
    E returnEnum(Integer persistedValue);
}
