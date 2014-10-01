/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson.aware;

import java.util.Collection;

/**
 * ResourceCollectionAware.
 */
public interface ResourceCollectionAware {
    void injectCollectionType(Class<? extends Collection> collectionType);

    void injectIdClassType(Class<?> idClassType);
}
