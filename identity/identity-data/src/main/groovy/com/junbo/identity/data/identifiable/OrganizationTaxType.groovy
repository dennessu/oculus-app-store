package com.junbo.identity.data.identifiable

import com.junbo.common.util.Identifiable
import groovy.transform.CompileStatic

import javax.ws.rs.NotSupportedException

/**
 * Created by liangfu on 6/5/14.
 */
@CompileStatic
enum OrganizationTaxType implements Identifiable<Short> {
    TIN((short)1),
    EIN((short)2),
    SSN((short)3)

    private final Short id

    OrganizationTaxType(Short id) {
        this.id = id
    }

    @Override
    Short getId() {
        return id
    }

    @Override
    void setId(Short id) {
        throw new NotSupportedException('enum OrganizationTaxType not settable')
    }
}
