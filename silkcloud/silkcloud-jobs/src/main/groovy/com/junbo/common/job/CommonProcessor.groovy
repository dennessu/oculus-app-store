package com.junbo.common.job

import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.data.PendingAction
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 7/2/14.
 */
@CompileStatic
public interface CommonProcessor {
    Promise<CommonProcessorResult> process(PendingAction pendingAction)
}
