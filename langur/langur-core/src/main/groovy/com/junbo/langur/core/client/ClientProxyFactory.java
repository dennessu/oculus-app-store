/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.client;

/**
 * ClientProxyFactory.
 * @param <T> the resource type.
 */
public interface ClientProxyFactory<T> {
    /**
     * Create client proxy for type T.
     * @return the client proxy for type T.
     */
    T create(String targetUrl);
}
