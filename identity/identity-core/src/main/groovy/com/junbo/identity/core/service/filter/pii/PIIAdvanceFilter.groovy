package com.junbo.identity.core.service.filter.pii

import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
public interface PIIAdvanceFilter {
    boolean handles(UserPersonalInfo userPersonalInfo)

    UserPersonalInfo getFilter(UserPersonalInfo userPersonalInfo)

    void checkCreatePermission()

    void checkUpdatePermission()

    void checkGetPermission(UserPersonalInfoId piiId)

    void checkDeletePermission()
}
