package com.junbo.langur.core.webflow.definition

import com.junbo.langur.core.webflow.action.Action
import groovy.transform.CompileStatic

/**
 * Created by kg on 2/25/14.
 */
@CompileStatic
class FlowDef {

    String id

    List<StateDef> states

    List<Action> startActions

    List<Action> endActions
}
