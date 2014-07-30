package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeAttemptId
import com.junbo.common.model.ResourceMetaBase
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.UserPropertyAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.UserTFABackupCodeAttemptFilter
import com.junbo.identity.core.service.validator.UserTFABackupCodeAttemptValidator
import com.junbo.identity.data.repository.UserTFABackupCodeAttemptRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserTFABackupCodeAttempt
import com.junbo.identity.spec.v1.option.list.UserTFABackupCodeAttemptListOptions
import com.junbo.identity.spec.v1.option.model.UserTFABackupCodeAttemptGetOptions
import com.junbo.identity.spec.v1.resource.UserTFABackupCodeAttemptResource
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
class UserTFABackupCodeAttemptResourceImpl implements UserTFABackupCodeAttemptResource {
    @Autowired
    private UserTFABackupCodeAttemptRepository userTFABackupCodeAttemptRepository

    @Autowired
    private UserTFABackupCodeAttemptFilter userTFABackupCodeAttemptFilter

    @Autowired
    private UserTFABackupCodeAttemptValidator userTFABackupCodeAttemptValidator

    @Autowired
    private PlatformTransactionManager transactionManager

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserPropertyAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    Promise<UserTFABackupCodeAttempt> create(UserId userId, UserTFABackupCodeAttempt userTFABackupCodeAttempt) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTFABackupCodeAttempt == null) {
            throw new IllegalArgumentException('userTFABackupCodeAttempt is null')
        }

        userTFABackupCodeAttempt = userTFABackupCodeAttemptFilter.filterForCreate(userTFABackupCodeAttempt)

        return userTFABackupCodeAttemptValidator.validateForCreate(userId, userTFABackupCodeAttempt).recover { Throwable e ->
            userTFABackupCodeAttempt.succeeded = false
            return createInNewTran(userTFABackupCodeAttempt).then {
                throw e
            }
        }.then {
            return createInNewTran(userTFABackupCodeAttempt).then { UserTFABackupCodeAttempt attempt ->

                if (attempt.succeeded) {
                    Created201Marker.mark(attempt.getId())

                    attempt = userTFABackupCodeAttemptFilter.filterForGet(attempt, null)
                    attempt = RightsScope.filterForAdminInfo(attempt as ResourceMetaBase) as UserTFABackupCodeAttempt
                    return Promise.pure(attempt)
                }

                throw AppErrors.INSTANCE.userTFACodeIncorrect().exception()
            }
        }
    }

    @Override
    Promise<UserTFABackupCodeAttempt> get(UserId userId, UserTFABackupCodeAttemptId userTFABackupCodeAttemptId,
                UserTFABackupCodeAttemptGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        if (userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('read')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return userTFABackupCodeAttemptValidator.validateForGet(userId, userTFABackupCodeAttemptId).
                    then { UserTFABackupCodeAttempt attempt ->
                attempt = userTFABackupCodeAttemptFilter.filterForGet(attempt,
                        getOptions.properties?.split(',') as List<String>)

                return Promise.pure(attempt)
            }
        }
    }

    @Override
    Promise<Results<UserTFABackupCodeAttempt>> list(UserId userId, UserTFABackupCodeAttemptListOptions listOptions) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('read')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return userTFABackupCodeAttemptValidator.validateForSearch(userId, listOptions).then {
                return search(listOptions).then { List<UserTFABackupCodeAttempt> attemptList ->
                    def result = new Results<UserTFABackupCodeAttempt>(items: [])

                    attemptList.each { UserTFABackupCodeAttempt attempt ->
                        attempt = userTFABackupCodeAttemptFilter.filterForGet(attempt,
                                listOptions.properties?.split(',') as List<String>)
                        result.items.add(attempt)
                    }

                    return Promise.pure(result)
                }
            }
        }
    }

    Promise<UserTFABackupCodeAttempt> createInNewTran(UserTFABackupCodeAttempt attempt) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return template.execute(new TransactionCallback<Promise<UserTFABackupCodeAttempt>>() {
            Promise<UserTFABackupCodeAttempt> doInTransaction(TransactionStatus txnStatus) {
                return userTFABackupCodeAttemptRepository.create(attempt)
            }
        }
        )
    }

    private Promise<List<UserTFABackupCodeAttempt>> search(UserTFABackupCodeAttemptListOptions listOptions) {
        if (listOptions.userId != null) {
            return userTFABackupCodeAttemptRepository.searchByUserId(listOptions.userId, listOptions.limit,
                    listOptions.offset)
        } else {
            throw new IllegalArgumentException('Nosupported search operation.')
        }
    }
}
