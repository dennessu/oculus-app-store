package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserAttributeId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.UserPropertyAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.UserAttributeFilter
import com.junbo.identity.core.service.validator.UserAttributeValidator
import com.junbo.identity.service.UserAttributeService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserAttribute
import com.junbo.identity.spec.v1.option.list.UserAttributeListOptions
import com.junbo.identity.spec.v1.option.model.UserAttributeGetOptions
import com.junbo.identity.spec.v1.resource.UserAttributeResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.core.Response

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
class UserAttributeResourceImpl implements UserAttributeResource {
    @Autowired
    private UserAttributeService userAttributeService

    @Autowired
    private UserAttributeFilter userAttributeFilter

    @Autowired
    private UserAttributeValidator userAttributeValidator

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserPropertyAuthorizeCallbackFactory authorizeCallbackFactory

    Promise<UserAttribute> create(UserAttribute userAttribute) {
        if (userAttribute == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        if (userAttribute.userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('user').exception()
        }

        def callback = authorizeCallbackFactory.create(userAttribute.userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            userAttribute = userAttributeFilter.filterForCreate(userAttribute)

            return userAttributeValidator.validateForCreate(userAttribute).then {
                return userAttributeService.create(userAttribute).then { UserAttribute newUserAttribute ->
                        Created201Marker.mark(newUserAttribute.getId())

                        newUserAttribute = userAttributeFilter.filterForGet(newUserAttribute, null)
                        return Promise.pure(newUserAttribute)
                }
            }
        }
    }

    Promise<UserAttribute> patch(UserAttributeId userAttributeId, UserAttribute userAttribute) {
        if (userAttributeId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        if (userAttribute == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        return userAttributeService.get(userAttributeId).then { UserAttribute oldUserAttribute ->
            if (oldUserAttribute == null) {
                throw AppErrors.INSTANCE.userAttributeNotFound(userAttributeId).exception()
            }

            def callback = authorizeCallbackFactory.create(oldUserAttribute.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                userAttribute = userAttributeFilter.filterForPatch(userAttribute, oldUserAttribute)
                return userAttributeValidator.validateForUpdate(userAttributeId, userAttribute, oldUserAttribute).then {
                    return userAttributeService.update(userAttribute, oldUserAttribute).then { UserAttribute newUserAttribute ->
                        newUserAttribute = userAttributeFilter.filterForGet(newUserAttribute, null)
                        return Promise.pure(newUserAttribute)
                    }
                }
            }
        }
    }

    Promise<UserAttribute> put(UserAttributeId userAttributeId, UserAttribute userAttribute) {
        if (userAttributeId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        if (userAttribute == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        return userAttributeService.get(userAttributeId).then { UserAttribute oldUserAttribute ->
            if (oldUserAttribute == null) {
                throw AppErrors.INSTANCE.userAttributeNotFound(userAttributeId).exception()
            }

            def callback = authorizeCallbackFactory.create(oldUserAttribute.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                userAttribute = userAttributeFilter.filterForPut(userAttribute, oldUserAttribute)
                return userAttributeValidator.validateForUpdate(userAttributeId, userAttribute, oldUserAttribute).then {
                    return userAttributeService.update(userAttribute, oldUserAttribute).then { UserAttribute newUserAttribute ->
                        newUserAttribute = userAttributeFilter.filterForGet(newUserAttribute, null)
                        return Promise.pure(newUserAttribute)
                    }
                }
            }
        }
    }

    Promise<UserAttribute> get(UserAttributeId userAttributeId, UserAttributeGetOptions getOptions) {
        return userAttributeValidator.validateForGet(userAttributeId).then { UserAttribute existing ->
                def callback = authorizeCallbackFactory.create(existing.userId)
                return RightsScope.with(authorizeService.authorize(callback)) {
                    if (!AuthorizeContext.hasRights('read')) {
                        throw AppErrors.INSTANCE.userAttributeNotFound(userAttributeId).exception()
                    }

                    existing = userAttributeFilter.filterForGet(existing, getOptions.properties?.split(',') as List<String>)
                    return Promise.pure(existing)
                }
        }
    }

    Promise<Results<UserAttribute>> list(UserAttributeListOptions listOptions) {
        return userAttributeValidator.validateForSearch(listOptions).then {
            return search(listOptions).then { Results<UserAttribute> userAttributes ->
                def result = new Results<UserAttribute>(items: [])
                result.total = userAttributes.total

                return Promise.each(userAttributes.items) { UserAttribute newUserAttribute ->
                    def callback = authorizeCallbackFactory.create(newUserAttribute.userId)
                    return RightsScope.with(authorizeService.authorize(callback)) {
                        if (newUserAttribute != null) {
                            newUserAttribute = userAttributeFilter.filterForGet(newUserAttribute,
                                    listOptions.properties?.split(',') as List<String>)
                        }

                        if (newUserAttribute != null && AuthorizeContext.hasRights('read')) {
                            result.items.add(newUserAttribute)
                            return Promise.pure(newUserAttribute)
                        } else {
                            return Promise.pure(null)
                        }
                    }
                }.then {
                    return Promise.pure(result)
                }
            }
        }
    }

    Promise<Response> delete(UserAttributeId userAttributeId) {
        return userAttributeValidator.validateForGet(userAttributeId).then { UserAttribute existing ->
            def callback = authorizeCallbackFactory.create(existing.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('delete')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }
                return userAttributeService.delete(userAttributeId).then {
                    return Promise.pure(Response.status(204).build())
                }
            }
        }
    }

    Promise<Results<UserAttribute>> search(UserAttributeListOptions options) {
        if (options.userId != null && options.userAttributeDefinitionId != null) {
            return userAttributeService.searchByUserIdAndAttributeDefinitionId(options.userId,
                    options.userAttributeDefinitionId, options.limit, options.offset)
        } else if (options.userId != null) {
            return userAttributeService.searchByUserId(options.userId, options.limit, options.offset)
        } else if (options.userAttributeDefinitionId != null) {
            return userAttributeService.searchByUserAttributeDefinitionId(options.userAttributeDefinitionId, options.limit, options.offset)
        } else {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId or userAttributeDefinitionId').exception()
        }
    }
}
