package com.junbo.identity.rest.resource.v1

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserAttributeDefinitionId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.core.service.filter.UserAttributeDefinitionFilter
import com.junbo.identity.core.service.validator.UserAttributeDefinitionValidator
import com.junbo.identity.service.UserAttributeDefinitionService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserAttributeDefinition
import com.junbo.identity.spec.v1.option.list.UserAttributeDefinitionListOptions
import com.junbo.identity.spec.v1.option.model.UserAttributeDefinitionGetOptions
import com.junbo.identity.spec.v1.resource.UserAttributeDefinitionResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.core.Response

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
class UserAttributeDefinitionResourceImpl implements UserAttributeDefinitionResource {
    @Autowired
    private UserAttributeDefinitionService userAttributeDefinitionService

    @Autowired
    private UserAttributeDefinitionFilter userAttributeDefinitionFilter

    @Autowired
    private UserAttributeDefinitionValidator userAttributeDefinitionValidator

    Promise<UserAttributeDefinition> create(UserAttributeDefinition userAttributeDefinition) {
        if (userAttributeDefinition == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        return userAttributeDefinitionValidator.validateForCreate(userAttributeDefinition).then {
            userAttributeDefinition = userAttributeDefinitionFilter.filterForCreate(userAttributeDefinition)
            return userAttributeDefinitionService.create(userAttributeDefinition).then { UserAttributeDefinition created ->
                Created201Marker.mark(created.getId())

                created = userAttributeDefinitionFilter.filterForGet(created, null)
                return Promise.pure(created)
            }
        }
    }

    Promise<UserAttributeDefinition> patch(UserAttributeDefinitionId userAttributeDefinitionId,
            UserAttributeDefinition userAttributeDefinition) {
        if (userAttributeDefinitionId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        if (userAttributeDefinition == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        if (userAttributeDefinition.id == null) {
            userAttributeDefinition.id = userAttributeDefinitionId
        }

        return userAttributeDefinitionService.get(userAttributeDefinitionId).then { UserAttributeDefinition oldUserAttributeDefinition ->
            if (oldUserAttributeDefinition == null) {
                throw AppErrors.INSTANCE.userAttributeDefinitionNotFound(userAttributeDefinitionId).exception()
            }

            userAttributeDefinition = userAttributeDefinitionFilter.filterForPatch(userAttributeDefinition, oldUserAttributeDefinition)

            return userAttributeDefinitionValidator.validateForUpdate(userAttributeDefinitionId, userAttributeDefinition,
                    oldUserAttributeDefinition).then {
                return userAttributeDefinitionService.update(userAttributeDefinition, oldUserAttributeDefinition).then {
                    UserAttributeDefinition newUserAttributeDefinition ->
                    newUserAttributeDefinition = userAttributeDefinitionFilter.filterForGet(newUserAttributeDefinition, null)
                    return Promise.pure(newUserAttributeDefinition)
                }
            }
        }
    }

    Promise<UserAttributeDefinition> put(UserAttributeDefinitionId userAttributeDefinitionId,
            UserAttributeDefinition userAttributeDefinition) {
        if (userAttributeDefinitionId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        if (userAttributeDefinition == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        return userAttributeDefinitionService.get(userAttributeDefinitionId).then { UserAttributeDefinition oldUserAttributeDefinition ->
            if (oldUserAttributeDefinition == null) {
                throw AppErrors.INSTANCE.userAttributeDefinitionNotFound(userAttributeDefinitionId).exception()
            }

            userAttributeDefinition = userAttributeDefinitionFilter.filterForPut(userAttributeDefinition, oldUserAttributeDefinition)

            return userAttributeDefinitionValidator.validateForUpdate(userAttributeDefinitionId,
                    userAttributeDefinition, oldUserAttributeDefinition).then {
                return userAttributeDefinitionService.update(userAttributeDefinition,
                        oldUserAttributeDefinition).then { UserAttributeDefinition newUserAttributeDefinition ->
                    newUserAttributeDefinition = userAttributeDefinitionFilter.filterForGet(newUserAttributeDefinition, null)
                    return Promise.pure(newUserAttributeDefinition)
                }
            }
        }
    }

    Promise<UserAttributeDefinition> get(UserAttributeDefinitionId userAttributeDefinitionId,
                                         UserAttributeDefinitionGetOptions getOptions) {
        if (userAttributeDefinitionId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return userAttributeDefinitionValidator.validateForGet(
                userAttributeDefinitionId).then { UserAttributeDefinition newUserAttributeDefinition ->
            if (newUserAttributeDefinition == null) {
                throw AppErrors.INSTANCE.userAttributeDefinitionNotFound(userAttributeDefinitionId).exception()
            }

            newUserAttributeDefinition = userAttributeDefinitionFilter.filterForGet(newUserAttributeDefinition,
                    getOptions.properties?.split(',') as List<String>)
            return Promise.pure(newUserAttributeDefinition)
        }
    }

    Promise<Results<UserAttributeDefinition>> list(UserAttributeDefinitionListOptions listOptions) {
        return userAttributeDefinitionValidator.validateForSearch(listOptions).then {
            def resultList = new Results<UserAttributeDefinition>(items: [])
            return search(listOptions).then { Results<UserAttributeDefinition> newUserAttributeDefinitions ->
                newUserAttributeDefinitions.items.each { UserAttributeDefinition newUserAttributeDefinition ->
                    if (newUserAttributeDefinition != null) {
                        newUserAttributeDefinition = userAttributeDefinitionFilter.filterForGet(newUserAttributeDefinition,
                                listOptions.properties?.split(',') as List<String>)
                        resultList.items.add(newUserAttributeDefinition)
                    }
                }
                return Promise.pure(resultList)
            }
        }
    }

    Promise<Response> delete(UserAttributeDefinitionId userAttributeDefinitionId) {
        return userAttributeDefinitionValidator.validateForGet(userAttributeDefinitionId).then {
            return userAttributeDefinitionService.delete(userAttributeDefinitionId).then {
                return Promise.pure(Response.status(204).build())
            }
        }
    }

    Promise<Results<UserAttributeDefinition>> search(UserAttributeDefinitionListOptions options) {
        return userAttributeDefinitionService.getAll(options.limit, options.offset)
    }
}
