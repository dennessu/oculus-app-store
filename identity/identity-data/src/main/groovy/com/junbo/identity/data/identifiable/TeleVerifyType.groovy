package com.junbo.identity.data.identifiable

import com.junbo.common.util.Identifiable
import groovy.transform.CompileStatic

import javax.ws.rs.NotSupportedException

/**
 * Created by liangfu on 4/30/14.
 */
@CompileStatic
enum TeleVerifyType implements Identifiable<Short> {
    CALL((short)1),
    SMS((short)2)

    private final Short id

    TeleVerifyType(Short id) {
        this.id = id
    }

    @Override
    Short getId() {
        return id
    }

    @Override
    void setId(Short id) {
        throw new NotSupportedException('enum TeleVerifyType not settable')
    }
}
