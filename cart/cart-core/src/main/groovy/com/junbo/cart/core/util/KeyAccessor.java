/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.core.util;

/**
 * Created by fzhang@wan-san.com on 14-1-28.
 *
 * @param <K> the type of key
 * @param <T> the type of th owner
 */
public interface KeyAccessor<K, T> {
    K key(T t);
}
