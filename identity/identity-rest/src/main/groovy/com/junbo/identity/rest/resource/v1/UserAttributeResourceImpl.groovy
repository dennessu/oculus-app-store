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
import com.junbo.identity.service.UserAttributeDefinitionService
import com.junbo.identity.service.UserAttributeService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserAttribute
import com.junbo.identity.spec.v1.model.UserAttributeDefinition
import com.junbo.identity.spec.v1.option.list.UserAttributeListOptions
import com.junbo.identity.spec.v1.option.model.UserAttributeGetOptions
import com.junbo.identity.spec.v1.resource.UserAttributeResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.core.Response
/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
class UserAttributeResourceImpl implements UserAttributeResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAttributeResourceImpl.class);

    @Autowired
    private UserAttributeService userAttributeService

    @Autowired
    private UserAttributeDefinitionService userAttributeDefinitionService

    @Autowired
    private UserAttributeFilter userAttributeFilter

    @Autowired
    private UserAttributeValidator userAttributeValidator

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserPropertyAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
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

    @Override
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

    @Override
    Promise<UserAttribute> get(UserAttributeId userAttributeId, UserAttributeGetOptions getOptions) {
        return userAttributeValidator.validateForGet(userAttributeId).then { UserAttribute existing ->
            return userAttributeDefinitionService.get(existing.userAttributeDefinitionId).then { UserAttributeDefinition uaDef ->
                if (uaDef == null) {
                    throw AppErrors.INSTANCE.userAttributeDefinitionNotFound(existing.userAttributeDefinitionId).exception();
                }
                if (uaDef.access == UserAttributeDefinition.ACCESS_PRIVATE) {
                    def callback = authorizeCallbackFactory.create(existing.userId)
                    return RightsScope.with(authorizeService.authorize(callback)) {
                        if (!AuthorizeContext.hasRights('read')) {
                            throw AppErrors.INSTANCE.userAttributeNotFound(userAttributeId).exception()
                        }

                        existing = userAttributeFilter.filterForGet(existing, getOptions.properties?.split(',') as List<String>)
                        return Promise.pure(existing)
                    }
                } else {
                    // public access, anyone can read
                    existing = userAttributeFilter.filterForGet(existing, getOptions.properties?.split(',') as List<String>)
                    return Promise.pure(existing)
                }
            }
        }
    }

    @Override
    Promise<Results<UserAttribute>> list(UserAttributeListOptions listOptions) {
        return userAttributeValidator.validateForSearch(listOptions).then { UserAttributeDefinition uaDef ->
            listOptions.userAttributeDefinitionId = uaDef.getId()

            Promise<Boolean> authPromise = Promise.pure(true);
            if (uaDef.access == UserAttributeDefinition.ACCESS_PRIVATE) {
                def callback = authorizeCallbackFactory.create(listOptions.userId)
                authPromise = RightsScope.with(authorizeService.authorize(callback)) {
                    return Promise.pure(AuthorizeContext.hasRights('read'))
                }
            }
            return authPromise.then { Boolean isAuthorized ->
                if (!isAuthorized) {
                    Results<UserAttribute> emptyResults = new Results<>();
                    emptyResults.items = new ArrayList<>();
                    emptyResults.total = 0;
                    return Promise.pure(emptyResults);
                }

                return search(listOptions).then { Results<UserAttribute> userAttributes ->
                    // optional re-validation.
                    def userAttributeDefIds = userAttributes.items
                            .collect { UserAttribute ua -> ua.userAttributeDefinitionId }.unique()
                    if (userAttributeDefIds.size() > 1) {
                        LOGGER.error("User Attribute Definition Search responded other definitions. Expected: {} Responses: {}", uaDef.id, userAttributeDefIds.join(","));
                        throw AppCommonErrors.INSTANCE.internalServerError().exception();
                    }
                    if (userAttributeDefIds.size() > 0) {
                        if (userAttributeDefIds.first() != uaDef.id) {
                            LOGGER.error("User Attribute Definition Search responded other definitions. Expected: {} Responses: {}", uaDef.id, userAttributeDefIds.first());
                            throw AppCommonErrors.INSTANCE.internalServerError().exception();
                        }
                    }

                    def result = new Results<UserAttribute>(items: [])
                    result.total = userAttributes.total

                    // no more authorization checks. It's done at the beginning.
                    // do filterForGet for properties
                    userAttributes.items.each { UserAttribute newUserAttribute ->
                        if (newUserAttribute != null) {
                            newUserAttribute = userAttributeFilter.filterForGet(newUserAttribute,
                                    listOptions.properties?.split(',') as List<String>)
                        }
                        result.items.add(newUserAttribute)
                    }
                    return Promise.pure(result)
                }
            }
        }
    }

    @Override
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
        if (options.userId != null) {
            return userAttributeService.searchByUserIdAndAttributeDefinitionId(options.userId,
                    options.userAttributeDefinitionId, options.activeOnly, options.limit, options.offset)
        } else {
            return userAttributeService.searchByUserAttributeDefinitionId(options.userAttributeDefinitionId, options.activeOnly, options.limit, options.offset)
        }
    }
}
