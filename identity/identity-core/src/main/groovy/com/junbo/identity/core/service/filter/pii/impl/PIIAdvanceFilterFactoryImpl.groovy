package com.junbo.identity.core.service.filter.pii.impl

import com.junbo.identity.core.service.filter.pii.PIIAdvanceFilter
import com.junbo.identity.core.service.filter.pii.PIIAdvanceFilterFactory
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
class PIIAdvanceFilterFactoryImpl implements PIIAdvanceFilterFactory {
    private final List<PIIAdvanceFilter> piiAdvanceFilterList

    PIIAdvanceFilterFactoryImpl(List<PIIAdvanceFilter> piiAdvanceFilterList) {
        this.piiAdvanceFilterList = piiAdvanceFilterList
    }

    @Override
    List<PIIAdvanceFilter> getAll() {
        return this.piiAdvanceFilterList
    }
}
