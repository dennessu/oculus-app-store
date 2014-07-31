package com.junbo.identity.data.identifiable

import com.junbo.common.util.Identifiable

import javax.ws.rs.NotSupportedException

/**
 * Created by liangfu on 7/31/14.
 */
enum TFASearchType implements Identifiable<Short> {
    MAIL((short)1),
    PHONE((short)2)

    private final Short id

    TFASearchType(Short id) {
        this.id = id
    }

    @Override
    Short getId() {
        return id
    }

    @Override
    void setId(Short id) {
        throw new NotSupportedException('enum TFASearchType not settable')
    }
}
