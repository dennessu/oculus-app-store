package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFAId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.email.spec.resource.EmailResource
import com.junbo.email.spec.resource.EmailTemplateResource
import com.junbo.identity.auth.UserPropertyAuthorizeCallbackFactory
import com.junbo.identity.core.service.filter.UserTFAFilter
import com.junbo.identity.core.service.validator.UserTFAValidator
import com.junbo.identity.data.identifiable.TFASearchType
import com.junbo.identity.data.identifiable.TFAVerifyType
import com.junbo.identity.data.telesign.TeleSign
import com.junbo.identity.service.UserTFAMailService
import com.junbo.identity.service.UserTFAPhoneService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.identity.spec.v1.option.list.UserTFAListOptions
import com.junbo.identity.spec.v1.option.model.UserTFAGetOptions
import com.junbo.identity.spec.v1.resource.UserTFAResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

import javax.ws.rs.core.Response

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
@Transactional
class UserTFAResourceImpl implements UserTFAResource {

    private static final String EMAIL_SOURCE = 'Oculus'
    private static final String EMAIL_ACTION = 'TFAVerificationCode_V1'

    @Autowired
    private UserTFAPhoneService userTFAPhoneService

    @Autowired
    private UserTFAMailService userTFAMailService

    @Autowired
    private UserTFAFilter userTFAFilter

    @Autowired
    private UserTFAValidator userTFAValidator

    @Autowired
    private TeleSign teleSign

    @Autowired
    @Qualifier('identityEmailResource')
    private EmailResource emailResource

    @Autowired
    @Qualifier('identityEmailTemplateResource')
    private EmailTemplateResource emailTemplateResource

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserPropertyAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    Promise<UserTFA> create(UserId userId, UserTFA userTFA) {
        if (userTFA == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userTFA.userId != null && userTFA.userId != userId) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('userId', userTFA.userId, userId).exception()
        }

        userTFA.verifyCode = null
        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            userTFA = userTFAFilter.filterForCreate(userTFA)

            return userTFAValidator.validateForCreate(userId, userTFA).then {
                return sendCode(userTFA).then {
                    return userTFAPhoneService.create(userTFA).then { UserTFA newUserTeleCode ->
                        Created201Marker.mark(newUserTeleCode.getId())

                        newUserTeleCode = userTFAFilter.filterForGet(newUserTeleCode, null)
                        if (!StringUtils.isEmpty(newUserTeleCode.verifyCode) && !AuthorizeContext.debugEnabled) {
                            newUserTeleCode.verifyCode = null
                        }
                        return Promise.pure(newUserTeleCode)
                    }
                }
            }
        }
    }

    @Override
    Promise<UserTFA> get(UserId userId, UserTFAId userTFAId, UserTFAGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userTFAId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userTFAId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('read')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return userTFAValidator.validateForGet(userId, userTFAId).then { UserTFA newUserTeleCode ->
                newUserTeleCode = userTFAFilter.filterForGet(newUserTeleCode,
                        getOptions.properties?.split(',') as List<String>)

                if (!StringUtils.isEmpty(newUserTeleCode.verifyCode) && !AuthorizeContext.debugEnabled) {
                    newUserTeleCode.verifyCode = null
                }
                return Promise.pure(newUserTeleCode)
            }
        }
    }

    @Override
    Promise<UserTFA> put(UserId userId, UserTFAId userTFAId, UserTFA userTFA) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userTFAId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userTFAId').exception()
        }

        if (userTFA == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        userTFA.verifyCode = null

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('update')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return userTFAPhoneService.get(userTFAId).then { UserTFA oldUserTeleCode ->
                if (oldUserTeleCode == null) {
                    throw AppErrors.INSTANCE.userTFANotFound(userTFAId).exception()
                }

                userTFA = userTFAFilter.filterForPut(userTFA, oldUserTeleCode)

                return userTFAValidator.validateForUpdate(userId, userTFAId, userTFA, oldUserTeleCode).then {
                    return userTFAPhoneService.update(userTFA, oldUserTeleCode).then { UserTFA newUserTeleCode ->
                        newUserTeleCode = userTFAFilter.filterForGet(newUserTeleCode, null)
                        if (!StringUtils.isEmpty(newUserTeleCode.verifyCode) && !AuthorizeContext.debugEnabled) {
                            newUserTeleCode.verifyCode = null
                        }
                        return Promise.pure(newUserTeleCode)
                    }
                }
            }
        }
    }

    @Override
    Promise<Response> delete(UserId userId, UserTFAId userTFAId) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userTFAId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userTFAId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('delete')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return userTFAValidator.validateForGet(userId, userTFAId).then {
                return userTFAPhoneService.delete(userTFAId).then {
                    return Promise.pure(Response.status(204).build())
                }
            }
        }
    }

    @Override
    Promise<Results<UserTFA>> list(UserId userId, UserTFAListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        def callback = authorizeCallbackFactory.create(userId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            def result = new Results<UserTFA>(items: [])
            if (!AuthorizeContext.hasRights('read')) {
                return Promise.pure(result)
            }

            listOptions.setUserId(userId)

            return userTFAValidator.validateForSearch(listOptions).then {
                return search(listOptions).then { List<UserTFA> userTeleCodeList ->
                    userTeleCodeList.each { UserTFA newUserTeleCode ->
                        if (newUserTeleCode != null) {
                            newUserTeleCode = userTFAFilter.filterForGet(newUserTeleCode,
                                    listOptions.properties?.split(',') as List<String>)

                            if (!StringUtils.isEmpty(newUserTeleCode.verifyCode) && !AuthorizeContext.debugEnabled) {
                                newUserTeleCode.verifyCode = null
                            }

                            result.items.add(newUserTeleCode)
                        }
                    }

                    return Promise.pure(result)
                }
            }
        }
    }

    private Promise<List<UserTFA>> search(UserTFAListOptions listOptions) {
        if (listOptions.userId != null && listOptions.personalInfo != null) {
            if (listOptions.type == TFASearchType.PHONE.toString()) {
                return userTFAPhoneService.searchTFACodeByUserIdAndPersonalInfoId(listOptions.userId, listOptions.personalInfo,
                    listOptions.limit, listOptions.offset)
            } else {
                return userTFAMailService.searchTFACodeByUserIdAndPersonalInfoId(listOptions.userId, listOptions.personalInfo,
                    listOptions.limit, listOptions.offset)
            }
        } else {
            throw AppCommonErrors.INSTANCE.invalidOperation('Unsupported search operation').exception()
        }
    }

    private Promise<Void> sendCode(UserTFA userTFA) {
        if (userTFA.verifyType == TFAVerifyType.EMAIL.toString()) {
            QueryParam queryParam = new QueryParam(
                    source: EMAIL_SOURCE,
                    action: EMAIL_ACTION,
                    locale: userTFA.sentLocale == null ? 'en_US' : userTFA.sentLocale.value.replace('-', '_')
            )

            return emailTemplateResource.getEmailTemplates(queryParam).then { Results<EmailTemplate> results ->
                if (results.items.isEmpty()) {
                    throw AppCommonErrors.INSTANCE.internalServerError(new Exception(EMAIL_ACTION + ' template not found')).exception()
                }
                EmailTemplate template = results.items.get(0)

                Email emailToSend = new Email(
                        userId: userTFA.userId,
                        templateId: template.getId(),
                        recipients: [userTFA.email].asList(),
                        replacements: [
                                'name': userTFA.username,
                                'code': userTFA.verifyCode
                        ]
                )

                return emailResource.postEmail(emailToSend).then { Email emailSent ->
                    if (emailSent == null) {
                        throw AppCommonErrors.INSTANCE.internalServerError(new Exception('Failed to send mail')).exception()
                    }

                    return Promise.pure(null)
                }
            }
        } else {
            return teleSign.verifyCode(userTFA).then {
                return Promise.pure(null)
            }
        }
    }
}
