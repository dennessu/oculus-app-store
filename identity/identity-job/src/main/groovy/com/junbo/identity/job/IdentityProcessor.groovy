package com.junbo.identity.job

import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 7/2/14.
 */
@CompileStatic
public interface IdentityProcessor {
    Promise<IdentityProcessorResult> process(User user)
}
