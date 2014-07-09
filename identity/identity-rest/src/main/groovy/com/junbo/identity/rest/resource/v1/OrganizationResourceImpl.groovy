package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.OrganizationAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.OrganizationFilter
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.validator.OrganizationValidator
import com.junbo.identity.data.repository.OrganizationRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 5/22/14.
 */
@Transactional
@CompileStatic
class OrganizationResourceImpl implements OrganizationResource {

    @Autowired
    private OrganizationRepository organizationRepository

    @Autowired
    private OrganizationFilter organizationFilter

    @Autowired
    private OrganizationValidator organizationValidator

    @Autowired
    private NormalizeService normalizeService

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private OrganizationAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    Promise<Organization> create(Organization organization) {
        organization = organizationFilter.filterForCreate(organization)

        return organizationValidator.validateForCreate(organization).then {
            def callback = authorizeCallbackFactory.create(organization)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('create')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return organizationRepository.create(organization).then { Organization newOrganization ->
                    Created201Marker.mark(newOrganization.getId())

                    newOrganization = organizationFilter.filterForGet(newOrganization, null)
                    return Promise.pure(newOrganization)
                }
            }
        }
    }

    @Override
    Promise<Organization> put(OrganizationId organizationId, Organization organization) {
        if (organizationId == null) {
            throw new IllegalArgumentException('organizationId is null')
        }

        if (organization == null) {
            throw new IllegalArgumentException('organization is null')
        }

        return organizationRepository.get(organizationId).then { Organization oldOrganization ->
            if (oldOrganization == null) {
                throw AppErrors.INSTANCE.organizationNotFound(organizationId).exception()
            }
            def callback = authorizeCallbackFactory.create(oldOrganization)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                organization = organizationFilter.filterForPatch(organization, oldOrganization)

                return organizationValidator.validateForUpdate(organizationId, organization, oldOrganization).then {
                    return organizationRepository.update(organization).then { Organization newOrganization ->
                        newOrganization = organizationFilter.filterForGet(newOrganization, null)
                        return Promise.pure(newOrganization)
                    }
                }
            }
        }
    }

    @Override
    Promise<Organization> get(OrganizationId organizationId, OrganizationGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return organizationValidator.validateForGet(organizationId).then {
            return organizationRepository.get(organizationId).then { Organization newOrganization ->
                def callback = authorizeCallbackFactory.create(newOrganization)
                return RightsScope.with(authorizeService.authorize(callback)) {
                    if (!AuthorizeContext.hasRights('read')) {
                        throw AppErrors.INSTANCE.organizationNotFound(organizationId).exception()
                    }

                    if (newOrganization == null) {
                        throw AppErrors.INSTANCE.organizationNotFound(organizationId).exception()
                    }

                    newOrganization = organizationFilter.filterForGet(newOrganization,
                            getOptions.properties?.split(',') as List<String>)
                    return Promise.pure(newOrganization)
                }
            }
        }
    }

    @Override
    Promise<Results<Organization>> list(OrganizationListOptions listOptions) {
        return organizationValidator.validateForSearch(listOptions).then {
            def resultList = new Results<Organization>(items: [])
            return search(listOptions).then { List<Organization> newOrganizations ->
                if (newOrganizations == null) {
                    return Promise.pure(resultList)
                }

                return Promise.each(newOrganizations) { Organization newOrganization ->
                    if (newOrganization != null) {
                        newOrganization = organizationFilter.filterForGet(newOrganization, listOptions.properties?.split(',') as List<String>)
                    }

                    if (newOrganization != null) {
                        def callback = authorizeCallbackFactory.create(newOrganization)
                        return RightsScope.with(authorizeService.authorize(callback)) {
                            if (AuthorizeContext.hasRights('read')) {
                                resultList.items.add(newOrganization)
                            }
                            return Promise.pure(null)
                        }
                    }

                    return Promise.pure(null)
                }.then {
                    return Promise.pure(resultList)
                }
            }
        }
    }

    Promise<List<Organization>> search(OrganizationListOptions listOptions) {
        if (listOptions.ownerId != null) {
            return organizationRepository.searchByOwner(listOptions.ownerId, listOptions.limit, listOptions.offset)
        } else if (!StringUtils.isEmpty(listOptions.name)) {
            return organizationRepository.searchByCanonicalName(normalizeService.normalize(listOptions.name), listOptions.limit,
                    listOptions.offset)
        } else {
            throw new IllegalArgumentException('Not support search')
        }
    }

    @Override
    Promise<Void> delete(OrganizationId organizationId) {
        return organizationValidator.validateForGet(organizationId).then { Organization organization ->
            def callback = authorizeCallbackFactory.create(organization)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('delete')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return organizationRepository.delete(organizationId)
            }
        }
    }
}
