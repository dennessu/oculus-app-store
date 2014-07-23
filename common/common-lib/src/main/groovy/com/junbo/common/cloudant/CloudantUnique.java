/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.cloudant;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The interface used together with CloudantEntity.
 * Any CloudantEntity which also implement CloudantUnique will use table unique to ensure the key uniqueness.
 */
public interface CloudantUnique {

    /**
     * Get the unique keys of the CloudantEntity.
     * Each of the keys are globally unique.
     * The unique keys for the same object must always be the same length. If some key is not required, place a null.
     * @return The unique keys.
     */
    @JsonIgnore
    String[] getUniqueKeys();
}
