package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleAttemptId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserTeleAttemptFilter
import com.junbo.identity.core.service.validator.UserTeleAttemptValidator
import com.junbo.identity.data.repository.UserTeleAttemptRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserTeleAttempt
import com.junbo.identity.spec.v1.option.list.UserTeleAttemptListOptions
import com.junbo.identity.spec.v1.option.model.UserTeleAttemptGetOptions
import com.junbo.identity.spec.v1.resource.UserTeleAttemptResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.transaction.AsyncTransactionTemplate
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallback

import javax.transaction.Transactional

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
@Transactional
class UserTeleAttemptResourceImpl implements UserTeleAttemptResource {

    @Autowired
    private UserTeleAttemptRepository userTeleAttemptRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserTeleAttemptFilter userTeleAttemptFilter

    @Autowired
    private UserTeleAttemptValidator userTeleAttemptValidator

    @Autowired
    private PlatformTransactionManager transactionManager

    @Override
    Promise<UserTeleAttempt> create(UserId userId, UserTeleAttempt userTeleAttempt) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTeleAttempt == null) {
            throw new IllegalArgumentException('userTeleAttempt is null')
        }

        userTeleAttempt = userTeleAttemptFilter.filterForCreate(userTeleAttempt)

        return userTeleAttemptValidator.validateForCreate(userId, userTeleAttempt).then {

            return createInNewTran(userTeleAttempt).then { UserTeleAttempt attempt ->

                if (attempt.succeeded == true) {
                    created201Marker.mark((Id)attempt.id)

                    // todo:    Later we can use one annotation to do this filter.
                    attempt.verifyCode = null
                    attempt = userTeleAttemptFilter.filterForGet(attempt, null)
                    return Promise.pure(attempt)
                }

                throw AppErrors.INSTANCE.userTeleCodeIncorrect().exception()
            }
        }
    }

    @Override
    Promise<UserTeleAttempt> get(UserId userId, UserTeleAttemptId userTeleAttemptId,
                                 UserTeleAttemptGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return userTeleAttemptValidator.validateForGet(userId, userTeleAttemptId).then { UserTeleAttempt attempt ->
            attempt = userTeleAttemptFilter.filterForGet(attempt, getOptions.properties?.split(',') as List<String>)

            return Promise.pure(attempt)
        }
    }

    @Override
    Promise<Results<UserTeleAttempt>> list(UserId userId, UserTeleAttemptListOptions listOptions) {
        return userTeleAttemptValidator.validateForSearch(userId, listOptions).then {
            return userTeleAttemptRepository.search(listOptions).then { List<UserTeleAttempt> attemptList ->
                def result = new Results<UserTeleAttempt>(items: [])

                attemptList.each { UserTeleAttempt attempt ->
                    attempt = userTeleAttemptFilter.filterForGet(attempt,
                    listOptions.properties?.split(',') as List<String>)
                    attempt.verifyCode = null
                    result.items.add(attempt)
                }

                return Promise.pure(result)
            }
        }
    }

    Promise<UserTeleAttempt> createInNewTran(UserTeleAttempt attempt) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return template.execute(new TransactionCallback<Promise<UserTeleAttempt>>() {
            Promise<UserTeleAttempt> doInTransaction(TransactionStatus txnStatus) {
                return userTeleAttemptRepository.create(attempt)
            }
        }
        )
    }
}
