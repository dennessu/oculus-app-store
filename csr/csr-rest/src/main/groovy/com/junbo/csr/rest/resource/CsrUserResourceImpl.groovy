package com.junbo.csr.rest.resource

import com.junbo.common.id.GroupId
import com.junbo.common.id.UserGroupId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.csr.core.service.EmailService
import com.junbo.csr.core.service.IdentityService
import com.junbo.csr.db.repo.CsrInvitationCodeRepository
import com.junbo.csr.spec.error.AppErrors
import com.junbo.csr.spec.model.CsrGroup
import com.junbo.csr.spec.model.CsrInvitationCode
import com.junbo.csr.spec.model.CsrUser
import com.junbo.csr.spec.option.list.CsrGroupListOptions
import com.junbo.csr.spec.resource.CsrGroupResource
import com.junbo.csr.spec.resource.CsrUserResource
import com.junbo.email.spec.model.QueryParam
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import com.junbo.identity.spec.v1.resource.UserGroupMembershipResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerRequest
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

    private UserGroupMembershipResource userGroupMembershipResource

    private IdentityService identityService
    private EmailService emailService

    private CsrInvitationCodeRepository csrInvitationCodeRepository
    private CsrGroupResource csrGroupResource
    private String pendingGroupName

    @Required
    void setCsrGroupResource(CsrGroupResource csrGroupResource) {
        this.csrGroupResource = csrGroupResource
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
    void setIdentityService(IdentityService identityService) {
        this.identityService = identityService
    }

    @Required
    void setEmailService(EmailService emailService) {
        this.emailService = emailService
    }

    @Required
    void setCsrInvitationCodeRepository(CsrInvitationCodeRepository csrInvitationCodeRepository) {
        this.csrInvitationCodeRepository = csrInvitationCodeRepository
    }

    @Override
    Promise<Results<CsrUser>> list() {
        return csrGroupResource.list(new CsrGroupListOptions()).then { Results<CsrGroup> csrGroupResults ->
            def resultList = new Results<CsrUser>(items: [])

            // merge users within each csr groups
            return Promise.each(csrGroupResults.items) { CsrGroup csrGroup ->
                return identityService.getUserByGroupId(csrGroup.groupId).then { Results<User> userResults ->
                    for (User user in userResults.items) {
                        def csrUser = new CsrUser(userId: user.getId() ,
                                username: user.username,
                                countryCode: user.countryOfResidence?.toString(),
                                tier: csrGroup.tier)
                        csrUser.name = identityService.getUserNameByUser(user)
                        resultList.items.add(csrUser)
                    }

                    return Promise.pure(null)
                }
            }.then {
                // sort by username
                resultList.items = resultList.items.sort { CsrUser csrUser -> csrUser.username }
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
    Promise<Response> inviteCsr(String locale, String email, GroupId groupId, ContainerRequestContext requestContext) {
        if (email == null || groupId == null) {
            throw AppErrors.INSTANCE.invalidRequest().exception()
        }

        URI baseUri = ((ContainerRequest)requestContext).baseUri

        return csrGroupResource.list(new CsrGroupListOptions(groupName: pendingGroupName)).then { Results<CsrGroup> csrGroupResults ->
            if (csrGroupResults.items.isEmpty()) {
                throw AppErrors.INSTANCE.pendingCsrGroupNotFound().exception()
            }
            CsrGroup pendingGroup = csrGroupResults.items.get(0)

            return identityService.getUserByVerifiedEmail(email).then { User user ->
                return identityService.getGroupById(groupId).then { Group group ->

                    UserGroup userGroup = identityService.saveUserGroupMembership(user.getId(), pendingGroup.groupId)

                    CsrInvitationCode code = new CsrInvitationCode(
                            email: email,
                            userId: user.id as UserId,
                            pendingGroupId: pendingGroup.groupId,
                            inviteGroupId: group.id as GroupId,
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
                    return emailService.sendCSRInvitationEmail(queryParam, email, user, link)
                }
            }
        }.then {
            return Promise.pure(Response.noContent().build())
        }
    }

    @Override
    Promise<Response> confirmCsrInvitation(String code) {
        if (StringUtils.isEmpty(code)) {
            throw AppErrors.INSTANCE.csrInvitationCodeMissing().exception()
        }

        CsrInvitationCode csrInvitationCode = csrInvitationCodeRepository.getAndRemove(code)

        if (csrInvitationCode == null) {
            throw AppErrors.INSTANCE.csrInvitationCodeInvalid().exception()
        }

        csrInvitationCodeRepository.removeByUserIdEmail(csrInvitationCode.userId.value, csrInvitationCode.email)

        // move user from pending group to tier group
        identityService.updateUserGroupMembership(csrInvitationCode.userGroupId, csrInvitationCode.userId, csrInvitationCode.inviteGroupId)

        return Promise.pure(Response.noContent().build())
    }
}
