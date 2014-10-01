/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.cloudant;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

/**
 * Created by Zhanxin on 5/26/2014.
 */
public interface CloudantMarshaller {
    String marshall(Object object) throws JsonProcessingException;
    <T> T unmarshall(String string, Class<T> clazz) throws IOException;
    <T> T unmarshall(String string, Class<T> parametrized, Class<?>... parameterClass) throws IOException;
}
