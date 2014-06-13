/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.langur.core.client;

/**
 * Java doc.
 */
public interface MessageTranscoder {

    <T> T decode(TypeReference typeRef, String body);

    //use byte[] to resolve body encode problem
    <T> byte[] encode(T body);
}
