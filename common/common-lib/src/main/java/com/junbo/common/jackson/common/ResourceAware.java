/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson.common;

/**
 * ResourceAware.
 */
public interface ResourceAware {
    void injectResourcePath(String resourceType);
}
