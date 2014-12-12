package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId
import com.junbo.common.model.ResourceMetaBase
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.auth.UserPropertyAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.UserSecurityQuestionAttemptFilter
import com.junbo.identity.core.service.validator.UserSecurityQuestionAttemptValidator
import com.junbo.identity.service.UserSecurityQuestionAttemptService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserSecurityQuestionVerifyAttempt
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionAttemptListOptions
import com.junbo.identity.spec.v1.option.model.UserSecurityQuestionAttemptGetOptions
import com.junbo.identity.spec.v1.resource.UserSecurityQuestionVerifyAttemptResource
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
 * Created by liangfu on 4/11/14.
 */
@Transactional
@CompileStatic
class UserSecurityQuestionVerifyAttemptResourceImpl implements UserSecurityQuestionVerifyAttemptResource {

    @Autowired
    private UserSecurityQuestionAttemptService userSecurityQuestionAttemptService

    @Autowired
    private UserSecurityQuestionAttemptFilter userSecurityQuestionAttemptFilter

    @Autowired
    private UserSecurityQuestionAttemptValidator userSecurityQuestionAttemptValidator

    @Autowired
    private PlatformTransactionManager transactionManager

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserPropertyAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    Promise<UserSecurityQuestionVerifyAttempt> create(
            UserId userId, UserSecurityQuestionVerifyAttempt userSecurityQuestionAttempt) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userSecurityQuestionAttempt == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        userSecurityQuestionAttempt = userSecurityQuestionAttemptFilter.filterForCreate(userSecurityQuestionAttempt)

        return userSecurityQuestionAttemptValidator.validateForCreate(userId, userSecurityQuestionAttempt).then {

            return createInNewTran(userSecurityQuestionAttempt).recover { Throwable e ->
                userSecurityQuestionAttempt.succeeded = false
                return createInNewTran(userSecurityQuestionAttempt).then {
                    throw e
                }
            }.then { UserSecurityQuestionVerifyAttempt attempt ->

                if (attempt.succeeded) {
                    Created201Marker.mark(attempt.getId())

                    attempt = userSecurityQuestionAttemptFilter.filterForGet(attempt, null)
                    attempt = RightsScope.filterForAdminInfo(attempt as ResourceMetaBase) as UserSecurityQuestionVerifyAttempt
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

        if (userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('read')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            listOptions.setUserId(userId)
            return userSecurityQuestionAttemptValidator.validateForSearch(listOptions).then {
                return search(listOptions).then { Results<UserSecurityQuestionVerifyAttempt> userSecurityQuestionAttemptList ->
                    def result = new Results<UserSecurityQuestionVerifyAttempt>(items: [])
                    result.total = userSecurityQuestionAttemptList.total

                    userSecurityQuestionAttemptList.items.each { UserSecurityQuestionVerifyAttempt attempt ->
                            attempt = userSecurityQuestionAttemptFilter.filterForGet(attempt,
                                    listOptions.properties?.split(',') as List<String>)
                        if (attempt != null) {
                            result.items.add(attempt)
                        }
                    }

                    return Promise.pure(result)
                }
            }
        }
    }

    @Override
    Promise<UserSecurityQuestionVerifyAttempt> get(UserId userId, UserSecurityQuestionVerifyAttemptId id,
                                           UserSecurityQuestionAttemptGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        if (userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId').exception()
        }

        if (id == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('UserSecurityQuestionVerifyAttemptId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('read')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return userSecurityQuestionAttemptValidator.validateForGet(userId, id).
                    then { UserSecurityQuestionVerifyAttempt attempt ->
                attempt = userSecurityQuestionAttemptFilter.filterForGet(attempt,
                        getOptions.properties?.split(',') as List<String>)

                return Promise.pure(attempt)
            }
        }
    }

    Promise<UserSecurityQuestionVerifyAttempt> createInNewTran(UserSecurityQuestionVerifyAttempt userLoginAttempt) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return template.execute(new TransactionCallback<Promise<UserSecurityQuestionVerifyAttempt>>() {
                Promise<UserSecurityQuestionVerifyAttempt> doInTransaction(TransactionStatus txnStatus) {
                    return userSecurityQuestionAttemptService.create(userLoginAttempt)
                }
            }
        )
    }

    private Promise<Results<UserSecurityQuestionVerifyAttempt>> search(UserSecurityQuestionAttemptListOptions listOptions) {
        if (listOptions.userId != null && listOptions.userSecurityQuestionId != null) {
            return userSecurityQuestionAttemptService.searchByUserIdAndSecurityQuestionId(listOptions.userId,
                    listOptions.userSecurityQuestionId, listOptions.limit, listOptions.offset)
        } else if (listOptions.userId != null) {
            return userSecurityQuestionAttemptService.searchByUserId(listOptions.userId, listOptions.limit,
                    listOptions.offset)
        } else {
            throw AppCommonErrors.INSTANCE.invalidOperation('Unsupported search operation').exception()
        }
    }
}
