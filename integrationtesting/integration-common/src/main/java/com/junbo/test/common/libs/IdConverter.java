/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.libs;

import com.junbo.common.id.Id;
import com.junbo.common.id.OrderId;
import com.junbo.common.shuffle.Oculus40Id;
import com.junbo.common.shuffle.Oculus48Id;
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

    public static String idLongToHexString(Class cls, long id) {
        if(cls == OrderId.class) {
            Oculus40Id.validateRawValue(id);
            Long value = Oculus40Id.shuffle(id);
            return Oculus40Id.format(value);
        }
        else if(cls.getSuperclass() == Id.class){
            Oculus48Id.validateRawValue(id);
            Long value = Oculus48Id.shuffle(id);
            return Oculus48Id.format(value);
        }
        else {
            throw new RuntimeException("unsupported class " + cls);
        }
    }
}
