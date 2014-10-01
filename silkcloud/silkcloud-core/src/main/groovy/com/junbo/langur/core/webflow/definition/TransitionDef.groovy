package com.junbo.langur.core.webflow.definition

import com.junbo.langur.core.webflow.action.Action
import groovy.transform.CompileStatic

/**
 * Created by kg on 2/25/14.
 */
@CompileStatic
class TransitionDef {

    String on

    List<Action> actions

    String to
}
