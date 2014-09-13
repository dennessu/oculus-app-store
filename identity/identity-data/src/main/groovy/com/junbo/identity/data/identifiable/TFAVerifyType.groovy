package com.junbo.identity.data.identifiable

import com.junbo.common.util.Identifiable
import groovy.transform.CompileStatic

import javax.ws.rs.NotSupportedException

/**
 * Created by liangfu on 4/30/14.
 */
@CompileStatic
enum TFAVerifyType implements Identifiable<Short> {
    CALL((short)1),
    SMS((short)2),
    EMAIL((short)3)

    private final Short id

    TFAVerifyType(Short id) {
        this.id = id
    }

    @Override
    Short getId() {
        return id
    }

    @Override
    void setId(Short id) {
        throw new NotSupportedException('enum TFAVerifyType not settable')
    }
}
