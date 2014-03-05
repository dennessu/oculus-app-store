package com.junbo.langur.core.webflow.action

import groovy.transform.CompileStatic

/**
 * Created by kg on 2/27/14.
 */
@CompileStatic
class ActionResult {

    private final String id

    private final Map<String, Object> data

    ActionResult(String id) {
        this(id, [:])
    }

    ActionResult(String id, Map<String, Object> data) {
        if (id == null) {
            throw new IllegalArgumentException('id is null')
        }

        if (data == null) {
            throw new IllegalArgumentException('data is null')
        }

        this.id = id
        this.data = data
    }

    String getId() {
        return id
    }

    Map<String, Object> getData() {
        return data
    }
}
