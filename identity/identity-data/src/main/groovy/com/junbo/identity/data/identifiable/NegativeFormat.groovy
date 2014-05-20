package com.junbo.identity.data.identifiable

import com.junbo.common.util.Identifiable
import groovy.transform.CompileStatic

import javax.ws.rs.NotSupportedException

/**
 * Created by liangfu on 5/20/14.
 */
@CompileStatic
enum NegativeFormat implements Identifiable<Short> {
    MINUS((short)1),
    BRACE((short)2)

    private final Short id

    NegativeFormat(Short id) {
        this.id = id
    }

    @Override
    Short getId() {
        return id
    }

    @Override
    void setId(Short id) {
        throw new NotSupportedException('enum NegativeFormat not settable')
    }
}
