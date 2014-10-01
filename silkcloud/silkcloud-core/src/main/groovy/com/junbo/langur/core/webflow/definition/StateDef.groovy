package com.junbo.langur.core.webflow.definition

import com.junbo.langur.core.webflow.action.Action
import groovy.transform.CompileStatic

/**
 * Created by kg on 2/25/14.
 */
@CompileStatic
abstract class StateDef {

    String id

    List<Action> entryActions
}
