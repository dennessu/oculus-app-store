package com.junbo.identity.data.identifiable

import com.junbo.common.util.Identifiable
import groovy.transform.CompileStatic

import javax.ws.rs.NotSupportedException

/**
 * Created by liangfu on 6/5/14.
 */
@CompileStatic
enum OrganizationType implements Identifiable<Short> {
    INDIVIDUAL((short)1),
    CORPORATE((short)2)

    private final Short id

    OrganizationType(Short id) {
        this.id = id
    }

    @Override
    Short getId() {
        return id
    }

    @Override
    void setId(Short id) {
        throw new NotSupportedException('enum UserStatus not settable')
    }
}
