package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
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

    @Override
    Promise<UserSecurityQuestion> create(UserId userId, UserSecurityQuestion userSecurityQuestion) {
        if (userSecurityQuestion == null) {
            throw new IllegalArgumentException('userSecurityQuestion is null')
        }

        userSecurityQuestion = userSecurityQuestionFilter.filterForCreate(userSecurityQuestion)

        return userSecurityQuestionValidator.validateForCreate(userId, userSecurityQuestion).then {
           return userSecurityQuestionRepository.create(userSecurityQuestion).
               then { UserSecurityQuestion newUserSecurityQuestion ->
                    Created201Marker.mark((Id)newUserSecurityQuestion.id)

                    newUserSecurityQuestion = userSecurityQuestionFilter.filterForGet(newUserSecurityQuestion, null)
                    return Promise.pure(newUserSecurityQuestion)
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
                newUserSecurityQuestion = userSecurityQuestionFilter.filterForGet(newUserSecurityQuestion,
                        getOptions.properties?.split(',') as List<String>)

            return Promise.pure(newUserSecurityQuestion)
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

    @Override
    Promise<UserSecurityQuestion> put(UserId userId, UserSecurityQuestionId userSecurityQuestionId,
                                      UserSecurityQuestion userSecurityQuestion) {
        if (userSecurityQuestionId == null) {
            throw new IllegalArgumentException('userSecurityQuestionId is null')
        }

        if (userSecurityQuestion == null) {
            throw new IllegalArgumentException('userSecurityQuestion is null')
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

    @Override
    Promise<Void> delete(UserId userId, UserSecurityQuestionId userSecurityQuestionId) {
        return userSecurityQuestionValidator.validateForGet(userId, userSecurityQuestionId).then {
            return userSecurityQuestionRepository.delete(userSecurityQuestionId)
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
        listOptions.setUserId(userId)

        return userSecurityQuestionValidator.validateForSearch(listOptions).then {
            return userSecurityQuestionRepository.search(listOptions)
                    .then { List<UserSecurityQuestion> userSecurityQuestionList ->
                def result = new Results<UserSecurityQuestion>(items: [])

                userSecurityQuestionList.each { UserSecurityQuestion newUserSecurityQuestion ->
                    if (newUserSecurityQuestion != null) {
                        newUserSecurityQuestion = userSecurityQuestionFilter.filterForGet(newUserSecurityQuestion,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newUserSecurityQuestion != null) {
                        result.items.add(newUserSecurityQuestion)
                    }
                }

                return Promise.pure(result)
            }
        }
    }
}
