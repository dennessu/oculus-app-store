package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserCommunicationId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.UserPropertyAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.UserCommunicationFilter
import com.junbo.identity.core.service.validator.UserCommunicationValidator
import com.junbo.identity.service.UserCommunicationService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserCommunication
import com.junbo.identity.spec.v1.option.list.UserOptinListOptions
import com.junbo.identity.spec.v1.option.model.UserOptinGetOptions
import com.junbo.identity.spec.v1.resource.UserCommunicationResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.ws.rs.core.Response

/**
 * Created by liangfu on 4/11/14.
 */
@Transactional
@CompileStatic
class UserCommunicationResourceImpl implements UserCommunicationResource {

    @Autowired
    private UserCommunicationService userCommunicationService

    @Autowired
    private UserCommunicationFilter userCommunicationFilter

    @Autowired
    private UserCommunicationValidator userCommunicationValidator

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserPropertyAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    Promise<UserCommunication> create(UserCommunication userCommunication) {
        if (userCommunication == null) {
            throw new IllegalArgumentException('userCommunication is null')
        }

        def callback = authorizeCallbackFactory.create(userCommunication.userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            userCommunication = userCommunicationFilter.filterForCreate(userCommunication)

            return userCommunicationValidator.validateForCreate(userCommunication).then {
                return userCommunicationService.create(userCommunication).then {
                    UserCommunication newUserCommunication ->
                    Created201Marker.mark(newUserCommunication.getId())

                    newUserCommunication = userCommunicationFilter.filterForGet(newUserCommunication, null)
                    return Promise.pure(newUserCommunication)
                }
            }
        }
    }

    @Override
    Promise<UserCommunication> get(UserCommunicationId userCommunicationId, UserOptinGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return userCommunicationValidator.validateForGet(userCommunicationId).then {
            UserCommunication userCommunication ->

            def callback = authorizeCallbackFactory.create(userCommunication.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('read')) {
                    throw AppErrors.INSTANCE.userOptinNotFound(userCommunicationId).exception()
                }

                userCommunication = userCommunicationFilter.filterForGet(userCommunication,
                        getOptions.properties?.split(',') as List<String>)

                return Promise.pure(userCommunication)
            }
        }
    }

    @Override
    Promise<UserCommunication> patch(UserCommunicationId userCommunicationId, UserCommunication userCommunication) {
        if (userCommunicationId == null) {
            throw new IllegalArgumentException('userCommunicationId is null')
        }

        if (userCommunication == null) {
            throw new IllegalArgumentException('userCommunication is null')
        }

        return userCommunicationService.get(userCommunicationId).then { UserCommunication oldUserOptin ->
            if (oldUserOptin == null) {
                throw AppErrors.INSTANCE.userOptinNotFound(userCommunicationId).exception()
            }

            def callback = authorizeCallbackFactory.create(oldUserOptin.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                userCommunication = userCommunicationFilter.filterForPatch(userCommunication, oldUserOptin)

                return userCommunicationValidator.validateForUpdate(userCommunicationId, userCommunication,
                        oldUserOptin).then {
                    return userCommunicationService.update(userCommunication, oldUserOptin).then {
                        UserCommunication newUserCommunication ->
                            newUserCommunication = userCommunicationFilter.filterForGet(newUserCommunication, null)
                            return Promise.pure(newUserCommunication)
                    }
                }
            }
        }
    }

    @Override
    Promise<UserCommunication> put(UserCommunicationId userCommunicationId, UserCommunication userCommunication) {
        if (userCommunicationId == null) {
            throw new IllegalArgumentException('userCommunicationId is null')
        }

        if (userCommunication == null) {
            throw new IllegalArgumentException('userCommunication is null')
        }

        return userCommunicationService.get(userCommunicationId).then { UserCommunication oldUserOptin ->
            if (oldUserOptin == null) {
                throw AppErrors.INSTANCE.userOptinNotFound(userCommunicationId).exception()
            }

            def callback = authorizeCallbackFactory.create(oldUserOptin.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                userCommunication = userCommunicationFilter.filterForPut(userCommunication, oldUserOptin)

                return userCommunicationValidator.validateForUpdate(userCommunicationId, userCommunication, oldUserOptin)
                        .then {
                    return userCommunicationService.update(userCommunication, oldUserOptin).then { UserCommunication newUserCommunication ->
                        newUserCommunication = userCommunicationFilter.filterForGet(newUserCommunication, null)
                        return Promise.pure(newUserCommunication)
                    }
                }
            }
        }
    }

    @Override
    Promise<Response> delete(UserCommunicationId userCommunicationId) {
        return userCommunicationValidator.validateForGet(userCommunicationId).then {
            return userCommunicationService.delete(userCommunicationId).then {
                return Promise.pure(Response.status(204).build())
            }
        }
    }

    @Override
    Promise<Results<UserCommunication>> list(UserOptinListOptions listOptions) {
        return userCommunicationValidator.validateForSearch(listOptions).then {
            return search(listOptions).then { List<UserCommunication> userCommunications ->
                def result = new Results<UserCommunication>(items: [])

                return Promise.each(userCommunications) { UserCommunication newUserCommunication ->
                    def callback = authorizeCallbackFactory.create(newUserCommunication.userId)
                    return RightsScope.with(authorizeService.authorize(callback)) {
                        if (newUserCommunication != null) {
                            newUserCommunication = userCommunicationFilter.filterForGet(newUserCommunication,
                                    listOptions.properties?.split(',') as List<String>)
                        }

                        if (newUserCommunication != null && AuthorizeContext.hasRights('read')) {
                            result.items.add(newUserCommunication)
                            return Promise.pure(newUserCommunication)
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

    private Promise<List<UserCommunication>> search(UserOptinListOptions listOptions) {
        if (listOptions.userId != null && listOptions.communicationId != null) {
            return userCommunicationService.searchByUserIdAndCommunicationId(listOptions.userId,
                    listOptions.communicationId, listOptions.limit, listOptions.offset)
        } else if (listOptions.userId != null) {
            return userCommunicationService.searchByUserId(listOptions.userId, listOptions.limit, listOptions.offset)
        } else {
            throw AppCommonErrors.INSTANCE.invalidOperation('Unsupported search operation').exception()
        }
    }
}
