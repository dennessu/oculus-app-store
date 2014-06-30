package com.junbo.identity.core.service.filter.pii

import groovy.transform.CompileStatic

/**
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
public interface PIIAdvanceFilterFactory {
    List<PIIAdvanceFilter> getAll()
}
