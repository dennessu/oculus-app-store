package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.UserPropertyAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.UserSecurityQuestionFilter
import com.junbo.identity.core.service.validator.UserSecurityQuestionValidator
import com.junbo.identity.data.repository.UserSecurityQuestionRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserSecurityQuestion
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionListOptions
import com.junbo.identity.spec.v1.option.model.UserSecurityQuestionGetOptions
import com.junbo.identity.spec.v1.resource.UserSecurityQuestionResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 4/11/14.
 */
@Transactional
@CompileStatic
class UserSecurityQuestionResourceImpl implements UserSecurityQuestionResource {

    @Autowired
    private UserSecurityQuestionRepository userSecurityQuestionRepository

    @Autowired
    private UserSecurityQuestionFilter userSecurityQuestionFilter

    @Autowired
    private UserSecurityQuestionValidator userSecurityQuestionValidator

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserPropertyAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    Promise<UserSecurityQuestion> create(UserId userId, UserSecurityQuestion userSecurityQuestion) {
        if (userSecurityQuestion == null) {
            throw new IllegalArgumentException('userSecurityQuestion is null')
        }

        def callback = authorizeCallbackFactory.create(userSecurityQuestion.userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            userSecurityQuestion = userSecurityQuestionFilter.filterForCreate(userSecurityQuestion)

            return userSecurityQuestionValidator.validateForCreate(userId, userSecurityQuestion).then {
                return userSecurityQuestionRepository.create(userSecurityQuestion).
                        then { UserSecurityQuestion newUserSecurityQuestion ->
                            Created201Marker.mark(newUserSecurityQuestion.getId())

                            newUserSecurityQuestion = userSecurityQuestionFilter.filterForGet(newUserSecurityQuestion, null)
                            return Promise.pure(newUserSecurityQuestion)
                        }
            }
        }
    }

    @Override
    Promise<UserSecurityQuestion> get(UserId userId, UserSecurityQuestionId userSecurityQuestionId,
                                      UserSecurityQuestionGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return userSecurityQuestionValidator.validateForGet(userId, userSecurityQuestionId).
            then { UserSecurityQuestion newUserSecurityQuestion ->
            def callback = authorizeCallbackFactory.create(newUserSecurityQuestion.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('read')) {
                    throw AppErrors.INSTANCE.userNotFound(userId).exception()
                }

                newUserSecurityQuestion = userSecurityQuestionFilter.filterForGet(newUserSecurityQuestion,
                        getOptions.properties?.split(',') as List<String>)

                return Promise.pure(newUserSecurityQuestion)
            }
        }
    }

    @Override
    Promise<UserSecurityQuestion> patch(UserId userId, UserSecurityQuestionId userSecurityQuestionId,
                                        UserSecurityQuestion userSecurityQuestion) {
        if (userSecurityQuestionId == null) {
            throw new IllegalArgumentException('userSecurityQuestionId is null')
        }

        if (userSecurityQuestion == null) {
            throw new IllegalArgumentException('userSecurityQuestion is null')
        }

        return userSecurityQuestionRepository.get(userSecurityQuestionId).then {
            UserSecurityQuestion oldUserSecurityQuestion ->
            def callback = authorizeCallbackFactory.create(userSecurityQuestion.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                if (oldUserSecurityQuestion == null) {
                    throw AppErrors.INSTANCE.userSecurityQuestionNotFound(userSecurityQuestionId).exception()
                }

                userSecurityQuestion = userSecurityQuestionFilter.
                        filterForPatch(userSecurityQuestion, oldUserSecurityQuestion)

                return userSecurityQuestionValidator.validateForUpdate(
                        userId, userSecurityQuestionId, userSecurityQuestion, oldUserSecurityQuestion).then {

                    return userSecurityQuestionRepository.update(userSecurityQuestion).
                            then { UserSecurityQuestion newUserSecurityQuestion ->
                        newUserSecurityQuestion = userSecurityQuestionFilter.filterForGet(newUserSecurityQuestion, null)
                        return Promise.pure(newUserSecurityQuestion)
                    }
                }
            }
        }
    }

    @Override
    Promise<UserSecurityQuestion> put(UserId userId, UserSecurityQuestionId userSecurityQuestionId,
                                      UserSecurityQuestion userSecurityQuestion) {
        if (userSecurityQuestionId == null) {
            throw new IllegalArgumentException('userSecurityQuestionId is null')
        }

        if (userSecurityQuestion == null) {
            throw new IllegalArgumentException('userSecurityQuestion is null')
        }

        def callback = authorizeCallbackFactory.create(userSecurityQuestion.userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('update')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return userSecurityQuestionRepository.get(userSecurityQuestionId).then {
                UserSecurityQuestion oldUserSecurityQuestion ->
                if (oldUserSecurityQuestion == null) {
                    throw AppErrors.INSTANCE.userSecurityQuestionNotFound(userSecurityQuestionId).exception()
                }

                userSecurityQuestion = userSecurityQuestionFilter.
                        filterForPut(userSecurityQuestion, oldUserSecurityQuestion)

                return userSecurityQuestionValidator.validateForUpdate(
                        userId, userSecurityQuestionId, userSecurityQuestion, oldUserSecurityQuestion).then {
                    return userSecurityQuestionRepository.update(userSecurityQuestion).
                            then { UserSecurityQuestion newUserSecurityQuestion ->
                        newUserSecurityQuestion = userSecurityQuestionFilter.filterForGet(newUserSecurityQuestion, null)
                        return Promise.pure(newUserSecurityQuestion)
                    }
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserId userId, UserSecurityQuestionId userSecurityQuestionId) {
        return userSecurityQuestionValidator.validateForGet(userId, userSecurityQuestionId)
                .then { UserSecurityQuestion userSecurityQuestion ->
            def callback = authorizeCallbackFactory.create(userSecurityQuestion.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('delete')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return userSecurityQuestionRepository.delete(userSecurityQuestionId)
            }
        }
    }

    @Override
    Promise<Results<UserSecurityQuestion>> list(UserId userId, UserSecurityQuestionListOptions listOptions) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            def result = new Results<UserSecurityQuestion>(items: [])
            if (!AuthorizeContext.hasRights('read')) {
                return Promise.pure(result)
            }

            listOptions.setUserId(userId)

            return userSecurityQuestionValidator.validateForSearch(listOptions).then {
                return search(listOptions).then { List<UserSecurityQuestion> userSecurityQuestionList ->


                    return Promise.each(userSecurityQuestionList) { UserSecurityQuestion newUserSecurityQuestion ->

                        if (newUserSecurityQuestion != null) {
                            newUserSecurityQuestion = userSecurityQuestionFilter.filterForGet(newUserSecurityQuestion,
                                    listOptions.properties?.split(',') as List<String>)
                        }

                        if (newUserSecurityQuestion != null) {
                            result.items.add(newUserSecurityQuestion)
                        }

                        return Promise.pure(null)
                    }
                }.then {
                    return Promise.pure(result)
                }
            }
        }
    }

    private Promise<List<UserSecurityQuestion>> search(UserSecurityQuestionListOptions listOptions) {
        if (listOptions.userId != null) {
            return userSecurityQuestionRepository.searchByUserId(listOptions.userId, listOptions.limit,
                    listOptions.offset)
        } else {
            throw new IllegalArgumentException("Unsupported search operation.")
        }
    }
}
