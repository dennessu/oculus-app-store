/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util;

/**
 * Java doc.
 * @param <T>
 */
public interface Identifiable<T> {
    T getId();
    void setId(T id);
}
