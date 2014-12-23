package com.junbo.langur.core.context

import groovy.transform.CompileStatic

/**
 * Created by kg on 5/23/2014.
 */
@CompileStatic
interface JunboHttpContextScopeListener {

    void begin()

    void end()
}
