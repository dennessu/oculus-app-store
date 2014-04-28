package com.junbo.identity.data.identifiable

import com.junbo.common.util.Identifiable
import groovy.transform.CompileStatic

import javax.ws.rs.NotSupportedException

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
enum UserPersonalInfoType implements Identifiable<Short> {
    EMAIL((short)1),
    PHONE((short)2),
    NAME((short)3),
    DOB((short)4),
    SMS((short)5),
    QQ((short)6),
    WHATSAPP((short)7),
    PASSPORT((short)8),
    GOVERNMENT_ID((short)9),
    DRIVERS_LICENSE((short)10),
    GENDER((short)11),
    ADDRESS((short)12)

    private final Short id

    UserPersonalInfoType(Short id) {
        this.id = id
    }

    @Override
    Short getId() {
        return id
    }

    @Override
    void setId(Short id) {
        throw new NotSupportedException('enum UserProfileType not settable')
    }
}
