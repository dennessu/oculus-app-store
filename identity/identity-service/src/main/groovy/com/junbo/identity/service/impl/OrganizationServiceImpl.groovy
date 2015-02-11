package com.junbo.identity.service.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.OrganizationRepository
import com.junbo.identity.service.OrganizationService
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class OrganizationServiceImpl implements OrganizationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationServiceImpl)
    private OrganizationRepository organizationRepository

    // cache
    private OrganizationId defaultOrganizationIdCache;
    private String defaultOrganizationName;

    @Required
    void setOrganizationRepository(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository
    }

    @Required
    void setDefaultOrganizationName(String defaultOrganizationName) {
        this.defaultOrganizationName = defaultOrganizationName
    }

    @Override
    Promise<Organization> get(OrganizationId id) {
        return organizationRepository.get(id)
    }

    @Override
    Promise<Organization> create(Organization model) {
        return organizationRepository.create(model)
    }

    @Override
    Promise<Organization> update(Organization model, Organization oldModel) {
        return organizationRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(OrganizationId id) {
        return organizationRepository.delete(id)
    }

    @Override
    Promise<Results<Organization>> searchByOwner(UserId ownerId, Integer limit, Integer offset) {
        return organizationRepository.searchByOwner(ownerId, limit, offset)
    }

    @Override
    Promise<Results<Organization>> searchByCanonicalName(String name, Integer limit, Integer offset) {
        return organizationRepository.searchByCanonicalName(name, limit, offset)
    }

    @Override
    Promise<Organization> searchByMigrateCompanyId(Long migratedCompanyId) {
        return organizationRepository.searchByMigrateCompanyId(migratedCompanyId)
    }

    @Override
    Promise<Results<Organization>> searchAll(Integer limit, Integer offset) {
        return organizationRepository.searchAll(limit, offset)
    }

    @Override
    Promise<OrganizationId> getDefaultOrganizationId() {
        if (defaultOrganizationIdCache == null) {
            return searchByCanonicalName(defaultOrganizationName.toLowerCase(), 2, 0).then { Results<Organization> results ->
                if (results.items == null || results.items.size() == 0) {
                    LOGGER.error("Default Organization with name {} not found.", defaultOrganizationName);
                    throw AppCommonErrors.INSTANCE.resourceNotFound("organization", defaultOrganizationName).exception();
                } else if (results.items.size() > 1) {
                    LOGGER.warn("Found {} Organizations with name {}. Should find 1.", results.items.size(), defaultOrganizationName);
                }
                defaultOrganizationIdCache = results.items.get(0).getId();
                return Promise.pure(defaultOrganizationIdCache);
            }
        }

        return Promise.pure(defaultOrganizationIdCache);
    }
}
