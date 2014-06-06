package com.junbo.oauth.spec.model

import com.junbo.common.error.Error
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-29.
 */
@CompileStatic
class ViewModel {
    String view
    Map<String, Object> model
    List<Error> errors
}
