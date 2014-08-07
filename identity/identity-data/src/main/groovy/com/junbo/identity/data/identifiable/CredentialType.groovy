package com.junbo.identity.data.identifiable

import com.junbo.common.util.Identifiable
import groovy.transform.CompileStatic

import javax.transaction.NotSupportedException

/**
 * Created by liangfu on 4/25/14.
 */
@CompileStatic
enum CredentialType implements Identifiable<Short> {
    PASSWORD((short)1),
    PIN((short)2),
    CHECK_NAME((short)3)

    private final Short id

    CredentialType(Short id) {
        this.id = id
    }

    @Override
    Short getId() {
        return id
    }

    @Override
    void setId(Short id) {
        throw new NotSupportedException('enum CredentialType not settable')
    }
}
