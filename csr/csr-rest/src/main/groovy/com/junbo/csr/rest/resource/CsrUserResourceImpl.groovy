package com.junbo.csr.rest.resource

import com.junbo.common.id.EmailTemplateId
import com.junbo.common.id.GroupId
import com.junbo.common.id.UserGroupId
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.csr.core.service.IdentityService
import com.junbo.csr.db.repo.CsrInvitationCodeRepository
import com.junbo.csr.spec.error.AppErrors
import com.junbo.csr.spec.model.CsrGroup
import com.junbo.csr.spec.model.CsrInvitationCode
import com.junbo.csr.spec.model.CsrUser
import com.junbo.csr.spec.option.list.CsrGroupListOptions
import com.junbo.csr.spec.resource.CsrGroupResource
import com.junbo.csr.spec.resource.CsrUserResource
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.email.spec.resource.EmailResource
import com.junbo.email.spec.resource.EmailTemplateResource
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.option.model.GroupGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.GroupResource
import com.junbo.identity.spec.v1.resource.UserGroupMembershipResource
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder

/**
 * Created by haomin on 14-7-14.
 */
@CompileStatic
class CsrUserResourceImpl implements CsrUserResource {
    private static final String CSR_INVITATION_PATH = 'csr-users/invite'
    private static final String EMAIL_SOURCE = 'Oculus'
    private static final String CSR_INVITATION_ACTION = 'CsrInvitation'
    private static final Logger LOGGER = LoggerFactory.getLogger(CsrUserResourceImpl)

    private UserResource userResource
    private UserPersonalInfoResource userPersonalInfoResource
    private UserGroupMembershipResource userGroupMembershipResource
    private GroupResource groupResource
    private EmailTemplateResource emailTemplateResource
    private EmailResource emailResource

    private IdentityService identityService

    private CsrInvitationCodeRepository csrInvitationCodeRepository
    private CsrGroupResource csrGroupResource
    private String pendingGroupName
    private String inactiveGroupName

    @Required
    void setCsrGroupResource(CsrGroupResource csrGroupResource) {
        this.csrGroupResource = csrGroupResource
    }

    @Required
    void setUserPersonalInfoResource(UserPersonalInfoResource userPersonalInfoResource) {
        this.userPersonalInfoResource = userPersonalInfoResource
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setPendingGroupName(String pendingGroupName) {
        this.pendingGroupName = pendingGroupName
    }

    @Required
    void setUserGroupMembershipResource(UserGroupMembershipResource userGroupMembershipResource) {
        this.userGroupMembershipResource = userGroupMembershipResource
    }

    @Required
    void setGroupResource(GroupResource groupResource) {
        this.groupResource = groupResource
    }

    @Required
    void setIdentityService(IdentityService identityService) {
        this.identityService = identityService
    }

    @Required
    void setCsrInvitationCodeRepository(CsrInvitationCodeRepository csrInvitationCodeRepository) {
        this.csrInvitationCodeRepository = csrInvitationCodeRepository
    }

    @Required
    void setEmailTemplateResource(EmailTemplateResource emailTemplateResource) {
        this.emailTemplateResource = emailTemplateResource
    }

    @Required
    void setEmailResource(EmailResource emailResource) {
        this.emailResource = emailResource
    }

    @Required
    void setInactiveGroupName(String inactiveGroupName) {
        this.inactiveGroupName = inactiveGroupName
    }

    @Override
    Promise<Results<CsrUser>> list() {
        return csrGroupResource.list(new CsrGroupListOptions()).then { Results<CsrGroup> csrGroupResults ->
            if (csrGroupResults == null || csrGroupResults.items == null) {
                throw AppErrors.INSTANCE.csrGroupNotFound().exception()
            }

            def resultList = new Results<CsrUser>(items: [])
            return Promise.each(csrGroupResults.items) { CsrGroup csrGroup ->
                return userResource.list(new UserListOptions(groupId: csrGroup.groupId)).then { Results<User> userResults ->
                    if (userResults != null && userResults.items != null && userResults.items.size() > 0) {
                        for (User user in userResults.items) {
                            def csrUser = new CsrUser(userId: user.getId() ,username: user.username, countryCode: user.countryOfResidence?.toString(), tier: csrGroup.tier)
                            if (user.name != null) {
                                UserPersonalInfo userPersonalInfo = userPersonalInfoResource.get(user.name.value, new UserPersonalInfoGetOptions()).get()

                                UserName name = ObjectMapperProvider.instance().treeToValue(userPersonalInfo.getValue(), UserName.class)
                                if (name != null) {
                                    csrUser.setName("$name.givenName $name.familyName")
                                }
                            }

                            resultList.items.add(csrUser)
                        }
                    }

                    return Promise.pure(null)
                }
            }.then {
                resultList.items = resultList.items.sort {CsrUser csrUser ->
                    csrUser.name == null ? csrUser.username : csrUser.name
                }
                resultList.total = resultList.items.size()
                return Promise.pure(resultList)
            }
        }
    }

    @Override
    Promise<Response> join(UserId userId, GroupId groupId) {
        if (userId == null || groupId == null) {
            throw AppErrors.INSTANCE.invalidRequest().exception()
        }

        Results<CsrGroup> csrGroupResults = csrGroupResource.list(new CsrGroupListOptions()).get()

        List<GroupId> groupIds = csrGroupResults.items.empty ?
                (List<GroupId>) Collections.emptyList() :
                csrGroupResults.items.collect { CsrGroup csrGroup -> csrGroup.groupId }

        return userGroupMembershipResource.list(new UserGroupListOptions(userId: userId)).then { Results<UserGroup> results ->
            if (results != null && results.items != null) {
                return Promise.each(results.items) { UserGroup userGroup ->
                    if (userGroup.groupId != groupId && groupIds.contains(userGroup.groupId)) {
                        return userGroupMembershipResource.delete(userGroup.id as UserGroupId)
                    }
                }.then {
                    results.items.retainAll { UserGroup userGroup ->
                        userGroup.groupId == groupId
                    }

                    if (results.items.size() == 0) {
                        return userGroupMembershipResource.create(new UserGroup(userId: userId, groupId: groupId))
                    }
                }
            }
            else {
                return userGroupMembershipResource.create(new UserGroup(userId: userId, groupId: groupId))
            }
        }.then {
            return Promise.pure(Response.noContent().build())
        }
    }

    @Override
    Promise<Response> inviteCsr(String locale, String email, String groupId, ContainerRequestContext requestContext) {
        if (email == null || groupId == null) {
            throw AppErrors.INSTANCE.invalidRequest().exception()
        }

        URI baseUri = ((ContainerRequest)requestContext).baseUri

        return csrGroupResource.list(new CsrGroupListOptions(groupName: pendingGroupName)).then { Results<CsrGroup> csrGroupResults ->
            if (csrGroupResults == null || csrGroupResults.items == null || csrGroupResults.items.isEmpty()) {
                throw AppErrors.INSTANCE.pendingCsrGroupNotFound().exception()
            }
            CsrGroup pendingGroup = csrGroupResults.items.get(0)

            return identityService.getUserByVerifiedEmail(email).then { User user ->
                if (user == null) {
                    throw AppErrors.INSTANCE.userNotFound().exception()
                }

                return groupResource.get(new GroupId(groupId), new GroupGetOptions()).then { Group group ->
                    if (group == null) {
                        throw AppErrors.INSTANCE.groupNotFound().exception()
                    }

                    return userGroupMembershipResource.list(new UserGroupListOptions(userId: user.id as UserId, groupId: pendingGroup.groupId)).then { Results<UserGroup> results ->
                        UserGroup userGroup = null
                        if (results == null || results.items == null || results.items.size() == 0) {
                            userGroup = userGroupMembershipResource.create(new UserGroup(userId: user.id as UserId, groupId: pendingGroup.groupId)).get()
                        }
                        else {
                            userGroup = results.items.get(0)
                        }

                        CsrInvitationCode code = new CsrInvitationCode(
                                email: email,
                                userId: (user.id as UserId).value,
                                pendingGroupId: pendingGroup.groupId.value,
                                inviteGroupId: (group.id as GroupId).value,
                                userGroupId: userGroup.id as UserGroupId
                        )

                        csrInvitationCodeRepository.save(code)

                        UriBuilder uriBuilder = UriBuilder.fromUri(baseUri)
                        uriBuilder.path(CSR_INVITATION_PATH)
                        uriBuilder.queryParam('code', code.code)
                        uriBuilder.queryParam('locale', locale == null ? 'en_US' : locale)

                        QueryParam queryParam = new QueryParam(
                                source: EMAIL_SOURCE,
                                action: CSR_INVITATION_ACTION,
                                locale: locale == null ? 'en_US' : locale
                        )

                        String link = uriBuilder.build().toString()
                        return this.sendEmail(queryParam, user, email, link)
                    }
                }
            }
        }.then {
            return Promise.pure(Response.noContent().build())
        }
    }

    @Override
    Promise<Response> confirmCsrInvitation(String code) {
        if (StringUtils.isEmpty(code)) {
            LOGGER.warn(AppErrors.INSTANCE.csrInvitationCodeMissing().toString())
            throw AppErrors.INSTANCE.csrInvitationCodeMissing().exception()
        }

        CsrInvitationCode csrInvitationCode = csrInvitationCodeRepository.getAndRemove(code)

        if (csrInvitationCode == null) {
            LOGGER.warn(AppErrors.INSTANCE.csrInvitationCodeInvalid().toString())
            throw AppErrors.INSTANCE.csrInvitationCodeInvalid().exception()
        }

        csrInvitationCodeRepository.removeByUserIdEmail(csrInvitationCode.userId, csrInvitationCode.email)

        try {
            userGroupMembershipResource.delete(csrInvitationCode.userGroupId as UserGroupId).get()
        }
        catch (Exception e) {
            LOGGER.info('')
        }

        try {
            userGroupMembershipResource.create(new UserGroup(userId: new UserId(csrInvitationCode.userId), groupId: new GroupId(csrInvitationCode.inviteGroupId))).get()
        }
        catch (Exception e) {
            LOGGER.info('')
        }

        return Promise.pure(Response.noContent().build())
    }

    private Promise<String> sendEmail(QueryParam queryParam, User user, String email, String uri) {
        // todo: remove this hard coded after email template has been setup
        queryParam.locale = 'en_US'

        // TODO: cache the email template for each locale.
        return emailTemplateResource.getEmailTemplates(queryParam).then { Results<EmailTemplate> results ->
            if (results == null || results.items == null || results.items.isEmpty()) {
                LOGGER.warn('Failed to get the email template, skip the email send')
                throw AppErrors.INSTANCE.emailTemplateNotFound().exception()
            }

            EmailTemplate template = results.items.get(0)

            Email emailToSend = new Email(
                    userId: user.id as UserId,
                    templateId: template.id as EmailTemplateId,
                    recipients: [email].asList(),
                    replacements: [
                            'name': user.username,
                            'link': uri
                    ]
            )

            return emailResource.postEmail(emailToSend).then { Email emailSent ->
                if (emailSent == null) {
                    LOGGER.warn('Failed to send the email, skip the email send')
                    throw AppErrors.INSTANCE.sendEmailFailed().exception()
                }

                // Return success no matter the email has been successfully sent.
                return Promise.pure(uri)
            }
        }
    }
}
