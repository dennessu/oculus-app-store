/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.provider;

/**
 * Interface of EmailProvider.
 */
public interface EmailProvider {
    Response send(Request request);
}
