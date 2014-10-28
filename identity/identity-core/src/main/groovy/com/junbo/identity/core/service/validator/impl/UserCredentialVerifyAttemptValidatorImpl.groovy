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
import com.junbo.identity.service.*
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
    private static final String MAIL_IDENTIFIER = "@"

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCredentialVerifyAttemptValidatorImpl)

    private UserCredentialVerifyAttemptService userCredentialVerifyAttemptService
    private UserService userService
    private UserPasswordService userPasswordService
    private UserPinService userPinService
    private UserPersonalInfoService userPersonalInfoService

    private List<Pattern> allowedIpAddressPatterns
    private Integer userAgentMinLength
    private Integer userAgentMaxLength
    // This is to check good user can't fail to login in some time
    private Integer maxRetryInterval                    // This is to control same user's allow retry count during interval
                                                        // The first parameter is the maxRetryCount, the second parameter is the retryInterval
                                                        // All of them are and operation, only when user's login history satisfy any configurations, it will be the same
    private Map<Integer, Integer> maxLockDownTime       // This is to control same user's lock out time
                                                        // The first parameter is the maxRetryCount, the second parameter is the lock down time
    private Map<Integer, String> maxLockDownMail        // This is to control same user's lock out mail
                                                        // The first parameter is the maxRetryCount, the second parametet is the lock down time email template

    // This is to check maxSameUserAttemptCount login attempts for the same user within attemptRetryCount time frame
    private Map<Integer, Integer> maxSameUserAttemptIntervalMap    // This is to control smae user's retry interval
                                                                    // The first parameter is the allowed number, the second parameter is the interval

    // This is to check More than maxSameIPRetryCount login attempts from the same IP address for different user account within sameIPRetryInterval timeframe
    private Map<Integer, Integer> maxSameIPIntervalMap  // This is to control same ip's retry interval
                                                        // The first parameter is the allowed number, the second parameter is the retryInterval
    private List<String> ip4WhiteList

    // Any data that will use this data should be data issue, we may need to fix this.
    private Integer maximumFetchSize

    private NormalizeService normalizeService
    private CredentialHashFactory credentialHashFactory

    private EmailResource emailResource
    private EmailTemplateResource emailTemplateResource
    private PlatformTransactionManager transactionManager
    private Boolean enableMailSend

    private Boolean maxRetryCountEnable
    private Boolean maxSameIPAttemptsEnable
    private Boolean maxSameUserAttemptsEnable

    @Override
    Promise<UserCredentialVerifyAttempt> validateForGet(UserCredentialVerifyAttemptId userLoginAttemptId) {
        if (userLoginAttemptId == null) {
            throw new IllegalArgumentException('userLoginAttemptId is null')
        }

        return userCredentialVerifyAttemptService.get(userLoginAttemptId).then { UserCredentialVerifyAttempt userLoginAttempt ->
            if (userLoginAttempt == null) {
                throw AppErrors.INSTANCE.userLoginAttemptNotFound(userLoginAttemptId).exception()
            }

            return userService.getNonDeletedUser(userLoginAttempt.userId).then { User user ->
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
                    return userPasswordService.searchByUserIdAndActiveStatus((UserId)user.id, true, maximumFetchSize,
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
                    return userPinService.searchByUserIdAndActiveStatus((UserId)user.id, true, maximumFetchSize,
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
            return userService.getNonDeletedUser(userLoginAttempt.userId)
        }

        if (isEmail(userLoginAttempt.username)) {
            return userPersonalInfoService.searchByEmail(userLoginAttempt.username.toLowerCase(Locale.ENGLISH), null, maximumFetchSize,
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
            return userPersonalInfoService.searchByCanonicalUsername(normalizeService.normalize(userLoginAttempt.username),
                    maximumFetchSize, 0).then { List<UserPersonalInfo> userPersonalInfoList ->
                if (CollectionUtils.isEmpty(userPersonalInfoList)) {
                    return Promise.pure(null)
                }

                User user = null;
                return Promise.each(userPersonalInfoList.iterator()) { UserPersonalInfo userPersonalInfo ->
                    return userService.getNonDeletedUser(userPersonalInfo.userId).then { User existing ->
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
            return userService.getNonDeletedUser(info.userId).then { User existing ->
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
        if (!maxRetryCountEnable) {
            return Promise.pure(null)
        }
        return getActiveCredentialCreatedTime(user, userLoginAttempt).then { Date passwordActiveTime ->
            Integer maxRetryCount = maxLockDownTime.keySet().max()
            Long maxRetryIntervalFromTime = System.currentTimeMillis() - maxRetryInterval * 1000L
            Long timeInterval = maxRetryIntervalFromTime > passwordActiveTime.getTime() ? maxRetryIntervalFromTime : passwordActiveTime.getTime()

            return userCredentialVerifyAttemptService.searchNonLockPeriodHistory(user.getId(), userLoginAttempt.type, timeInterval, maxRetryCount, 0).then { List<UserCredentialVerifyAttempt> attemptList ->
                // If no credential verify attempt found, return
                if (CollectionUtils.isEmpty(attemptList)) {
                    return Promise.pure(null)
                }
                List<UserCredentialVerifyAttempt> consecutiveFailureAttempts = new ArrayList<>()
                for (int i = 0; i < attemptList.size(); i++) {
                    if (attemptList.get(i).succeeded) {
                        break
                    }
                    consecutiveFailureAttempts.add(attemptList.get(i))
                }

                // Return the rule template
                int index = getRetryKeyIndex(consecutiveFailureAttempts.size())

                // Check whether this is in lock down period
                checkLockDownPeriod(consecutiveFailureAttempts, index, userLoginAttempt)

                // Check whether need to send email
                if (index < maxLockDownTime.keySet().size() && !userLoginAttempt.succeeded && consecutiveFailureAttempts.size() == maxLockDownTime.keySet().getAt(index) - 1) {
                    return sendMaximumRetryReachedNotification(user, userLoginAttempt, index).recover { Throwable ex ->
                        LOGGER.error("Error sending Maximum retry reachable Notification")
                        return Promise.pure(null)
                    }
                }

                // If not in lockdown period, will just return.
                // Else if it is in lockdown period, return and throw potential exception
                return Promise.pure(null)
            }
        }
    }

    private Integer getRetryKeyIndex(Integer value) {
        int size = maxLockDownTime.keySet().size()
        int startKey, endKey
        for(int index = 0; index <= size; index ++) {
            startKey = index == 0? 0: maxLockDownTime.keySet().getAt(index - 1)
            endKey = index == size? Integer.MAX_VALUE : maxLockDownTime.keySet().getAt(index) - 1

            if (value >= startKey && value <= endKey) {
                return index
            }
        }
    }

    private void checkLockDownPeriod(List<UserCredentialVerifyAttempt> attemptList, int index, UserCredentialVerifyAttempt userLoginAttempt) {
        if (index == maxLockDownTime.keySet().size()) {
            userLoginAttempt.isLockDownPeriodAttempt = true
            return
        }
        for (int k = 0; k < index; k++) {
            Integer retryCount = maxLockDownTime.keySet().getAt(k);
            Integer lockDownTime = maxLockDownTime.get(retryCount);
            if (attemptList.size() < retryCount) {
                userLoginAttempt.isLockDownPeriodAttempt = false
                continue
            }
            UserCredentialVerifyAttempt attempt = attemptList.get(attemptList.size() - retryCount)
            // in lock down time
            if (attempt.createdTime.after(DateUtils.addSeconds(new Date(), -lockDownTime))) {
                userLoginAttempt.isLockDownPeriodAttempt = true
                break
            } else {
                userLoginAttempt.isLockDownPeriodAttempt = false
            }
        }
    }

    private Promise<Void> sendMaximumRetryReachedNotification(User user, UserCredentialVerifyAttempt userLoginAttempt, int index) {
        if (!enableMailSend) {
            return Promise.pure(null)
        }

        String emailTemplate = this.maxLockDownMail.get(
                this.maxLockDownMail.size() <= index ? this.maxLockDownMail.keySet().getAt(this.maxLockDownMail.size() - 1) : this.maxLockDownMail.keySet().getAt(index))
        Integer lockDownTime = this.maxLockDownTime.get(
                this.maxLockDownTime.size() <= index ? this.maxLockDownTime.keySet().getAt(this.maxLockDownTime.size() - 1) : this.maxLockDownTime.keySet().getAt(index))

        if (JunboHttpContext.getRequestHeaders() == null
                || !CollectionUtils.isEmpty(JunboHttpContext.getRequestHeaders().get(Constants.HEADER_DISABLE_EMAIL))) {
            return Promise.pure(null)
        }

        QueryParam queryParam = new QueryParam(
                source: EMAIL_SOURCE,
                action: emailTemplate,
                locale: 'en_US'
        )

        return emailTemplateResource.getEmailTemplates(queryParam).then { Results<EmailTemplate> results ->
            if (results.items.isEmpty()) {
                throw AppCommonErrors.INSTANCE.internalServerError(new Exception(emailTemplate + ' with locale: en_US template not found')).exception()
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
                                    'link': ConfigServiceManager.instance().getConfigValue('identity.conf.oculusHelpLink'),
                                    'minute': (lockDownTime/60).toString()
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
            return userPersonalInfoService.get(user.name).then { UserPersonalInfo userPersonalInfo ->
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
            return userPersonalInfoService.get(link.value).then { UserPersonalInfo userPersonalInfo ->
                com.junbo.identity.spec.v1.model.Email email = (com.junbo.identity.spec.v1.model.Email)JsonHelper.jsonNodeToObj(userPersonalInfo.value,
                        com.junbo.identity.spec.v1.model.Email)
                return Promise.pure(email.info)
            }
        }
    }

    private Promise<Date> getActiveCredentialCreatedTime(User user, UserCredentialVerifyAttempt userLoginAttempt) {
        if (userLoginAttempt.type == CredentialType.PASSWORD.toString()) {
            return userPasswordService.searchByUserIdAndActiveStatus(user.getId(), true, 1, 0).then { List<UserPassword> userPasswordList ->
                if (CollectionUtils.isEmpty(userPasswordList)) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('username', 'No Active password exists').exception()
                }

                return Promise.pure(userPasswordList.get(0).createdTime)
            }
        } else {
            return userPinService.searchByUserIdAndActiveStatus(user.getId(), true, 1, 0).then { List<UserPin> userPinList ->
                if (CollectionUtils.isEmpty(userPinList)) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('username', 'No Active pin exists').exception()
                }

                return Promise.pure(userPinList.get(0).createdTime)
            }
        }
    }

    private Promise<Void> checkMaximumSameUserAttemptCount(User user, UserCredentialVerifyAttempt userLoginAttempt) {
        if (!maxSameUserAttemptsEnable) {
            return Promise.pure(null)
        }
        return Promise.each(this.maxSameUserAttemptIntervalMap.entrySet()) { Map.Entry<Integer, Integer> entry ->
            Integer maxSameUserAttemptCount = entry.key
            Integer sameUserAttemptRetryInterval = entry.value
            Long timeInterval = System.currentTimeMillis() - sameUserAttemptRetryInterval * 1000
            return userCredentialVerifyAttemptService.searchByUserIdAndCredentialTypeAndInterval(user.getId(), userLoginAttempt.type, timeInterval,
                    maxSameUserAttemptCount + 1, 0).then { List<UserCredentialVerifyAttempt> attemptList ->
                if (CollectionUtils.isEmpty(attemptList) || attemptList.size() <= maxSameUserAttemptCount) {
                    return Promise.pure(null)
                }
                throw AppErrors.INSTANCE.maximumLoginAttempt().exception()
            }
        }.then {
            return Promise.pure(null)
        }
    }

    private Promise<Void> checkMaximumSameIPAttemptCount(UserCredentialVerifyAttempt userLoginAttempt) {
        if (!maxSameIPAttemptsEnable) {
            return Promise.pure(null)
        }
        if (StringUtils.isEmpty(userLoginAttempt.ipAddress) || isInIP4WhiteList(userLoginAttempt.ipAddress)) {
            return Promise.pure(null)
        }

        return Promise.each(this.maxSameIPIntervalMap.entrySet()) { Map.Entry<Integer, Integer> entry ->
            Integer maxSameIPRetryCount = entry.getKey()
            Integer maxSameIPRetryInterval = entry.getValue()

            Long fromTimeStamp = System.currentTimeMillis() - maxSameIPRetryInterval * 1000

            return userCredentialVerifyAttemptService.searchByIPAddressAndCredentialTypeAndInterval(userLoginAttempt.ipAddress, userLoginAttempt.type, fromTimeStamp,
                    maxSameIPRetryCount + 1, 0).then { List<UserCredentialVerifyAttempt> attemptList ->
                if (CollectionUtils.isEmpty(attemptList) || attemptList.size() <= maxSameIPRetryCount) {
                    return Promise.pure(null)
                }

                throw AppErrors.INSTANCE.maximumLoginAttempt().exception()
            }

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

        if (userLoginAttempt.isLockDownPeriodAttempt != null) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('isLockDownPeriodAttempt').exception()
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
    void setMaxSameUserAttemptIntervalMap(String maxSameUserAttemptIntervalStr) {
        String[] maxSameUserAttemptIntervalArray = maxSameUserAttemptIntervalStr.split(';')
        this.maxSameUserAttemptIntervalMap = new HashMap<>()
        maxSameUserAttemptIntervalArray.each { String retryIntervalStr ->
            String[] retryIntervalArray = retryIntervalStr.split(':')
            assert retryIntervalArray.size() == 2
            this.maxSameUserAttemptIntervalMap.put(parseInteger(retryIntervalArray[0]), parseInteger(retryIntervalArray[1]))
        }
    }

    @Required
    void setUserCredentialVerifyAttemptService(UserCredentialVerifyAttemptService userCredentialVerifyAttemptService) {
        this.userCredentialVerifyAttemptService = userCredentialVerifyAttemptService
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
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
    void setUserPasswordService(UserPasswordService userPasswordService) {
        this.userPasswordService = userPasswordService
    }

    @Required
    void setUserPinService(UserPinService userPinService) {
        this.userPinService = userPinService
    }

    @Required
    void setNormalizeService(NormalizeService normalizeService) {
        this.normalizeService = normalizeService
    }

    @Required
    void setMaxRetryInterval(String maxRetryInterval) {
        this.maxRetryInterval = parseInteger(maxRetryInterval)
    }

    @Required
    void setMaxLockDownTime(String maxLockDownTimeStr) {
        String[] retryIntervalArray = maxLockDownTimeStr.split(';')
        this.maxLockDownTime = new HashMap<>()
        retryIntervalArray.each { String retryIntervalStr ->
            String[] retryIntervalStrArray = retryIntervalStr.split(':')
            assert retryIntervalStrArray.size() == 2
            this.maxLockDownTime.put(parseInteger(retryIntervalStrArray[0]), parseInteger(retryIntervalStrArray[1]))
        }
    }

    @Required
    void setMaxLockDownMail(String maxLockDownMailStr) {
        String[] lockDownMailTemplates = maxLockDownMailStr.split(';')
        this.maxLockDownMail = new HashMap<>()
        lockDownMailTemplates.each { String lockDownMailTemplate ->
            String[] lockDownMailTemplateMap = lockDownMailTemplate.split(':')
            assert lockDownMailTemplateMap.size() == 2
            this.maxLockDownMail.put(parseInteger(lockDownMailTemplateMap[0]), lockDownMailTemplateMap[1])
        }
    }

    private Integer parseInteger(String value) {
        if ("MAX_VALUE".equalsIgnoreCase(value)) {
            return Integer.MAX_VALUE
        }

        return Integer.parseInt(value)
    }


    @Required
    void setMaxSameIPIntervalMap(String maxSameIPIntervalStr) {
        String[] maxSameIPIntervalStrArray = maxSameIPIntervalStr.split(';')
        this.maxSameIPIntervalMap = new HashMap<>()
        maxSameIPIntervalStrArray.each { String retryIntervalStr ->
            String[] retryIntervalStrArray = retryIntervalStr.split(':')
            assert retryIntervalStrArray.size() == 2
            this.maxSameIPIntervalMap.put(parseInteger(retryIntervalStrArray[0]), parseInteger(retryIntervalStrArray[1]))
        }
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
    void setUserPersonalInfoService(UserPersonalInfoService userPersonalInfoService) {
        this.userPersonalInfoService = userPersonalInfoService
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

    @Required
    void setEnableMailSend(Boolean enableMailSend) {
        this.enableMailSend = enableMailSend
    }

    @Required
    void setMaxRetryCountEnable(Boolean maxRetryCountEnable) {
        this.maxRetryCountEnable = maxRetryCountEnable
    }

    @Required
    void setMaxSameIPAttemptsEnable(Boolean maxSameIPAttemptsEnable) {
        this.maxSameIPAttemptsEnable = maxSameIPAttemptsEnable
    }

    @Required
    void setMaxSameUserAttemptsEnable(Boolean maxSameUserAttemptsEnable) {
        this.maxSameUserAttemptsEnable = maxSameUserAttemptsEnable
    }
}
