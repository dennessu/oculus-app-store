/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.model;

/**
 * Interface for model that needs to be sign during serialize.
 */
public interface SigningSupport {

    String getPayload();

    String getSignature();
}
