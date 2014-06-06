package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.Organization
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/22/14.
 */
@CompileStatic
class OrganizationFilter extends ResourceFilterImpl<Organization> {

    @Override
    protected Organization filter(Organization model, MappingContext context) {
        return selfMapper.filterOrganization(model, context)
    }

    @Override
    protected Organization merge(Organization source, Organization base, MappingContext context) {
        return selfMapper.mergeOrganization(source, base, context)
    }
}
