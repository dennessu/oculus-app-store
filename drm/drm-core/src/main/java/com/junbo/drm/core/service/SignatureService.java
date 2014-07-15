/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.drm.core.service;

/**
 * SignatureService.
 */
public interface SignatureService {
    byte[] sign(byte[] bytes);
}
