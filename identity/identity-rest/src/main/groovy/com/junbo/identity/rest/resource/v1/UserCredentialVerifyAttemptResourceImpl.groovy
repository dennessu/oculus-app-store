package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserCredentialVerifyAttemptFilter
import com.junbo.identity.core.service.validator.UserCredentialVerifyAttemptValidator
import com.junbo.identity.data.repository.UserCredentialVerifyAttemptRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import com.junbo.identity.spec.v1.resource.UserCredentialVerifyAttemptResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.transaction.AsyncTransactionTemplate
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionCallback

import javax.ws.rs.ext.Provider

/**
 * Created by liangfu on 4/9/14.
 */
@Provider
@Component
@Scope('prototype')
@Transactional
@CompileStatic
class UserCredentialVerifyAttemptResourceImpl implements UserCredentialVerifyAttemptResource {

    @Autowired
    @Qualifier('userCredentialVerifyAttemptRepository')
    private UserCredentialVerifyAttemptRepository userLoginAttemptRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserCredentialVerifyAttemptFilter userCredentialVerifyAttemptFilter

    @Autowired
    private UserCredentialVerifyAttemptValidator credentialVerifyAttemptValidator

    @Autowired
    private PlatformTransactionManager transactionManager

    @Override
    Promise<UserCredentialVerifyAttempt> create(UserCredentialVerifyAttempt userCredentialAttempt) {
        if (userCredentialAttempt == null) {
            throw new IllegalArgumentException('userLoginAttempt is null')
        }

        userCredentialAttempt = userCredentialVerifyAttemptFilter.filterForCreate(userCredentialAttempt)

        credentialVerifyAttemptValidator.validateForCreate(userCredentialAttempt).then {

            createInNewTran(userCredentialAttempt).then { UserCredentialVerifyAttempt attempt ->

                if (attempt.succeeded == true) {
                    created201Marker.mark((Id)attempt.id)

                    attempt = userCredentialVerifyAttemptFilter.filterForGet(attempt, null)
                    return Promise.pure(attempt)
                }
                if (userCredentialAttempt.type == 'password') {
                    throw AppErrors.INSTANCE.userPasswordIncorrect().exception()
                }
                else {
                    throw AppErrors.INSTANCE.userPinIncorrect().exception()
                }
            }
        }
    }

    @Override
    Promise<Results<UserCredentialVerifyAttempt>> list(UserCredentialAttemptListOptions listOptions) {
        credentialVerifyAttemptValidator.validateForSearch(listOptions).then {
            userLoginAttemptRepository.search(listOptions).then { List<UserCredentialVerifyAttempt> attempts ->
                def result = new Results<UserCredentialVerifyAttempt>(items: [])

                attempts.each { UserCredentialVerifyAttempt attempt ->
                    attempt = userCredentialVerifyAttemptFilter.filterForGet(attempt,
                            listOptions.properties?.split(',') as List<String>)
                    result.items.add(attempt)
                }

                return Promise.pure(result)
            }
        }
    }

    Promise<UserCredentialVerifyAttempt> createInNewTran(UserCredentialVerifyAttempt userLoginAttempt) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return template.execute(new TransactionCallback<Promise<UserCredentialVerifyAttempt>>() {
            Promise<UserCredentialVerifyAttempt> doInTransaction(TransactionStatus txnStatus) {
                return userLoginAttemptRepository.create(userLoginAttempt)
            }
        }
        )
    }
}
