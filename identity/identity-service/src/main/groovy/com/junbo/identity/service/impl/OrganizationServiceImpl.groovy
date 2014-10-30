package com.junbo.identity.service.impl

import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.OrganizationRepository
import com.junbo.identity.service.OrganizationService
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class OrganizationServiceImpl implements OrganizationService {
    private OrganizationRepository organizationRepository

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

    @Required
    void setOrganizationRepository(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository
    }
}
