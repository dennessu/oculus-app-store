package com.junbo.identity.core.service.filter

import com.junbo.authorization.filter.AbstractResourceFilter
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class UserPersonalInfoFilter extends AbstractResourceFilter<UserPersonalInfo> {
    @Autowired
    protected SelfMapper selfMapper

    @Override
    protected UserPersonalInfo filter(UserPersonalInfo userPersonalInfo, MappingContext context) {
        return selfMapper.filterUserPersonalInfo(userPersonalInfo, context)
    }

    @Override
    protected UserPersonalInfo merge(UserPersonalInfo source, UserPersonalInfo base, MappingContext context) {
        return selfMapper.mergeUserPersonalInfo(source, base, context)
    }
}
