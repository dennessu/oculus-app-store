/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.client;

/**
 * Created by liangfu on 3/11/14.
 */
public class QueryParamTranscoderImpl implements QueryParamTranscoder{
    @Override
    public <T> String encode(T pathParam) {
        throw new RuntimeException("Unsupported operation.");
    }
}
