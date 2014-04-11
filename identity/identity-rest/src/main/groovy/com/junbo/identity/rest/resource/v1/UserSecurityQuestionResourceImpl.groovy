package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
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
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.ws.rs.ext.Provider

/**
 * Created by liangfu on 4/11/14.
 */
@Provider
@Component
@Scope('prototype')
@Transactional
@CompileStatic
class UserSecurityQuestionResourceImpl implements UserSecurityQuestionResource {

    @Autowired
    private UserSecurityQuestionRepository userSecurityQuestionRepository

    @Autowired
    private Created201Marker created201Marker

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

        userSecurityQuestionValidator.validateForCreate(userId, userSecurityQuestion).then {
           return userSecurityQuestionRepository.create(userSecurityQuestion).
               then { UserSecurityQuestion newUserSecurityQuestion ->
                    created201Marker.mark((Id)newUserSecurityQuestion.id)

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

        userSecurityQuestionValidator.validateForGet(userId, userSecurityQuestionId).
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

            userSecurityQuestionValidator.validateForUpdate(
                    userId, userSecurityQuestionId, userSecurityQuestion, oldUserSecurityQuestion).then {

                userSecurityQuestionRepository.update(userSecurityQuestion).
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

            userSecurityQuestionValidator.validateForUpdate(
                    userId, userSecurityQuestionId, userSecurityQuestion, oldUserSecurityQuestion).then {
                userSecurityQuestionRepository.update(userSecurityQuestion).
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
            userSecurityQuestionRepository.delete(userSecurityQuestionId)

            return Promise.pure(null)
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

        return userSecurityQuestionValidator.validateForSearch(listOptions).then {
            userSecurityQuestionRepository.search(userId, listOptions)
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
