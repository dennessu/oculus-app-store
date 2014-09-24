package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserCredentialVerifyAttemptId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.configuration.ConfigServiceManager
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.email.spec.resource.EmailResource
import com.junbo.email.spec.resource.EmailTemplateResource
import com.junbo.identity.common.util.Constants
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.credential.CredentialHash
import com.junbo.identity.core.service.credential.CredentialHashFactory
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.validator.UserCredentialVerifyAttemptValidator
import com.junbo.identity.data.identifiable.CredentialType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.*
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.transaction.AsyncTransactionTemplate
import groovy.transform.CompileStatic
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallback
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

import java.util.Locale
import java.util.regex.Pattern

/**
 * Check userValid (non-anonymous and active user.)
 * Check IP patterns
 * Check userAgent minimum and maximum length
 * Check maxRetry number
 * Created by liangfu on 3/28/14.
 */
@CompileStatic
@SuppressWarnings('UnnecessaryGetter')
class UserCredentialVerifyAttemptValidatorImpl implements UserCredentialVerifyAttemptValidator {
    private static final String EMAIL_SOURCE = 'Oculus'
    private static final String EMAIL_ACTION = 'UserLockout_V1'
    private static final String MAIL_IDENTIFIER = "@"

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCredentialVerifyAttemptValidatorImpl)

    private UserCredentialVerifyAttemptRepository userLoginAttemptRepository
    private UserRepository userRepository
    private UserPasswordRepository userPasswordRepository
    private UserPinRepository userPinRepository
    private UserPersonalInfoRepository userPersonalInfoRepository

    private List<Pattern> allowedIpAddressPatterns
    private Integer userAgentMinLength
    private Integer userAgentMaxLength
    // This is to check good user can't fail to login in some time
    private Integer maxRetryCount
    private Integer retryInterval

    // This is to check maxSameUserAttemptCount login attempts for the same user within attemptRetryCount time frame
    private Integer maxSameUserAttemptCount
    private Integer sameUserAttemptRetryInterval

    // This is to check More than maxSameIPRetryCount login attempts from the same IP address for different user account within sameIPRetryInterval timeframe
    private Integer maxSameIPRetryCount
    private List<String> ip4WhiteList
    private Integer sameIPRetryInterval

    // Any data that will use this data should be data issue, we may need to fix this.
    private Integer maximumFetchSize

    private NormalizeService normalizeService
    private CredentialHashFactory credentialHashFactory

    private EmailResource emailResource
    private EmailTemplateResource emailTemplateResource
    private PlatformTransactionManager transactionManager

    @Override
    Promise<UserCredentialVerifyAttempt> validateForGet(UserCredentialVerifyAttemptId userLoginAttemptId) {
        if (userLoginAttemptId == null) {
            throw new IllegalArgumentException('userLoginAttemptId is null')
        }

        return userLoginAttemptRepository.get(userLoginAttemptId).then { UserCredentialVerifyAttempt userLoginAttempt ->
            if (userLoginAttempt == null) {
                throw AppErrors.INSTANCE.userLoginAttemptNotFound(userLoginAttemptId).exception()
            }

            return userRepository.get(userLoginAttempt.userId).then { User user ->
                if (user == null) {
                    throw AppErrors.INSTANCE.userNotFound(userLoginAttempt.userId).exception()
                }

                if (user.isAnonymous) {
                    throw AppErrors.INSTANCE.userInInvalidStatus(userLoginAttempt.userId).exception()
                }

                if (user.status != UserStatus.ACTIVE.toString()) {
                    throw AppErrors.INSTANCE.userInInvalidStatus(userLoginAttempt.userId).exception()
                }

                return Promise.pure(userLoginAttempt)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserCredentialAttemptListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserCredentialVerifyAttempt userLoginAttempt) {
        if (userLoginAttempt == null) {
            throw new IllegalArgumentException('userLoginAttempt is null')
        }
        checkBasicUserLoginAttemptInfo(userLoginAttempt)

        if (userLoginAttempt.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }

        if (userLoginAttempt.succeeded != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('succeeded').exception()
        }

        return checkMaximumSameIPAttemptCount(userLoginAttempt).then {
            return findUser(userLoginAttempt).then { User user ->
                if (user == null) {
                    throw AppErrors.INSTANCE.userNotFoundByName(userLoginAttempt.username).exception()
                }

                if (user.status != UserStatus.ACTIVE.toString()) {
                    throw AppErrors.INSTANCE.userInInvalidStatusByName(userLoginAttempt.username).exception()
                }

                if (user.isAnonymous) {
                    throw AppErrors.INSTANCE.userInInvalidStatusByName(userLoginAttempt.username).exception()
                }

                userLoginAttempt.setUserId((UserId)user.id)

                if (userLoginAttempt.type == CredentialType.PASSWORD.toString()) {
                    return userPasswordRepository.searchByUserIdAndActiveStatus((UserId)user.id, true, maximumFetchSize,
                            0).then { List<UserPassword> userPasswordList ->
                        if (CollectionUtils.isEmpty(userPasswordList) || userPasswordList.size() > 1) {
                            throw AppErrors.INSTANCE.userPasswordIncorrect().exception()
                        }

                        List<CredentialHash> credentialHashList = credentialHashFactory.getAllCredentialHash()
                        CredentialHash matched = credentialHashList.find { CredentialHash hash ->
                            return hash.matches(userLoginAttempt.value, userPasswordList.get(0).passwordHash)
                        }
                        userLoginAttempt.setSucceeded(matched != null)
                        return checkMaximumRetryCount(user, userLoginAttempt).then {
                            return checkMaximumSameUserAttemptCount(user, userLoginAttempt)
                        }
                    }
                }
                else if (userLoginAttempt.type == CredentialType.PIN.toString()) {
                    return userPinRepository.searchByUserIdAndActiveStatus((UserId)user.id, true, maximumFetchSize,
                            0).then { List<UserPin> userPinList ->
                        if (CollectionUtils.isEmpty(userPinList) || userPinList.size() > 1) {
                            throw AppErrors.INSTANCE.userPinIncorrect().exception()
                        }

                        List<CredentialHash> credentialHashList = credentialHashFactory.getAllCredentialHash()
                        CredentialHash matched = credentialHashList.find { CredentialHash hash ->
                            return hash.matches(userLoginAttempt.value, userPinList.get(0).pinHash)
                        }

                        userLoginAttempt.setSucceeded(matched != null)
                        return checkMaximumRetryCount(user, userLoginAttempt).then {
                            return checkMaximumSameUserAttemptCount(user, userLoginAttempt)
                        }
                    }
                } else if (userLoginAttempt.type == CredentialType.CHECK_NAME.toString()) {
                    userLoginAttempt.setSucceeded(true)
                    return Promise.pure()
                } else {
                    throw AppCommonErrors.INSTANCE.parameterInvalid("credentialType").exception()
                }
            }
        }
    }

    private Promise<User> findUser(UserCredentialVerifyAttempt userLoginAttempt) {
        if (userLoginAttempt.userId != null) {
            return userRepository.get(userLoginAttempt.userId)
        }

        if (isEmail(userLoginAttempt.username)) {
            return userPersonalInfoRepository.searchByEmail(userLoginAttempt.username.toLowerCase(Locale.ENGLISH), null, maximumFetchSize,
                    0).then { List<UserPersonalInfo> personalInfos ->
                    if (CollectionUtils.isEmpty(personalInfos)) {
                        throw AppErrors.INSTANCE.userNotFoundByName(userLoginAttempt.username).exception()
                    }

                    return getDefaultEmailUser(personalInfos).then { User user ->
                        if (user == null) {
                            throw AppErrors.INSTANCE.userNotFoundByName(userLoginAttempt.username).exception()
                        }

                        if (user.isAnonymous || user.status != UserStatus.ACTIVE.toString()) {
                            throw AppCommonErrors.INSTANCE.fieldInvalid('value', 'User in invalid status.').
                                    exception()
                        }

                        if (CollectionUtils.isEmpty(user.emails)) {
                            throw AppErrors.INSTANCE.userNotFoundByName(userLoginAttempt.username).exception()
                        }

                        return Promise.pure(user)
                    }
                }
        } else {
            return userPersonalInfoRepository.searchByCanonicalUsername(normalizeService.normalize(userLoginAttempt.username),
                    maximumFetchSize, 0).then { List<UserPersonalInfo> userPersonalInfoList ->
                if (CollectionUtils.isEmpty(userPersonalInfoList)) {
                    return Promise.pure(null)
                }

                User user = null;
                return Promise.each(userPersonalInfoList.iterator()) { UserPersonalInfo userPersonalInfo ->
                    return userRepository.get(userPersonalInfo.userId).then { User existing ->
                        if (existing.username == userPersonalInfo.getId() && existing.status != UserStatus.DELETED.toString()) {
                            user = existing
                            return Promise.pure(Promise.BREAK)
                        }

                        return Promise.pure(null)
                    }
                }.then {
                    return Promise.pure(user)
                }
            }
        }
    }

    Promise<User> getDefaultEmailUser(List<UserPersonalInfo> userPersonalInfoList) {
        User user = null
        return Promise.each(userPersonalInfoList){ UserPersonalInfo info ->
            return userRepository.get(info.userId).then { User existing ->
                if (existing == null || CollectionUtils.isEmpty(existing.emails) || existing.status == UserStatus.DELETED.toString()) {
                    return Promise.pure(null)
                }

                UserPersonalInfoLink existingLink = existing.emails.find { UserPersonalInfoLink link ->
                    return link.isDefault && link.value == info.id
                }

                if (existingLink != null) {
                    user = existing
                    return Promise.pure(Promise.BREAK)
                }

                return Promise.pure(null)
            }
        }.then {
            return Promise.pure(user)
        }
    }

    private Promise<Void> checkMaximumRetryCount(User user, UserCredentialVerifyAttempt userLoginAttempt) {

        return getActiveCredentialCreatedTime(user, userLoginAttempt).then { Date passwordActiveTime ->
            Long timeInterval = passwordActiveTime.getTime()

            return userLoginAttemptRepository.searchByUserIdAndCredentialTypeAndInterval(user.getId(), userLoginAttempt.type, timeInterval, maxRetryCount + 1, 0).then {
                    List<UserCredentialVerifyAttempt> attemptListTemp ->
                if (CollectionUtils.isEmpty(attemptListTemp) || attemptListTemp.size() < maxRetryCount) {
                    return Promise.pure(null)
                }

                List<UserCredentialVerifyAttempt> attemptList = new ArrayList<>()
                for (int index = 0; index < maxRetryCount; index ++) {
                    attemptList.add(attemptListTemp.get(index))
                }

                UserCredentialVerifyAttempt successAttempt = attemptList.find { UserCredentialVerifyAttempt attempt ->
                    return attempt.succeeded
                }
                if (successAttempt != null) {
                    return Promise.pure(null)
                }

                return sendMaximumRetryReachedNotification(user, userLoginAttempt, attemptListTemp).recover { Throwable throwable ->
                    LOGGER.error("Error sending Maximum retry reachable Notification")
                    return Promise.pure(null)
                }.then {
                    throw AppErrors.INSTANCE.maximumLoginAttempt().exception()
                }
            }
        }
    }

    private Promise<Void> sendMaximumRetryReachedNotification(User user, UserCredentialVerifyAttempt userLoginAttempt, List<UserCredentialVerifyAttempt> attemptList) {
        if (attemptList.size() < maxRetryCount + 1) {
            return Promise.pure(null)
        }

        UserCredentialVerifyAttempt userCredentialVerifyAttempt = attemptList.get(maxRetryCount);
        // One day can send only one user lock mail
        if (!userCredentialVerifyAttempt.succeeded && userCredentialVerifyAttempt.createdTime.after(DateUtils.addDays(new Date(), -1))) {
            return Promise.pure(null)
        }

        // todo:    can remove this later due to mailenable is set to
        if (!Boolean.parseBoolean(ConfigServiceManager.instance().getConfigValue("identity.conf.mailEnable"))) {
            return Promise.pure(null)
        }

        if (JunboHttpContext.getRequestHeaders() == null
                || !CollectionUtils.isEmpty(JunboHttpContext.getRequestHeaders().get(Constants.HEADER_DISABLE_EMAIL))) {
            return Promise.pure(null)
        }

        QueryParam queryParam = new QueryParam(
                source: EMAIL_SOURCE,
                action: EMAIL_ACTION,
                locale: 'en_US'
        )

        return emailTemplateResource.getEmailTemplates(queryParam).then { Results<EmailTemplate> results ->
            if (results.items.isEmpty()) {
                throw AppCommonErrors.INSTANCE.internalServerError(new Exception(EMAIL_ACTION + ' with locale: en_US template not found')).exception()
            }
            EmailTemplate template = results.items.get(0)

            return getUserName(user, userLoginAttempt).then { String userLoginName ->
                return getUserEmail(user, userLoginAttempt).then { String userMail ->
                    if (StringUtils.isEmpty(userMail)) {
                        throw AppErrors.INSTANCE.userInInvalidStatus(user.getId()).exception()
                    }

                    Email emailToSend = new Email(
                            userId: user.getId(),
                            templateId: template.getId(),
                            recipients: [userMail].asList(),
                            replacements: [
                                    'name': userLoginName,
                                    'link': ConfigServiceManager.instance().getConfigValue('identity.conf.oculusHelpLink')
                            ]
                    )

                    return createInNewTran(emailToSend).then { Email emailSent ->
                        if (emailSent == null) {
                            throw AppCommonErrors.INSTANCE.internalServerError(new Exception('Failed to send mail')).exception()
                        }

                        return Promise.pure(null)
                    }
                }
            }
        }
    }

    private Promise<String> getUserName(User user, UserCredentialVerifyAttempt userLoginAttempt) {
        if (user.name == null) {
            return Promise.pure('')
        } else {
            return userPersonalInfoRepository.get(user.name).then { UserPersonalInfo userPersonalInfo ->
                if (userPersonalInfo == null) {
                    return Promise.pure('')
                }

                UserName userName = (UserName)JsonHelper.jsonNodeToObj(userPersonalInfo.value, UserName)
                String firstName = StringUtils.isEmpty(userName.givenName) ? "" : userName.givenName
                String lastName = StringUtils.isEmpty(userName.familyName) ? "" : userName.familyName
                return Promise.pure(firstName + ' ' + lastName)
            }
        }
    }

    private Promise<String> getUserEmail(User user, UserCredentialVerifyAttempt userLoginAttempt) {
        if (isEmail(userLoginAttempt.username)) {
            return Promise.pure(userLoginAttempt.username)
        } else {
            if (CollectionUtils.isEmpty(user.emails)) {
                LOGGER.warn('user emails is empty')
                return Promise.pure(null)
            }
            UserPersonalInfoLink link = user.emails.find { UserPersonalInfoLink infoLink ->
                return infoLink.isDefault
            }

            link = link == null ? user.emails.get(0) : link;
            return userPersonalInfoRepository.get(link.value).then { UserPersonalInfo userPersonalInfo ->
                com.junbo.identity.spec.v1.model.Email email = (com.junbo.identity.spec.v1.model.Email)JsonHelper.jsonNodeToObj(userPersonalInfo.value,
                        com.junbo.identity.spec.v1.model.Email)
                return Promise.pure(email.info)
            }
        }
    }

    private Promise<Date> getActiveCredentialCreatedTime(User user, UserCredentialVerifyAttempt userLoginAttempt) {
        if (userLoginAttempt.type == CredentialType.PASSWORD.toString()) {
            return userPasswordRepository.searchByUserIdAndActiveStatus(user.getId(), true, 1, 0).then { List<UserPassword> userPasswordList ->
                if (CollectionUtils.isEmpty(userPasswordList)) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('username', 'No Active password exists').exception()
                }

                return Promise.pure(userPasswordList.get(0).createdTime)
            }
        } else {
            return userPinRepository.searchByUserIdAndActiveStatus(user.getId(), true, 1, 0).then { List<UserPin> userPinList ->
                if (CollectionUtils.isEmpty(userPinList)) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('username', 'No Active pin exists').exception()
                }

                return Promise.pure(userPinList.get(0).createdTime)
            }
        }
    }

    private Promise<Void> checkMaximumSameUserAttemptCount(User user, UserCredentialVerifyAttempt userLoginAttempt) {
        Long timeInterval = System.currentTimeMillis() - sameUserAttemptRetryInterval * 1000
        return userLoginAttemptRepository.searchByUserIdAndCredentialTypeAndInterval(user.getId(), userLoginAttempt.type, timeInterval,
                maxSameUserAttemptCount + 1, 0).then { List<UserCredentialVerifyAttempt> attemptList ->
            if (CollectionUtils.isEmpty(attemptList) || attemptList.size() <= maxSameUserAttemptCount) {
                return Promise.pure(null)
            }
            throw AppErrors.INSTANCE.maximumLoginAttempt().exception()
        }
    }

    private Promise<Void> checkMaximumSameIPAttemptCount(UserCredentialVerifyAttempt userLoginAttempt) {
        if (StringUtils.isEmpty(userLoginAttempt.ipAddress) || isInIP4WhiteList(userLoginAttempt.ipAddress)) {
            return Promise.pure(null)
        }

        Long fromTimeStamp = System.currentTimeMillis() - sameIPRetryInterval * 1000

        return userLoginAttemptRepository.searchByIPAddressAndCredentialTypeAndInterval(userLoginAttempt.ipAddress, userLoginAttempt.type, fromTimeStamp,
                maxSameIPRetryCount + 1, 0).then { List<UserCredentialVerifyAttempt> attemptList ->
            if (CollectionUtils.isEmpty(attemptList) || attemptList.size() <= maxSameIPRetryCount) {
                return Promise.pure(null)
            }

            throw AppErrors.INSTANCE.maximumLoginAttempt().exception()
        }
    }

    private boolean isInIP4WhiteList(String ipAddress) {
        String[] ipAddressArray = ipAddress.split("\\.")
        if (ipAddressArray.length != 4) {
            return false
        }

        boolean flag = true
        ip4WhiteList.each { String whiteList ->
            if (!flag) {
                return
            }
            String[] ipRange = whiteList.split("\\.")
            for (int index = 0; index < 4; index ++ ) {
                String range = ipRange[index]
                if (range != '*' && range != ipAddressArray[index]) {
                    flag = false
                    break
                }
            }
        }

        return flag
    }

    private void checkBasicUserLoginAttemptInfo(UserCredentialVerifyAttempt userLoginAttempt) {
        if (userLoginAttempt == null) {
            throw new IllegalArgumentException('userLoginAttempt is null')
        }

        if (userLoginAttempt.ipAddress != null) {
            if (!allowedIpAddressPatterns.any {
                        Pattern pattern -> pattern.matcher(userLoginAttempt.ipAddress).matches()
                    }) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('ipAddress').exception()
            }
        }

        if (userLoginAttempt.type == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('type').exception()
        }

        List<String> allowedTypes = CredentialType.values().collect { CredentialType credentialType ->
            credentialType.toString()
        }

        if (!(userLoginAttempt.type in allowedTypes)) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('type', allowedTypes.join(',')).exception()
        }

        if (userLoginAttempt.userAgent != null) {
            if (userLoginAttempt.userAgent.length() > userAgentMaxLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('userAgent', userAgentMaxLength).exception()
            }

            if (userLoginAttempt.userAgent.length() < userAgentMinLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('userAgent', userAgentMinLength).exception()
            }
        }

        if (StringUtils.isEmpty(userLoginAttempt.username) && userLoginAttempt.userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('username').exception()
        }

        if (!StringUtils.isEmpty(userLoginAttempt.username) && !isEmail(userLoginAttempt.username)) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('username', 'Only mail login is supported').exception()
        }

        if (userLoginAttempt.value == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('value').exception()
        }
    }

    Promise<UserCredentialVerifyAttempt> createInNewTran(Email emailToSend) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return template.execute(new TransactionCallback<Promise<UserCredentialVerifyAttempt>>() {
            Promise<UserCredentialVerifyAttempt> doInTransaction(TransactionStatus txnStatus) {
                return emailResource.postEmail(emailToSend)
            }
        })
    }

    static boolean isEmail(String value) {
        if (value.contains(MAIL_IDENTIFIER)) {
            return true
        }
        return false
    }

    @Required
    void setUserLoginAttemptRepository(UserCredentialVerifyAttemptRepository userLoginAttemptRepository) {
        this.userLoginAttemptRepository = userLoginAttemptRepository
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setAllowedIpAddressPatterns(List<String> allowedIpAddressPatterns) {
        this.allowedIpAddressPatterns = allowedIpAddressPatterns.collect {
            String pattern -> Pattern.compile(pattern)
        }
    }

    @Required
    void setUserAgentMinLength(Integer userAgentMinLength) {
        this.userAgentMinLength = userAgentMinLength
    }

    @Required
    void setUserAgentMaxLength(Integer userAgentMaxLength) {
        this.userAgentMaxLength = userAgentMaxLength
    }

    @Required
    void setUserPasswordRepository(UserPasswordRepository userPasswordRepository) {
        this.userPasswordRepository = userPasswordRepository
    }

    @Required
    void setUserPinRepository(UserPinRepository userPinRepository) {
        this.userPinRepository = userPinRepository
    }

    @Required
    void setNormalizeService(NormalizeService normalizeService) {
        this.normalizeService = normalizeService
    }

    @Required
    void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount
    }

    @Required
    void setRetryInterval(Integer retryInterval) {
        this.retryInterval = retryInterval
    }

    @Required
    void setMaxAttemptCount(Integer maxAttemptCount) {
        this.maxSameUserAttemptCount = maxAttemptCount
    }

    @Required
    void setAttemptRetryInterval(Integer attemptRetryInterval) {
        this.sameUserAttemptRetryInterval = attemptRetryInterval
    }

    @Required
    void setMaxSameIPRetryCount(Integer maxSameIPRetryCount) {
        this.maxSameIPRetryCount = maxSameIPRetryCount
    }

    @Required
    void setIp4WhiteList(String ip4WhiteList) {
        this.ip4WhiteList = ip4WhiteList.split(';') as List
        this.ip4WhiteList.each { String ip ->
            if (StringUtils.isEmpty(ip)) {
                throw new IllegalArgumentException('configuration ip4whiteList can\'t have empty ip')
            }
            String[] ipFieldList = ip.split('\\.')
            if (ipFieldList.length != 4) {
                throw new IllegalArgumentException('configuration ip4WhiteList must be xx.xx.xx.xx;xx.xx.xx.xx ' + ip + ipFieldList.length)
            }
        }
    }

    @Required
    void setSameIPRetryInterval(Integer sameIPRetryInterval) {
        this.sameIPRetryInterval = sameIPRetryInterval
    }

    @Required
    void setUserPersonalInfoRepository(UserPersonalInfoRepository userPersonalInfoRepository) {
        this.userPersonalInfoRepository = userPersonalInfoRepository
    }

    @Required
    void setCredentialHashFactory(CredentialHashFactory credentialHashFactory) {
        this.credentialHashFactory = credentialHashFactory
    }

    @Required
    void setEmailResource(EmailResource emailResource) {
        this.emailResource = emailResource
    }

    @Required
    void setEmailTemplateResource(EmailTemplateResource emailTemplateResource) {
        this.emailTemplateResource = emailTemplateResource
    }

    @Required
    void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager
    }

    @Required
    void setMaximumFetchSize(Integer maximumFetchSize) {
        this.maximumFetchSize = maximumFetchSize
    }
}
