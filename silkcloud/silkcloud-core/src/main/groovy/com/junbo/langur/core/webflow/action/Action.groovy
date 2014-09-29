package com.junbo.langur.core.webflow.action

import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by kg on 2/27/14.
 */
@CompileStatic
interface Action {

    Promise<ActionResult> execute(ActionContext context)
}
