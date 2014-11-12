package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFAAttemptId
import com.junbo.common.model.ResourceMetaBase
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.UserPropertyAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.UserTFAAttemptFilter
import com.junbo.identity.core.service.validator.UserTFAAttemptValidator
import com.junbo.identity.service.UserTFAAttemptService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserTFAAttempt
import com.junbo.identity.spec.v1.option.list.UserTFAAttemptListOptions
import com.junbo.identity.spec.v1.option.model.UserTFAAttemptGetOptions
import com.junbo.identity.spec.v1.resource.UserTFAAttemptResource
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
class UserTFAAttemptResourceImpl implements UserTFAAttemptResource {
    @Autowired
    private UserTFAAttemptService userTFAAttemptService

    @Autowired
    private UserTFAAttemptFilter userTFAAttemptFilter

    @Autowired
    private UserTFAAttemptValidator userTFAAttemptValidator

    @Autowired
    private PlatformTransactionManager transactionManager

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserPropertyAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    Promise<UserTFAAttempt> create(UserId userId, UserTFAAttempt userTeleAttempt) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTeleAttempt == null) {
            throw new IllegalArgumentException('userTeleAttempt is null')
        }

        userTeleAttempt = userTFAAttemptFilter.filterForCreate(userTeleAttempt)

        return userTFAAttemptValidator.validateForCreate(userId, userTeleAttempt).recover{ Throwable e ->
            userTeleAttempt.succeeded = false
            userTeleAttempt.verifyCode = null
            return createInNewTran(userTeleAttempt).then {
                throw e
            }
        }.then {

            return createInNewTran(userTeleAttempt).then { UserTFAAttempt attempt ->

                if (attempt.succeeded) {
                    Created201Marker.mark(attempt.getId())

                    attempt.verifyCode = null
                    attempt = userTFAAttemptFilter.filterForGet(attempt, null)
                    attempt = RightsScope.filterForAdminInfo(attempt as ResourceMetaBase) as UserTFAAttempt
                    return Promise.pure(attempt)
                }

                throw AppErrors.INSTANCE.userTFACodeIncorrect().exception()
            }
        }
    }

    @Override
    Promise<UserTFAAttempt> get(UserId userId, UserTFAAttemptId userTFAAttemptId, UserTFAAttemptGetOptions getOptions) {
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

            return userTFAAttemptValidator.validateForGet(userId, userTFAAttemptId).then { UserTFAAttempt attempt ->
                attempt = userTFAAttemptFilter.filterForGet(attempt, getOptions.properties?.split(',') as List<String>)

                return Promise.pure(attempt)
            }
        }
    }

    @Override
    Promise<Results<UserTFAAttempt>> list(UserId userId, UserTFAAttemptListOptions listOptions) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('read')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return userTFAAttemptValidator.validateForSearch(userId, listOptions).then {
                return search(listOptions).then { Results<UserTFAAttempt> attemptList ->
                    def result = new Results<UserTFAAttempt>(items: [])
                    result.total = attemptList.total
                    attemptList.items.each { UserTFAAttempt attempt ->
                        attempt = userTFAAttemptFilter.filterForGet(attempt,
                                listOptions.properties?.split(',') as List<String>)
                        attempt.verifyCode = null
                        result.items.add(attempt)
                    }

                    return Promise.pure(result)
                }
            }
        }
    }

    Promise<UserTFAAttempt> createInNewTran(UserTFAAttempt attempt) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return template.execute(new TransactionCallback<Promise<UserTFAAttempt>>() {
            Promise<UserTFAAttempt> doInTransaction(TransactionStatus txnStatus) {
                return userTFAAttemptService.create(attempt)
            }
        }
        )
    }

    private Promise<Results<UserTFAAttempt>> search(UserTFAAttemptListOptions listOptions) {
        if (listOptions.userId != null && listOptions.userTFAId != null) {
            return userTFAAttemptService.searchByUserIdAndUserTFAId(listOptions.userId, listOptions.userTFAId,
                    listOptions.limit, listOptions.offset)
        } else if (listOptions.userId != null) {
            return userTFAAttemptService.searchByUserId(listOptions.userId, listOptions.limit, listOptions.offset)
        } else {
            throw AppCommonErrors.INSTANCE.invalidOperation('Nosupported search operation').exception()
        }
    }
}
