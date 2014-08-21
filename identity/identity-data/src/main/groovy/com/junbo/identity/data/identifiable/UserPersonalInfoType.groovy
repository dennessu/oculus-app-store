package com.junbo.identity.data.identifiable

import com.junbo.common.util.Identifiable
import groovy.transform.CompileStatic

import javax.ws.rs.NotSupportedException

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
enum UserPersonalInfoType implements Identifiable<Short> {

    WIPED((short)1),
    ADDRESS((short)2),
    EMAIL((short)3),
    PHONE((short)4),
    NAME((short)5),
    DOB((short)6),
    SMS((short)7),
    QQ((short)8),
    WHATSAPP((short)9),
    PASSPORT((short)10),
    GOVERNMENT_ID((short)11),
    DRIVERS_LICENSE((short)12),
    GENDER((short)13),
    SSN((short)14),
    TIN((short)15),
    EIN((short)16),
    USERNAME((short)17),
    TEXT_MESSAGE((short)18)

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
