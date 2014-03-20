/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.libs;

import com.junbo.common.id.Id;
import com.junbo.common.util.IdFormatter;

/**
 * @author Jason
 * Time: 3/20/204
 * Id converter between Long and Hex
 */
public class IdConverter {

    private IdConverter(){
    }

    public static Long hexStringToId(Class cls, String formattedId){
        return IdFormatter.decodeId(cls, formattedId);
    }

    public static String idToHexString(Id id) {
        return IdFormatter.encodeId(id);
    }
}
