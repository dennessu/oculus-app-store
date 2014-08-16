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
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.CollectionUtils

/**
 * Created by liangfu on 4/10/14.
 */
@Transactional
@CompileStatic
class UserResourceImpl implements UserResource {
    private static final String EMAIL_SOURCE = 'Oculus'
    private static final String EMAIL_DEACTIVATE_ACCOUNT_ACTION = 'DeactivateAccount'
    private static final String EMAIL_REACTIVATE_ACCOUNT_ACTION = 'ReactivateAccount'
    private static final Logger logger = LoggerFactory.getLogger(UserResource)

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
                            // send email
                            return triggerUserUpdateEmail(newUser, oldUser).then {
                                return Promise.pure(newUser)
                            }
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
                            // send email
                            return triggerUserUpdateEmail(newUser, oldUser).then {
                                return Promise.pure(newUser)
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
                            if (existing.username == userPersonalInfo.getId()) {
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
                if (!AuthorizeContext.hasRights('delete')) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception()
                }

                return userRepository.delete(userId)
            }
        }
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
        return userPersonalInfoRepository.get(user.username).then { UserPersonalInfo userPersonalInfo ->
            if (userPersonalInfo == null) {
                return Promise.pure(null)
            }

            UserLoginName loginName = (UserLoginName)JsonHelper.jsonNodeToObj(userPersonalInfo.value, UserLoginName)
            return Promise.pure(loginName.userName)
        }
    }

    private Promise<String> getUserEmail(User user) {
        UserPersonalInfoId userPersonalInfoId = user.emails?.find { UserPersonalInfoLink link ->
            link.isDefault
        }?.value

        if (userPersonalInfoId == null) {
            return Promise.pure(null)
        }

        return userPersonalInfoRepository.get(userPersonalInfoId).then { UserPersonalInfo userPersonalInfo ->
            if (userPersonalInfo == null) {
                return Promise.pure(null)
            }

            com.junbo.identity.spec.v1.model.Email email = (com.junbo.identity.spec.v1.model.Email)JsonHelper.jsonNodeToObj(userPersonalInfo.value, com.junbo.identity.spec.v1.model.Email)
            return Promise.pure(email.info)
        }
    }

    private void auditCSR(User user, User oldUser) {
        // csr audit log
        if (AuthorizeContext.hasScopes('csr') && AuthorizeContext.currentUserId != null) {
            // country updated
            if (user.countryOfResidence != oldUser.countryOfResidence) {
                csrLogResource.create(new CsrLog(userId: AuthorizeContext.currentUserId, regarding: 'Account', action: CsrLogActionType.CountryUpdated,
                        property: getUserNameStr(oldUser).get())).get()
            }

            // deactive account
            if (oldUser.status == 'ACTIVE' && (user.status == 'SUSPEND' || user.status == 'BANNED')) {
                csrLogResource.create(new CsrLog(userId: AuthorizeContext.currentUserId, regarding: 'Account', action: CsrLogActionType.DeactiveAccount,
                        property: getUserNameStr(oldUser).get())).get()
            }

            // reactive account
            if (user.status == 'ACTIVE' && (oldUser.status == 'SUSPEND' || oldUser.status == 'BANNED' || oldUser.status == 'DELETED')) {
                csrLogResource.create(new CsrLog(userId: AuthorizeContext.currentUserId, regarding: 'Account', action: CsrLogActionType.ReactiveAccount,
                        property: getUserNameStr(oldUser).get())).get()
            }

            // flag for deletion
            if (user.status == 'DELETED') {
                csrLogResource.create(new CsrLog(userId: AuthorizeContext.currentUserId, regarding: 'Account', action: CsrLogActionType.FlagForDelection,
                        property: getUserNameStr(oldUser).get())).get()
            }
        }
    }

    private Promise<Void> triggerUserUpdateEmail(User user, User oldUser) {
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
                        recipients: [getUserEmail(user).get()],
                        replacements: [
                                'accountname': getUserNameStr(user).get()
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
        catch(Exception e) {
            logger.error('Send user status update email failed', e)
            return Promise.pure(null)
        }
    }
}
