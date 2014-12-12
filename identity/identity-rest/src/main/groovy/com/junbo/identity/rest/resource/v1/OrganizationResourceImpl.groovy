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
import com.junbo.identity.service.OrganizationService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

import javax.ws.rs.core.Response

/**
 * Created by liangfu on 5/22/14.
 */
@Transactional
@CompileStatic
class OrganizationResourceImpl implements OrganizationResource {

    @Autowired
    private OrganizationService organizationService

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

    @Value('${common.maximum.fetch.size}')
    private Integer maximumFetchSize

    @Override
    Promise<Organization> create(Organization organization) {
        if (organization == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        organization = organizationFilter.filterForCreate(organization)

        return organizationValidator.validateForCreate(organization).then {
            def callback = authorizeCallbackFactory.create(organization)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('create')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return organizationService.create(organization).then { Organization newOrganization ->
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
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        if (organization == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        return organizationService.get(organizationId).then { Organization oldOrganization ->
            if (oldOrganization == null) {
                throw AppErrors.INSTANCE.organizationNotFound(organizationId).exception()
            }
            def callback = authorizeCallbackFactory.create(oldOrganization)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                organization = organizationFilter.filterForPut(organization, oldOrganization)

                return organizationValidator.validateForUpdate(organizationId, organization, oldOrganization).then {
                    return organizationService.update(organization, oldOrganization).then { Organization newOrganization ->
                        newOrganization = organizationFilter.filterForGet(newOrganization, null)
                        return Promise.pure(newOrganization)
                    }
                }
            }
        }
    }

    @Override
    Promise<Organization> patch(OrganizationId organizationId, Organization organization) {
        if (organizationId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        if (organization == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        return organizationService.get(organizationId).then { Organization oldOrganization ->
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
                    return organizationService.update(organization, oldOrganization).then { Organization newOrganization ->
                        newOrganization = organizationFilter.filterForGet(newOrganization, null)
                        return Promise.pure(newOrganization)
                    }
                }
            }
        }
    }

    @Override
    Promise<Organization> get(OrganizationId organizationId, OrganizationGetOptions getOptions) {
        if (organizationId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return organizationValidator.validateForGet(organizationId).then {
            return organizationService.get(organizationId).then { Organization newOrganization ->
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
            return search(listOptions).then { Results<Organization> results ->
                List<Organization> newOrganizations = results.items
                if (newOrganizations == null) {
                    return Promise.pure(resultList)
                }
                resultList.total = results.total

                return Promise.each(newOrganizations) { Organization newOrganization ->
                    if (newOrganization != null) {
                        newOrganization = organizationFilter.filterForGet(newOrganization, listOptions.properties?.split(',') as List<String>)
                    }

                    if (newOrganization != null) {
                        def callback = authorizeCallbackFactory.create(newOrganization)
                        return RightsScope.with(authorizeService.authorize(callback)) {
                            if (AuthorizeContext.hasRights('read')) {
                                resultList.items.add(newOrganization)
                                return Promise.pure(newOrganization)
                            } else {
                                return Promise.pure(null)
                            }
                        }
                    }

                    return Promise.pure(null)
                }.then {
                    return Promise.pure(resultList)
                }
            }
        }
    }

    Promise<Results<Organization>> search(OrganizationListOptions listOptions) {
        if (listOptions.ownerId != null) {
            return organizationService.searchByOwner(listOptions.ownerId, listOptions.limit, listOptions.offset)
        } else if (!StringUtils.isEmpty(listOptions.name)) {
            return organizationService.searchByCanonicalName(normalizeService.normalize(listOptions.name), listOptions.limit,
                    listOptions.offset)
        } else if (listOptions.ownerId == null && StringUtils.isEmpty(listOptions.name)) {
            return organizationService.searchAll(listOptions.limit == null ? maximumFetchSize : listOptions.limit, listOptions.offset)
        } else {
            throw AppCommonErrors.INSTANCE.invalidOperation('Not support search').exception()
        }
    }

    @Override
    Promise<Response> delete(OrganizationId organizationId) {
        if (organizationId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }
        return organizationValidator.validateForDelete(organizationId).then { Organization organization ->
            def callback = authorizeCallbackFactory.create(organization)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('delete')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return organizationService.delete(organizationId).then {
                    return Promise.pure(Response.status(204).build())
                }
            }
        }
    }
}
