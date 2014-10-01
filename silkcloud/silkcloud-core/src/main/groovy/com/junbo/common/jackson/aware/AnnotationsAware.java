/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson.aware;

import com.fasterxml.jackson.databind.introspect.Annotated;

/**
 * AnnotationsAware.
 */
public interface AnnotationsAware {
    void injectAnnotations(Annotated annotations);
}
