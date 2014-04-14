package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.model.domaindata.SecurityQuestion
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/27/14.
 */
@CompileStatic
class SecurityQuestionFilter extends ResourceFilterImpl<SecurityQuestion> {
    @Override
    protected SecurityQuestion filter(SecurityQuestion securityQuestion, MappingContext context) {
        return selfMapper.filterSecurityQuestion(securityQuestion, context)
    }

    @Override
    protected SecurityQuestion merge(SecurityQuestion source, SecurityQuestion base, MappingContext context) {
        return selfMapper.mergeSecurityQuestion(source, base, context)
    }
}
