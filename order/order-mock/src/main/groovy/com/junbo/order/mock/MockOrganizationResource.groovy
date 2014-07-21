package com.junbo.order.mock

import com.junbo.common.id.OrganizationId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * Implementation of mock organization resource.
 */

@Component('mockOrganizationResource')
@CompileStatic
@TypeChecked
class MockOrganizationResource extends BaseMock implements OrganizationResource {
    @Override
    Promise<Organization> create(Organization organization) {
        return null
    }

    @Override
    Promise<Organization> put(OrganizationId organizationId, Organization organization) {
        return null
    }

    @Override
    Promise<Organization> get(OrganizationId organizationId, OrganizationGetOptions getOptions) {
        Organization organization = new Organization(
                id: new OrganizationId(generateLong()),
                name: 'mock_organization'
        )
        return Promise.pure(organization)
    }

    @Override
    Promise<Results<Organization>> list(OrganizationListOptions listOptions) {
        return null
    }

    @Override
    Promise<Void> delete(OrganizationId organizationIdId) {
        return null
    }
}
