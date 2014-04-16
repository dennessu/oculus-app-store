package com.junbo.oauth.core.model

import com.junbo.common.error.Error
import groovy.transform.CompileStatic

/**
 * Created by Shenhua on 4/9/2014.
 */
@CompileStatic
class ViewModel {
    String view
    Map<String, Object> model
    List<Error> errors
}
