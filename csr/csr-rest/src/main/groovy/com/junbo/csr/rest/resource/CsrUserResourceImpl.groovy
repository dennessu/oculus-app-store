package com.junbo.csr.rest.resource

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.error.AppCommonErrors
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
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerRequest
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder
import java.util.regex.Pattern

/**
 * Created by haomin on 14-7-14.
 */
@CompileStatic
class CsrUserResourceImpl implements CsrUserResource {
    private static final String CSR_INVITATION_PATH = 'csr-users/invite'
    private static final String EMAIL_SOURCE = 'Oculus'
    private static final String CSR_INVITATION_ACTION = 'CsrInvitation'
    private static final String CSR_SCOPE = "csr";

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$', Pattern.CASE_INSENSITIVE);

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
        authorize()
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
        authorize()
        if (userId == null || groupId == null) {
            throw AppErrors.INSTANCE.invalidRequest().exception()
        }

        // check userId
        identityService.getUserById(userId).get()
        // check groupId
        identityService.getGroupById(groupId).get()

        Results<CsrGroup> csrGroupResults = csrGroupResource.list(new CsrGroupListOptions()).get()
        List<GroupId> groupIds = csrGroupResults.items.empty ?
                (List<GroupId>) Collections.emptyList() :
                csrGroupResults.items.collect { CsrGroup csrGroup -> csrGroup.groupId }

        identityService.switchUserGroupMembershipWithinGroups(userId, groupId, groupIds)
        return Promise.pure(Response.noContent().build())
    }

    @Override
    Promise<Response> inviteCsr(String locale, String email, GroupId groupId, ContainerRequestContext requestContext) {
        authorize()
        if (email == null || groupId == null || requestContext == null) {
            throw AppErrors.INSTANCE.invalidRequest().exception()
        }

        if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
            throw AppErrors.INSTANCE.invalidEmail().exception()
        }

        URI baseUri = ((ContainerRequest)requestContext).baseUri
        return csrGroupResource.list(new CsrGroupListOptions(groupName: pendingGroupName)).then { Results<CsrGroup> csrGroupResults ->
            if (csrGroupResults.items.isEmpty()) {
                throw AppErrors.INSTANCE.pendingCsrGroupNotFound().exception()
            }
            CsrGroup pendingGroup = csrGroupResults.items.get(0)

            return identityService.getUserByVerifiedEmail(email).then { User user ->
                // check the user already in csr groups or not
                Results<CsrGroup> groups = csrGroupResource.list(new CsrGroupListOptions(userId: user.getId())).get()
                if (!groups.items.empty) {
                    if (groups.items.size() != 1 || (groups.items.size() == 1 && groups.items.get(0).groupId != pendingGroup.groupId)) {
                        throw AppErrors.INSTANCE.userAlreadyInCsrGroup(email).exception()
                    }
                }

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
            return Promise.pure(Response.ok().entity(email).build())
        }
    }

    @Override
    Promise<Response> confirmCsrInvitation(String code) {
        authorize()
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

        return Promise.pure(Response.ok().entity('You have joined OculusVR CSR group').build())
    }

    private static void authorize() {
        if (!AuthorizeContext.hasScopes(CSR_SCOPE)) {
            throw AppCommonErrors.INSTANCE.insufficientScope().exception();
        }
    }
}
