package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserCredentialVerifyAttemptId
import com.junbo.common.model.ResourceMetaBase
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.UserPropertyAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.UserCredentialVerifyAttemptFilter
import com.junbo.identity.core.service.validator.UserCredentialVerifyAttemptValidator
import com.junbo.identity.data.identifiable.CredentialType
import com.junbo.identity.service.UserCredentialVerifyAttemptService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import com.junbo.identity.spec.v1.option.model.UserCredentialAttemptGetOptions
import com.junbo.identity.spec.v1.resource.UserCredentialVerifyAttemptResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.transaction.AsyncTransactionTemplate
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionCallback

/**
 * Created by liangfu on 4/9/14.
 */
@Transactional
@CompileStatic
class UserCredentialVerifyAttemptResourceImpl implements UserCredentialVerifyAttemptResource {
    @Autowired
    private UserCredentialVerifyAttemptService userCredentialVerifyAttemptService

    @Autowired
    private UserCredentialVerifyAttemptFilter userCredentialVerifyAttemptFilter

    @Autowired
    private UserCredentialVerifyAttemptValidator credentialVerifyAttemptValidator

    @Autowired
    private PlatformTransactionManager transactionManager

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserPropertyAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    Promise<UserCredentialVerifyAttempt> create(UserCredentialVerifyAttempt userCredentialAttempt) {
        if (userCredentialAttempt == null) {
            throw new IllegalArgumentException('userLoginAttempt is null')
        }

        userCredentialAttempt = userCredentialVerifyAttemptFilter.filterForCreate(userCredentialAttempt)

        return credentialVerifyAttemptValidator.validateForCreate(userCredentialAttempt).recover { Throwable e ->
            userCredentialAttempt.succeeded = false
            return createInNewTran(userCredentialAttempt).then {
                throw e
            }
        }.then {
            // For any period login, will always mark it as failure
            if (userCredentialAttempt.isLockDownPeriodAttempt) {
                userCredentialAttempt.succeeded = false
            }
            return createInNewTran(userCredentialAttempt).then { UserCredentialVerifyAttempt attempt ->
                if (attempt.isLockDownPeriodAttempt) {
                    throw AppErrors.INSTANCE.maximumLoginAttempt().exception()
                }
                if (attempt.succeeded) {
                    if (attempt.id != null) {
                        Created201Marker.mark(attempt.getId())
                    }

                    attempt = userCredentialVerifyAttemptFilter.filterForGet(attempt, null)
                    attempt = RightsScope.filterForAdminInfo(attempt as ResourceMetaBase) as UserCredentialVerifyAttempt
                    return Promise.pure(attempt)
                }
                if (userCredentialAttempt.type == CredentialType.PASSWORD.toString()) {
                    throw AppErrors.INSTANCE.userPasswordIncorrect().exception()
                } else if (userCredentialAttempt.type == CredentialType.PIN.toString()) {
                    throw AppErrors.INSTANCE.userPinIncorrect().exception()
                } else {
                    throw new RuntimeException("Should never be here.")
                }
            }
        }
    }

    @Override
    Promise<Results<UserCredentialVerifyAttempt>> list(UserCredentialAttemptListOptions listOptions) {
        return credentialVerifyAttemptValidator.validateForSearch(listOptions).then {
            def callback = authorizeCallbackFactory.create(listOptions.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('read')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return search(listOptions).then {
                    List<UserCredentialVerifyAttempt> attempts ->
                    def result = new Results<UserCredentialVerifyAttempt>(items: [])

                    return Promise.each(attempts) { UserCredentialVerifyAttempt attempt ->

                        attempt = userCredentialVerifyAttemptFilter.filterForGet(attempt,
                                listOptions.properties?.split(',') as List<String>)
                        if (attempt != null && AuthorizeContext.hasRights('read')) {
                            result.items.add(attempt)
                            return Promise.pure(attempt)
                        } else {
                            return Promise.pure(null)
                        }
                    }.then {
                        return Promise.pure(result)
                    }
                }
            }
        }
    }

    @Override
    Promise<UserCredentialVerifyAttempt> get(UserCredentialVerifyAttemptId id,
                                             UserCredentialAttemptGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return credentialVerifyAttemptValidator.validateForGet(id).then { UserCredentialVerifyAttempt attempt ->
            def callback = authorizeCallbackFactory.create(attempt.userId)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('read')) {
                    throw AppErrors.INSTANCE.userLoginAttemptNotFound(attempt.id as UserCredentialVerifyAttemptId).exception()
                }

                attempt = userCredentialVerifyAttemptFilter.filterForGet(attempt,
                        getOptions.properties?.split(',') as List<String>)

                return Promise.pure(attempt)
            }
        }
    }

    Promise<UserCredentialVerifyAttempt> createInNewTran(UserCredentialVerifyAttempt userLoginAttempt) {
        if (userLoginAttempt.type == CredentialType.CHECK_NAME.toString()) {
            return Promise.pure(userLoginAttempt)
        }

        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return template.execute(new TransactionCallback<Promise<UserCredentialVerifyAttempt>>() {
            Promise<UserCredentialVerifyAttempt> doInTransaction(TransactionStatus txnStatus) {
                return userCredentialVerifyAttemptService.create(userLoginAttempt)
            }
        })
    }

    private Promise<List<UserCredentialVerifyAttempt>> search(UserCredentialAttemptListOptions listOptions) {
        if (listOptions.userId != null && listOptions.type != null) {
            return userCredentialVerifyAttemptService.searchByUserIdAndCredentialTypeAndInterval(listOptions.userId,
                    listOptions.type, 0L, listOptions.limit, listOptions.offset)
        } else if (listOptions.userId != null) {
            return userCredentialVerifyAttemptService.searchByUserId(listOptions.userId, listOptions.limit,
                    listOptions.offset)
        } else {
            throw new IllegalArgumentException('Unsupported search operation')
        }
    }
}
