package com.junbo.identity.service.impl

import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserAttributeDefinitionId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.UserAttributeDefinitionRepository
import com.junbo.identity.service.UserAttributeDefinitionService
import com.junbo.identity.spec.v1.model.UserAttributeDefinition
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
class UserAttributeDefinitionServiceImpl implements UserAttributeDefinitionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAttributeDefinitionServiceImpl.class);

    UserAttributeDefinitionRepository userAttributeDefinitionRepository

    @Override
    Promise<UserAttributeDefinition> get(UserAttributeDefinitionId userAttributeDefinitionId) {
        return userAttributeDefinitionRepository.get(userAttributeDefinitionId)
    }

    @Override
    Promise<UserAttributeDefinition> create(UserAttributeDefinition userAttributeDefinition) {
        return userAttributeDefinitionRepository.create(userAttributeDefinition)
    }

    @Override
    Promise<UserAttributeDefinition> update(UserAttributeDefinition newUserAttributeDefinition,
                                            UserAttributeDefinition oldUserAttributeDefinition) {
        return userAttributeDefinitionRepository.update(newUserAttributeDefinition, oldUserAttributeDefinition)
    }

    @Override
    Promise<Void> delete(UserAttributeDefinitionId userAttributeDefinitionId) {
        return userAttributeDefinitionRepository.delete(userAttributeDefinitionId)
    }

    @Override
    Promise<Results<UserAttributeDefinition>> getAll(Integer limit, Integer offset) {
        Results<UserAttributeDefinition> results = new Results<>()
        return userAttributeDefinitionRepository.getAll(limit, offset).then { List<UserAttributeDefinition> userAttributeDefinitionList ->
            results.items = userAttributeDefinitionList

            return Promise.pure(results)
        }
    }

    @Override
    Promise<Results<UserAttributeDefinition>> getByOrganizationId(OrganizationId organizationId, Integer limit, Integer offset) {
        Results<UserAttributeDefinition> results = new Results<>()
        return userAttributeDefinitionRepository.getByOrganizationId(organizationId, limit, offset).then { List<UserAttributeDefinition> list ->
            results.items = list

            return Promise.pure(results)
        }
    }

    @Override
    Promise<UserAttributeDefinition> getByOrganizationIdAndType(OrganizationId organizationId, String type) {
        return userAttributeDefinitionRepository.getByOrganizationIdAndType(organizationId, type, 2, 0).then { List<UserAttributeDefinition> list ->
            if (list.size() > 1) {
                LOGGER.warn("Found {} user attribute defs with org {} and type {}. Should find 1.", list.size(), organizationId, type);
            }
            if (list.size() == 0) {
                return Promise.pure(null);
            } else {
                return Promise.pure(list.get(0));
            }
        }
    }

    @Required
    void setUserAttributeDefinitionRepository(UserAttributeDefinitionRepository userAttributeDefinitionRepository) {
        this.userAttributeDefinitionRepository = userAttributeDefinitionRepository
    }
}
