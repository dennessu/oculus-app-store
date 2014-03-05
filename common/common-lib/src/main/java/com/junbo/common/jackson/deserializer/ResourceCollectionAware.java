/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson.deserializer;

/**
 * Java doc.
 */
public interface ResourceCollectionAware {
    void injectCollectionType(Class<?> collectionType);
}
