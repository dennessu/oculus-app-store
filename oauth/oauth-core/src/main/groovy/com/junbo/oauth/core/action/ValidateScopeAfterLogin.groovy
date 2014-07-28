/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.option.list.GroupListOptions
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import com.junbo.identity.spec.v1.resource.GroupResource
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.identity.spec.v1.resource.UserGroupMembershipResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.util.ExceptionUtil
import com.junbo.oauth.db.repo.ScopeRepository
import com.junbo.oauth.spec.model.Scope
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * ValidateScopeAfterLogin.
 */
@CompileStatic
class ValidateScopeAfterLogin implements Action {
    private ScopeRepository scopeRepository

    private GroupResource groupResource

    private OrganizationResource organizationResource

    private UserGroupMembershipResource userGroupMembershipResource

    private boolean isAuthorizeFlow

    @Required
    void setScopeRepository(ScopeRepository scopeRepository) {
        this.scopeRepository = scopeRepository
    }

    @Required
    void setGroupResource(GroupResource groupResource) {
        this.groupResource = groupResource
    }

    @Required
    void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource
    }

    @Required
    void setUserGroupMembershipResource(UserGroupMembershipResource userGroupMembershipResource) {
        this.userGroupMembershipResource = userGroupMembershipResource
    }

    @Required
    void setIsAuthorizeFlow(boolean isAuthorizeFlow) {
        this.isAuthorizeFlow = isAuthorizeFlow
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def oauthInfo = contextWrapper.oauthInfo
        def loginState = contextWrapper.loginState

        Assert.notNull(oauthInfo, 'oauthInfo is null')
        Assert.notNull(loginState, 'loginState is null')

        List<Scope> scopes = []
        oauthInfo.scopes.each { String scopeString ->
            scopes.add(scopeRepository.getScope(scopeString))
        }

        boolean tfaRequired = false
        boolean checkGroupsRequired = false
        scopes.each { Scope scope ->
            if (scope.tfaRequired) {
                tfaRequired = true
            }

            if (scope.allowedUserGroups != null && !scope.allowedUserGroups.isEmpty()) {
                checkGroupsRequired = true
            }
        }

        if (checkGroupsRequired) {
            Boolean allowed = true
            StringBuilder forbiddenScopes = new StringBuilder()
            return Promise.each(scopes) { Scope scope ->
                if (scope.allowedUserGroups != null && !scope.allowedUserGroups.isEmpty()) {
                    Boolean scopeAllowed = false
                    return Promise.each(scope.allowedUserGroups) { String allowedUserGroup ->
                        return checkUserGroup(allowedUserGroup, loginState.userId, contextWrapper).then { Boolean inGroup ->
                            if (inGroup) {
                                scopeAllowed = true
                            }
                        }
                    }.then {
                        if (!scopeAllowed) {
                            allowed = false
                        }
                    }
                }
                return Promise.pure(null)
            }.then {
                if (allowed) {
                    return Promise.pure(new ActionResult('success'))
                } else {
                    if (!isAuthorizeFlow) {
                        throw AppCommonErrors.INSTANCE
                                .forbiddenWithMessage("The user does not has required" +
                                " group for scopes $forbiddenScopes").exception()
                    } else {
                        contextWrapper.errors.add(AppCommonErrors.INSTANCE.
                                forbiddenWithMessage("The user does not has required group for scopes $forbiddenScopes").error())
                        return Promise.pure(new ActionResult('forbidden'))
                    }
                }
            }
        }

        if (tfaRequired && isAuthorizeFlow) {
            return Promise.pure(new ActionResult('tfaRequired'))
        }

        return Promise.pure(new ActionResult('success'))
    }

    private Promise<Boolean> checkUserGroup(String allowedUserGroup, Long userId, ActionContextWrapper contextWrapper) {
        String tokens = allowedUserGroup.split('\\.')
        assert tokens.length() == 2
        String organizationName = tokens[0]
        String groupName = tokens[1]

        return organizationResource.list(new OrganizationListOptions(name: organizationName)).recover { Throwable e ->
            ExceptionUtil.handleIdentityException(e, contextWrapper, !isAuthorizeFlow)
            return Promise.pure(null)
        }.then { Results<Organization> results ->
            if (results == null) {
                return Promise.pure(new ActionResult('error'))
            }

            assert !results.items.isEmpty()
            Organization organization = results.items.get(0)

            GroupListOptions options = new GroupListOptions(
                    organizationId: organization.getId(),
                    name: groupName
            )

            return groupResource.list(options).recover { Throwable e ->
                ExceptionUtil.handleIdentityException(e, contextWrapper, !isAuthorizeFlow)
                return Promise.pure(null)
            }.then { Results<Group> groupResults ->
                if (groupResults == null) {
                    return Promise.pure(new ActionResult('error'))
                }

                assert !groupResults.items.isEmpty()
                Group group = groupResults.items.get(0)

                UserGroupListOptions groupListOptions = new UserGroupListOptions(
                        userId: new UserId(userId),
                        groupId: group.getId()
                )
                return userGroupMembershipResource.list(groupListOptions).recover { Throwable e ->
                    ExceptionUtil.handleIdentityException(e, contextWrapper, !isAuthorizeFlow)
                    return Promise.pure(null)
                }.then { Results<UserGroup> userGroupResults ->
                    if (userGroupResults == null) {
                        return Promise.pure(new ActionResult('error'))
                    }
                    return userGroupResults.items.isEmpty()
                }
            }
        }
    }
}
