package com.junbo.email.db.entity

import com.junbo.common.util.Identifiable

/**
 * Created by Wei on 3/5/14.
 */
enum EmailType implements Identifiable<Short> {
    COMMERCE((short)1)

    EmailType(Short id) {
        this.id = id
    }

    private final short id

    Short getId() {
        return id
    }
}