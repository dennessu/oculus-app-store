package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserSecurityQuestionAttemptFilter
import com.junbo.identity.core.service.validator.UserSecurityQuestionAttemptValidator
import com.junbo.identity.data.repository.UserSecurityQuestionAttemptRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserSecurityQuestionVerifyAttempt
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionAttemptListOptions
import com.junbo.identity.spec.v1.resource.UserSecurityQuestionVerifyAttemptResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.transaction.AsyncTransactionTemplate
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionCallback
import javax.ws.rs.ext.Provider

/**
 * Created by liangfu on 4/11/14.
 */
@Provider
@Component
@Scope('prototype')
@Transactional
@CompileStatic
class UserSecurityQuestionVerifyAttemptResourceImpl implements UserSecurityQuestionVerifyAttemptResource {

    @Autowired
    private UserSecurityQuestionAttemptRepository userSecurityQuestionAttemptRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserSecurityQuestionAttemptFilter userSecurityQuestionAttemptFilter

    @Autowired
    private UserSecurityQuestionAttemptValidator userSecurityQuestionAttemptValidator

    @Autowired
    private PlatformTransactionManager transactionManager

    @Override
    Promise<UserSecurityQuestionVerifyAttempt> create(
            UserId userId, UserSecurityQuestionVerifyAttempt userSecurityQuestionAttempt) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userSecurityQuestionAttempt == null) {
            throw new IllegalArgumentException('userSecurityQuestionAttempt')
        }

        userSecurityQuestionAttempt = userSecurityQuestionAttemptFilter.filterForCreate(userSecurityQuestionAttempt)

        userSecurityQuestionAttemptValidator.validateForCreate(userId, userSecurityQuestionAttempt).then {

            createInNewTran(userSecurityQuestionAttempt).then { UserSecurityQuestionVerifyAttempt attempt ->

                if (attempt.succeeded == true) {
                    created201Marker.mark((Id)attempt.id)

                    attempt = userSecurityQuestionAttemptFilter.filterForGet(attempt, null)
                    return Promise.pure(attempt)
                }

                throw AppErrors.INSTANCE.userSecurityQuestionIncorrect().exception()
            }
        }
    }

    @Override
    Promise<Results<UserSecurityQuestionVerifyAttempt>> list(
            UserId userId, UserSecurityQuestionAttemptListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('userId is null')
        }
        listOptions.setUserId(userId)
        userSecurityQuestionAttemptValidator.validateForSearch(listOptions).then {
            userSecurityQuestionAttemptRepository.search(listOptions).
                    then { List<UserSecurityQuestionVerifyAttempt> userSecurityQuestionAttemptList ->
                        def result = new Results<UserSecurityQuestionVerifyAttempt>(items: [])

                        userSecurityQuestionAttemptList.each { UserSecurityQuestionVerifyAttempt attempt ->
                            attempt = userSecurityQuestionAttemptFilter.filterForGet(attempt,
                                    listOptions.properties?.split(',') as List<String>)
                            result.items.add(attempt)
                        }

                        return Promise.pure(result)
                    }
        }
    }

    Promise<UserSecurityQuestionVerifyAttempt> createInNewTran(UserSecurityQuestionVerifyAttempt userLoginAttempt) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return template.execute(new TransactionCallback<Promise<UserSecurityQuestionVerifyAttempt>>() {
                Promise<UserSecurityQuestionVerifyAttempt> doInTransaction(TransactionStatus txnStatus) {
                    return userSecurityQuestionAttemptRepository.create(userLoginAttempt)
                }
            }
        )
    }
}
