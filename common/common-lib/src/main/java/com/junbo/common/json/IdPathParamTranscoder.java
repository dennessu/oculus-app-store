/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.json;

import com.junbo.common.id.Id;
import com.junbo.common.util.IdFormatter;
import com.junbo.langur.core.client.PathParamTranscoder;

/**
 * Created by liangfu on 3/11/14.
 */
public class IdPathParamTranscoder implements PathParamTranscoder {
    @Override
    public <T> String encode(T pathParam) {
        if(pathParam.getClass() == Id.class || pathParam.getClass().getSuperclass() == Id.class) {
            return IdFormatter.encodeId((Id)pathParam);
        }
        else {
            return pathParam.toString();
        }
    }
}
