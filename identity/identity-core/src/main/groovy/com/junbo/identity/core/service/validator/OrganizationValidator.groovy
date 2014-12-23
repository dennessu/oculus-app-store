package com.junbo.identity.core.service.validator

import com.junbo.common.id.OrganizationId
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/22/14.
 */
@CompileStatic
public interface OrganizationValidator {
    Promise<Organization> validateForGet(OrganizationId organizationId)
    Promise<Void> validateForSearch(OrganizationListOptions options)
    Promise<Void> validateForCreate(Organization organization)
    Promise<Void> validateForUpdate(OrganizationId organizationId, Organization organization,
                                    Organization oldOrganization)
    Promise<Void> validateForDelete(OrganizationId organizationId)
}
