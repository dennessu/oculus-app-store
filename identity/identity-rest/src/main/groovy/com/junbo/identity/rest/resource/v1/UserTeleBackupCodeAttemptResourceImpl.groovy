package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleBackupCodeAttemptId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.core.service.filter.UserTeleBackupCodeAttemptFilter
import com.junbo.identity.core.service.validator.UserTeleBackupCodeAttemptValidator
import com.junbo.identity.data.repository.UserTeleBackupCodeAttemptRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserTeleBackupCodeAttempt
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeAttemptListOptions
import com.junbo.identity.spec.v1.option.model.UserTeleBackupCodeAttemptGetOptions
import com.junbo.identity.spec.v1.resource.UserTeleBackupCodeAttemptResource
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
class UserTeleBackupCodeAttemptResourceImpl implements UserTeleBackupCodeAttemptResource {

    @Autowired
    private UserTeleBackupCodeAttemptRepository userTeleBackupCodeAttemptRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserTeleBackupCodeAttemptFilter userTeleBackupCodeAttemptFilter

    @Autowired
    private UserTeleBackupCodeAttemptValidator userTeleBackupCodeAttemptValidator

    @Autowired
    private PlatformTransactionManager transactionManager

    @Override
    Promise<UserTeleBackupCodeAttempt> create(UserId userId, UserTeleBackupCodeAttempt userTeleBackupCodeAttempt) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTeleBackupCodeAttempt == null) {
            throw new IllegalArgumentException('userTeleBackupCodeAttempt is null')
        }

        userTeleBackupCodeAttempt = userTeleBackupCodeAttemptFilter.filterForCreate(userTeleBackupCodeAttempt)

        return userTeleBackupCodeAttemptValidator.validateForCreate(userId, userTeleBackupCodeAttempt).then {

            return createInNewTran(userTeleBackupCodeAttempt).then { UserTeleBackupCodeAttempt attempt ->

                if (attempt.succeeded == true) {
                    created201Marker.mark((Id)attempt.id)

                    attempt = userTeleBackupCodeAttemptFilter.filterForGet(attempt, null)
                    return Promise.pure(attempt)
                }

                throw AppErrors.INSTANCE.userTeleCodeIncorrect().exception()
            }
        }
    }

    @Override
    Promise<UserTeleBackupCodeAttempt> get(UserId userId, UserTeleBackupCodeAttemptId userTeleBackupCodeAttemptId,
                UserTeleBackupCodeAttemptGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return userTeleBackupCodeAttemptValidator.validateForGet(userId, userTeleBackupCodeAttemptId).
                then { UserTeleBackupCodeAttempt attempt ->
            attempt = userTeleBackupCodeAttemptFilter.filterForGet(attempt,
                    getOptions.properties?.split(',') as List<String>)

            return Promise.pure(attempt)
        }
    }

    @Override
    Promise<Results<UserTeleBackupCodeAttempt>> list(UserId userId, UserTeleBackupCodeAttemptListOptions listOptions) {
        return userTeleBackupCodeAttemptValidator.validateForSearch(userId, listOptions).then {
            return userTeleBackupCodeAttemptRepository.search(listOptions).
                    then { List<UserTeleBackupCodeAttempt> attemptList ->
                def result = new Results<UserTeleBackupCodeAttempt>(items: [])

                attemptList.each { UserTeleBackupCodeAttempt attempt ->
                    attempt = userTeleBackupCodeAttemptFilter.filterForGet(attempt,
                            listOptions.properties?.split(',') as List<String>)
                    result.items.add(attempt)
                }

                return Promise.pure(result)
            }
        }
    }

    Promise<UserTeleBackupCodeAttempt> createInNewTran(UserTeleBackupCodeAttempt attempt) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return template.execute(new TransactionCallback<Promise<UserTeleBackupCodeAttempt>>() {
            Promise<UserTeleBackupCodeAttempt> doInTransaction(TransactionStatus txnStatus) {
                return userTeleBackupCodeAttemptRepository.create(attempt)
            }
        }
        )
    }
}
