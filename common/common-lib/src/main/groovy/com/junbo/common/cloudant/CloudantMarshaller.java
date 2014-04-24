/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.cloudant;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

/**
 * The interface for Cloudant Marshaller.
 */
public interface CloudantMarshaller {
    String marshall(CloudantEntity object) throws JsonProcessingException;
    <T extends CloudantEntity> T unmarshall(String string, Class<T> clazz) throws IOException;
    <T extends CloudantEntity> T unmarshall(String string, Class<?> parametrized, Class<?> parameterClass) throws IOException;
}
