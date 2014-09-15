package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.csr.spec.def.CsrLogActionType
import com.junbo.csr.spec.model.CsrLog
import com.junbo.csr.spec.resource.CsrLogResource
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.email.spec.resource.EmailResource
import com.junbo.email.spec.resource.EmailTemplateResource
import com.junbo.identity.auth.UserAuthorizeCallbackFactory
import com.junbo.identity.common.util.Constants
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.filter.UserFilter
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.validator.UserValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.*
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 4/10/14.
 */
@Transactional
@CompileStatic
class UserResourceImpl implements UserResource {
    private static final String EMAIL_SOURCE = 'Oculus'
    private static final String EMAIL_DEACTIVATE_ACCOUNT_ACTION = 'DeactivateAccount'
    private static final String EMAIL_REACTIVATE_ACCOUNT_ACTION = 'ReactivateAccount'
    private static final String EMAIL_PII_CHANGE_ACTION = 'UserPersonalInfoChange'

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource)

    @Autowired
    private UserRepository userRepository

    @Autowired
    private UserPersonalInfoRepository userPersonalInfoRepository

    @Autowired
    private UserGroupRepository userGroupRepository

    @Autowired
    private UserPasswordRepository userPasswordRepository

    @Autowired
    private UserPinRepository userPinRepository

    @Autowired
    private UserValidator userValidator

    @Autowired
    private UserFilter userFilter

    @Autowired
    private NormalizeService normalizeService

    @Autowired
    private AuthorizeService authorizeService

    @Autowired
    private UserAuthorizeCallbackFactory userAuthorizeCallbackFactory

    @Autowired
    @Qualifier('identityCsrLogResource')
    private CsrLogResource csrLogResource

    @Autowired
    @Qualifier('identityEmailResource')
    private EmailResource emailResource

    @Autowired
    @Qualifier('identityEmailTemplateResource')
    private EmailTemplateResource emailTemplateResource

    @Autowired
    @Value('${identity.conf.mailEnable}')
    private Boolean identityMailSentEnable

    void setIdentityMailSentEnable(Boolean identityMailSentEnable) {
        this.identityMailSentEnable = identityMailSentEnable
    }

    @Override
    Promise<User> create(User user) {
        if (user == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        def callback = userAuthorizeCallbackFactory.create(user)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            user = userFilter.filterForCreate(user)

            return userValidator.validateForCreate(user).then {
                return userRepository.create(user).then { User newUser ->
                    Created201Marker.mark(newUser.getId())

                    newUser = userFilter.filterForGet(newUser, null)
                    return Promise.pure(newUser)
                }
            }
        }
    }

    @Override
    Promise<User> put(UserId userId, User user) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (user == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        return userRepository.get(userId).then { User oldUser ->
            return silentPut(userId, user).then { User newUser ->
                return isMailSentRequired(oldUser, newUser).then { Boolean aBoolean ->
                    //todo: Need to discuss with Hao to move all those mail trigger to service layer.
                    if (!aBoolean) {
                        return Promise.pure(newUser)
                    }
                    // send email
                    return triggerCsrUserUpdateEmail(newUser, oldUser).then {
                        return sendPIIChangeNotification(newUser, oldUser).then {
                            return Promise.pure(newUser)
                        }
                    }
                }
            }
        }
    }

    @Override
    public Promise<User> silentPut(UserId userId, User user) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (user == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        return userRepository.get(userId).then { User oldUser ->
            if (oldUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            def callback = userAuthorizeCallbackFactory.create(oldUser)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                user = userFilter.filterForPut(user, oldUser)

                auditCSR(user, oldUser)

                return userValidator.validateForUpdate(user, oldUser).then {
                    return userRepository.update(user, oldUser).then { User newUser ->
                        return updateCredential(user.getId(), oldUser.username, newUser.username).then {
                            newUser = userFilter.filterForGet(newUser, null)
                            return Promise.pure(newUser)
                        }
                    }
                }
            }
        }
    }

    @Override
    Promise<User> patch(UserId userId, User user) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        return userRepository.get(userId).then { User oldUser ->
            if (oldUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            def callback = userAuthorizeCallbackFactory.create(oldUser)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('update')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                user = userFilter.filterForPatch(user, oldUser)

                auditCSR(user, oldUser)

                return userValidator.validateForUpdate(user, oldUser).then {
                    return userRepository.update(user, oldUser).then { User newUser ->
                        return updateCredential(user.getId(), oldUser.username, newUser.username).then {
                            newUser = userFilter.filterForGet(newUser, null)

                            return isMailSentRequired(oldUser, newUser).then { Boolean aBoolean ->
                                if (!aBoolean) {
                                    return Promise.pure(newUser)
                                }

                                // send email
                                return triggerCsrUserUpdateEmail(newUser, oldUser).then {
                                    return sendPIIChangeNotification(newUser, oldUser).then {
                                        return Promise.pure(newUser)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    Promise<User> get(UserId userId, UserGetOptions getOptions) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return userValidator.validateForGet(userId).then { User user ->

            def callback = userAuthorizeCallbackFactory.create(user)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('read')) {
                    throw AppErrors.INSTANCE.userNotFound(userId).exception()
                }

                user = userFilter.filterForGet(user, getOptions.properties?.split(',') as List<String>)

                if (user == null) {
                    throw AppErrors.INSTANCE.userNotFound(userId).exception()
                }

                return Promise.pure(user)
            }
        }
    }

    @Override
    Promise<Results<User>> list(UserListOptions listOptions) {
        return userValidator.validateForSearch(listOptions).then {
            def resultList = new Results<User>(items: [])

            def filterUser = { User user ->
                def callback = userAuthorizeCallbackFactory.create(user)
                return RightsScope.with(authorizeService.authorize(callback)) {
                    if (!AuthorizeContext.hasRights('search')) {
                        throw AppCommonErrors.INSTANCE.forbidden().exception()
                    }

                    if (!AuthorizeContext.hasScopes('csr') && user.status == UserStatus.DELETED.toString()) {
                        return Promise.pure(null)
                    }

                    if (user == null) {
                        return Promise.pure(user)
                    }

                    user = userFilter.filterForGet(user, listOptions.properties?.split(',') as List<String>)

                    if (user != null) {
                        resultList.items.add(user)
                        return Promise.pure(user)
                    } else {
                        return Promise.pure(null)
                    }
                }
            }

            if (listOptions.username != null) {
                String canonicalUsername = normalizeService.normalize(listOptions.username)
                return userPersonalInfoRepository.searchByCanonicalUsername(canonicalUsername, Integer.MAX_VALUE, 0).then { List<UserPersonalInfo> userPersonalInfoList ->
                    User user = null
                    return Promise.each(userPersonalInfoList.iterator()) { UserPersonalInfo userPersonalInfo ->
                        return userRepository.get(userPersonalInfo.userId).then { User existing ->
                            if (existing.username == userPersonalInfo.getId()
                             && (existing.status != UserStatus.DELETED.toString() || AuthorizeContext.hasScopes('csr'))) {
                                user = existing
                                return Promise.pure(Promise.BREAK)
                            }

                            return Promise.pure(null)
                        }
                    }.then {
                        if (user == null) {
                            return Promise.pure(resultList)
                        } else {
                            filterUser(user).then {
                                return Promise.pure(resultList)
                            }
                        }
                    }
                }
            } else {
                return userGroupRepository.searchByGroupId(listOptions.groupId, listOptions.limit,
                        listOptions.offset).then { List<UserGroup> userGroupList ->
                    return Promise.each(userGroupList) { UserGroup userGroup ->
                        return userValidator.validateForGet(userGroup.userId).then(filterUser)
                    }.then {
                        return Promise.pure(resultList)
                    }
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        return userValidator.validateForGet(userId).then { User user ->
            def callback = userAuthorizeCallbackFactory.create(user)
            return RightsScope.with(authorizeService.authorize(callback)) {
                if (!AuthorizeContext.hasRights('delete') || !AuthorizeContext.hasScopes('csr')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                user.setStatus(UserStatus.DELETED.toString())
                return userRepository.update(user, user).then {
                    return Promise.pure(null)
                }
            }
        }
    }

    @Override
    Promise<Void> checkUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('username').exception()
        }

        return userValidator.validateUsername(username)
    }

    @Override
    Promise<Void> checkEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('email').exception()
        }

        return userValidator.validateEmail(email)
    }

    Promise<Void> updateCredential(UserId userId, UserPersonalInfoId oldUsername, UserPersonalInfoId newUsername) {
        if (oldUsername == newUsername) {
            return Promise.pure(null)
        } else {
            // if username changes, will disable all passwords and pins. User needs to reset this.
            return userPasswordRepository.searchByUserIdAndActiveStatus(userId, true, Integer.MAX_VALUE, 0).then {
                List<UserPassword> userPasswordList ->
                    if (CollectionUtils.isEmpty(userPasswordList)) {
                        return Promise.pure(null)
                    }

                    return Promise.each(userPasswordList.iterator()) { UserPassword userPassword ->
                        userPassword.active = false
                        return userPasswordRepository.update(userPassword, userPassword)
                    }.then {
                        return Promise.pure(null)
                    }
            }.then {
                return userPinRepository.searchByUserIdAndActiveStatus(userId, true, Integer.MAX_VALUE, 0).then {
                    List<UserPin> userPinList ->
                        if (CollectionUtils.isEmpty(userPinList)) {
                            return Promise.pure(null)
                        }

                        return Promise.each(userPinList.iterator()) { UserPin userPin ->
                            userPin.active = false
                            return userPinRepository.update(userPin, userPin)
                        }.then {
                            return Promise.pure(null)
                        }
                }
            }
        }
    }

    private Promise<String> getUserNameStr(User user) {
        if (user.name == null) {
            return Promise.pure('')
        } else {
            return userPersonalInfoRepository.get(user.name).then { UserPersonalInfo userPersonalInfo ->
                if (userPersonalInfo == null) {
                    return Promise.pure('')
                }

                UserName userName = (UserName)JsonHelper.jsonNodeToObj(userPersonalInfo.value, UserName)
                // Please don't use "${} ${}"
                // http://www.jworks.nl/2011/08/29/gstringimpl-cannot-be-cast-to-java-lang-string/
                // Stupid groovy

                String firstName = StringUtils.isEmpty(userName.givenName) ? "" : userName.givenName
                String lastName = StringUtils.isEmpty(userName.familyName) ? "" : userName.familyName
                return Promise.pure(firstName + ' ' + lastName)
            }
        }
    }

    private Promise<String> getUserLoginNameStr(User user) {
        if (user.username == null) {
            return Promise.pure('')
        } else {
            return userPersonalInfoRepository.get(user.username).then { UserPersonalInfo userPersonalInfo ->
                if (userPersonalInfo == null) {
                    return Promise.pure('')
                }

                UserLoginName loginName = (UserLoginName) JsonHelper.jsonNodeToObj(userPersonalInfo.value, UserLoginName)
                return Promise.pure(loginName.userName)
            }
        }
    }

    Promise<String> getEmailStr(User user) {
        if (CollectionUtils.isEmpty(user.emails)) {
            LOGGER.warn('user emails is empty')
            return Promise.pure(null)
        }
        UserPersonalInfoLink link = user.emails.find { UserPersonalInfoLink infoLink ->
            return infoLink.isDefault
        }

        link = link == null ? user.emails.get(0) : link;
        return userPersonalInfoRepository.get(link.value).then { UserPersonalInfo userPersonalInfo ->
            Email email = (Email)JsonHelper.jsonNodeToObj(userPersonalInfo.value, Email)
            return Promise.pure(email.info)
        }
    }

    private void auditCSR(User user, User oldUser) {
        // csr audit log
        if (AuthorizeContext.hasScopes('csr') && AuthorizeContext.currentUserId != null) {
            // country updated
            if (user.countryOfResidence != oldUser.countryOfResidence) {
                csrLogResource.create(new CsrLog(userId: AuthorizeContext.currentUserId, regarding: 'Account', action: CsrLogActionType.CountryUpdated,
                        property: getUserLoginNameStr(oldUser).get())).get()
            }

            // deactive account
            if (oldUser.status == 'ACTIVE' && (user.status == 'SUSPEND' || user.status == 'BANNED')) {
                csrLogResource.create(new CsrLog(userId: AuthorizeContext.currentUserId, regarding: 'Account', action: CsrLogActionType.DeactiveAccount,
                        property: getUserLoginNameStr(oldUser).get())).get()
            }

            // reactive account
            if (user.status == 'ACTIVE' && (oldUser.status == 'SUSPEND' || oldUser.status == 'BANNED' || oldUser.status == 'DELETED')) {
                csrLogResource.create(new CsrLog(userId: AuthorizeContext.currentUserId, regarding: 'Account', action: CsrLogActionType.ReactiveAccount,
                        property: getUserLoginNameStr(oldUser).get())).get()
            }

            // flag for deletion
            if (user.status == 'DELETED') {
                csrLogResource.create(new CsrLog(userId: AuthorizeContext.currentUserId, regarding: 'Account', action: CsrLogActionType.FlagForDelection,
                        property: getUserLoginNameStr(oldUser).get())).get()
            }
        }
    }

    private Promise<Void> triggerCsrUserUpdateEmail(User user, User oldUser) {
        // check x-send-email header
        if (JunboHttpContext.getRequestHeaders() == null
                || CollectionUtils.isEmpty(JunboHttpContext.getRequestHeaders().get(Constants.HEADER_TRIGGER_EMAIL))) {
            return Promise.pure(null)
        }

        try {
            QueryParam queryParam
            // trigger update user update email
            // deactive account
            if (oldUser.status == 'ACTIVE' && (user.status == 'SUSPEND' || user.status == 'BANNED' || user.status == 'DELETED')) {
                queryParam = new QueryParam(
                        source: EMAIL_SOURCE,
                        action: EMAIL_DEACTIVATE_ACCOUNT_ACTION,
                        locale: 'en_US'   //todo: remove hardcode locale when email template is localized
                )
            }

            // reactive account
            if (user.status == 'ACTIVE' && (oldUser.status == 'SUSPEND' || oldUser.status == 'BANNED' || oldUser.status == 'DELETED')) {
                queryParam = new QueryParam(
                        source: EMAIL_SOURCE,
                        action: EMAIL_REACTIVATE_ACCOUNT_ACTION,
                        locale: 'en_US' //todo: remove hardcode locale when email template is localized
                )
            }

            if (queryParam == null) {
                return Promise.pure(null)
            }

            return emailTemplateResource.getEmailTemplates(queryParam).then { Results<EmailTemplate> results ->
                if (results.items.isEmpty()) {
                    throw AppCommonErrors.INSTANCE.internalServerError(new Exception('email template not found')).exception()
                }

                EmailTemplate template = results.items.get(0)

                com.junbo.email.spec.model.Email emailToSend = new com.junbo.email.spec.model.Email(
                        userId: user.getId(),
                        templateId: template.getId(),
                        recipients: [getEmailStr(user).get()].asList(),
                        replacements: [
                                'name': getUserNameStr(user).get(),
                                'username': getUserLoginNameStr(user).get()
                        ]
                )

                return emailResource.postEmail(emailToSend).then {
                    return Promise.pure(null)
                }.recover {
                    return Promise.pure(null)
                }
            }.recover {
                return Promise.pure(null)
            }
        }
        catch (Exception e) {
            LOGGER.error('Send user status update email failed', e)
            return Promise.pure(null)
        }
    }

    private Promise<Void> sendPIIChangeNotification(User newUser, User oldUser) {
        String piiChangedType = getChangeType(oldUser, newUser);
        if (StringUtils.isEmpty(piiChangedType)) {
            return Promise.pure(null)
        }
        return sendPIIChangeEmail(newUser, piiChangedType).recover { Throwable e ->
            LOGGER.error("Send Mail failure")
            return Promise.pure(null)
        }
    }

    private static String getPIIDataType(User oldUser, User newUser) {
        if (isPIIChanged(oldUser.username, newUser.username)) {
            return UserPersonalInfoType.USERNAME.toString()
        }
        if (isPIIChanged(oldUser.name, newUser.name)) {
            return UserPersonalInfoType.NAME.toString()
        }
        if (isPIIChanged(oldUser.dob, newUser.dob)) {
            return UserPersonalInfoType.DOB.toString()
        }
        if (isPIIChanged(oldUser.passport, newUser.passport)) {
            return UserPersonalInfoType.PASSPORT.toString()
        }
        if (isPIIChanged(oldUser.governmentId, newUser.governmentId)) {
            return UserPersonalInfoType.GOVERNMENT_ID.toString()
        }
        if (isPIIChanged(oldUser.driversLicense, newUser.driversLicense)) {
            return UserPersonalInfoType.DRIVERS_LICENSE.toString()
        }
        if (isPIIChanged(oldUser.gender, newUser.gender)){
            return UserPersonalInfoType.GENDER.toString()
        }
        if (isPIILinkListChanged(oldUser.addresses, newUser.addresses)) {
            return UserPersonalInfoType.ADDRESS.toString()
        }
        if (isPIILinkListChanged(oldUser.emails, newUser.emails)) {
            return UserPersonalInfoType.EMAIL.toString()
        }
        if (isPIILinkListChanged(oldUser.phones, newUser.phones)) {
            return UserPersonalInfoType.PHONE.toString()
        }
        if (isPIILinkListChanged(oldUser.textMessages, newUser.textMessages)) {
            return UserPersonalInfoType.TEXT_MESSAGE.toString()
        }
        if (isPIILinkListChanged(oldUser.qqs, newUser.qqs)) {
            return UserPersonalInfoType.QQ.toString()
        }
        if (isPIILinkListChanged(oldUser.whatsApps, newUser.whatsApps)) {
            return UserPersonalInfoType.WHATSAPP.toString()
        }

        return null
    }

    private static String getNonPIIDataType(User oldUser, User newUser) {
        if (oldUser.nickName != newUser.nickName) {
            return 'nickName'
        }

        if (oldUser.preferredLocale != newUser.preferredLocale) {
            return 'preferredLocale'
        }

        if (oldUser.preferredTimezone != newUser.preferredTimezone) {
            return 'preferredLocale'
        }

        if (oldUser.countryOfResidence != newUser.countryOfResidence) {
            return 'cor'
        }

        if (isVatChanged(oldUser.vat, newUser.vat)) {
            return 'vat'
        }

        if (oldUser.defaultPI != newUser.defaultPI) {
            return 'defaultPI'
        }

        return null
    }

    private static String getChangeType(User oldUser, User user) {
        String piiDataType = getPIIDataType(oldUser, user)
        if (!StringUtils.isEmpty(piiDataType)) {
            return piiDataType
        }
        if (AuthorizeContext.hasScopes('csr') && AuthorizeContext.currentUserId != null) {
            return getNonPIIDataType(oldUser, user)
        }

        return null
    }

    private Promise<Boolean> isMailSentRequired(User oldUser, User newUser) {
        if (!identityMailSentEnable) {
            return Promise.pure(false)
        }

        if (JunboHttpContext.getRequestHeaders() == null
                || !CollectionUtils.isEmpty(JunboHttpContext.getRequestHeaders().get(Constants.HEADER_DISABLE_EMAIL))) {
            return Promise.pure(false)
        }

        return hasVerifiedEmail(oldUser).then { Boolean aBoolean ->
            if (!aBoolean) {
                return Promise.pure(false)
            }
            return hasVerifiedEmail(newUser)
        }
    }

    private Promise<Boolean> hasVerifiedEmail(User user) {
        if (CollectionUtils.isEmpty(user.emails)) {
            LOGGER.warn('user emails is empty')
            return Promise.pure(false)
        }
        UserPersonalInfoLink link = user.emails.find { UserPersonalInfoLink infoLink ->
            return infoLink.isDefault
        }

        link = link == null ? user.emails.get(0) : link;
        return userPersonalInfoRepository.get(link.value).then { UserPersonalInfo userPersonalInfo ->
            if (userPersonalInfo.lastValidateTime != null) {
                return Promise.pure(true)
            }
            return Promise.pure(false)
        }
    }

    private static Boolean isPIILinkListChanged(List<UserPersonalInfoLink> oldInfoLinks, List<UserPersonalInfoLink> infoLinks) {
        Boolean flag = true
        if (!CollectionUtils.isEmpty(oldInfoLinks) && !CollectionUtils.isEmpty(infoLinks)) {
            if (oldInfoLinks.size() != infoLinks.size()) {
                return true
            }
            oldInfoLinks.each { UserPersonalInfoLink oldInfoLink ->
                flag = flag && infoLinks.find { UserPersonalInfoLink newInfoLink ->
                    return (oldInfoLink.isDefault == newInfoLink.isDefault
                        || oldInfoLink.value == newInfoLink.value
                        || oldInfoLink.label == newInfoLink.label)
                }
            }
        } else if(!CollectionUtils.isEmpty(oldInfoLinks) || !CollectionUtils.isEmpty(infoLinks)) {
            return true
        }

        return !flag
    }

    private static Boolean isPIIChanged(UserPersonalInfoId oldPIIId, UserPersonalInfoId newPIIId) {
        return oldPIIId != newPIIId
    }

    private static Boolean isVatChanged(Map<String, UserVAT> oldVat, Map<String, UserVAT> newVat) {
        if ((oldVat == null || oldVat.isEmpty()) && (newVat == null || newVat.isEmpty())) {
            return false
        }

        if ((oldVat == null || oldVat.isEmpty()) && !newVat.isEmpty()) {
            return true
        }

        if (!oldVat.isEmpty() && (newVat == null || newVat.isEmpty())) {
            return true
        }

        if (oldVat.size() != newVat.size()) {
            return true
        }

        Boolean flag = true
        oldVat.entrySet().each { Map.Entry<String, UserVAT> vatEntry ->
            UserVAT userVAT = newVat.get(vatEntry.getKey())
            if (userVAT == null) {
                flag = false
            } else {
                if (userVAT.lastValidateTime != vatEntry.value.lastValidateTime || userVAT.vatNumber != vatEntry.value.vatNumber) {
                    flag = false
                }
            }
        }

        if (!flag) return true
    }

    private Promise<Void> sendPIIChangeEmail(User user, String piiType) {
        QueryParam queryParam = new QueryParam(
                source: EMAIL_SOURCE,
                action: EMAIL_PII_CHANGE_ACTION,
                locale: 'en_US'
        )

        if (CollectionUtils.isEmpty(user.emails)) {
            return Promise.pure(null)
        }

        if (user.username == null) {
            throw AppCommonErrors.INSTANCE.internalServerError(new Exception('username is missing')).exception()
        }

        return emailTemplateResource.getEmailTemplates(queryParam).then { Results<EmailTemplate> results ->
            if (results.items.isEmpty()) {
                throw AppCommonErrors.INSTANCE.internalServerError(new Exception(EMAIL_PII_CHANGE_ACTION + ' with locale: en_US template not found')).exception()
            }
            EmailTemplate template = results.items.get(0)

            return getUserNameStr(user).then { String userName ->
                return getEmailStr(user).then { String userMail ->
                    if (StringUtils.isEmpty(userMail)) {
                        throw AppErrors.INSTANCE.userInInvalidStatus(user.getId()).exception()
                    }

                    com.junbo.email.spec.model.Email emailToSend = new com.junbo.email.spec.model.Email(
                            userId: user.getId(),
                            templateId: template.getId(),
                            recipients: [userMail].asList(),
                            replacements: [
                                    'name': userName
                            ]
                    )

                    return emailResource.postEmail(emailToSend).then { Email emailSent ->
                        if (emailSent == null) {
                            throw AppCommonErrors.INSTANCE.internalServerError(new Exception('Failed to send mail')).exception()
                        }

                        return Promise.pure(null)
                    }
                }
            }
        }
    }
}
