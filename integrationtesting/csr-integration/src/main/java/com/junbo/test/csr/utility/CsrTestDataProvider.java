/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.csr.utility;

import com.junbo.csr.spec.def.SearchType;
import com.junbo.csr.spec.model.CsrGroup;
import com.junbo.csr.spec.model.CsrUpdate;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.Identity.UserInfo;
import com.junbo.test.common.Utility.BaseTestDataProvider;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.csr.apihelper.GroupService;
import com.junbo.test.csr.apihelper.SearchService;
import com.junbo.test.csr.apihelper.UpdateService;
import com.junbo.test.csr.apihelper.impl.GroupServiceImpl;
import com.junbo.test.csr.apihelper.impl.SearchServiceImpl;
import com.junbo.test.csr.apihelper.impl.UpdateServiceImpl;

import java.util.List;

/**
 * Created by weiyu_000 on 11/25/14.
 */
public class CsrTestDataProvider extends BaseTestDataProvider {
    protected UserService identityClient = UserServiceImpl.instance();
    protected OAuthService oAuthClient = OAuthServiceImpl.getInstance();
    protected SearchService searchService = SearchServiceImpl.getInstance();
    protected GroupService groupService = GroupServiceImpl.getInstance();
    protected UpdateService updateService = UpdateServiceImpl.getInstance();

    public String createUser() throws Exception {
        return identityClient.PostUser();
    }

    public String createUser(UserInfo userInfo) throws Exception {
        return identityClient.PostUser(userInfo);
    }

    public String postCsrAdminAccessToken() throws Exception {
        return oAuthClient.postUserAccessToken("csrAdmin", ConfigHelper.getSetting("csr_admin_account"), ConfigHelper.getSetting("csr_admin_password"), "csr", "csr");
    }

    public List<String> searchUsers(SearchType searchType, String value) throws Exception {
        return searchService.searchUsers(searchType, value);
    }

    public List<CsrGroup> getCsrGroups() throws Exception {
        return groupService.getCsrGroups();
    }

    public List<CsrGroup> getCsrGroups(String groupName, String uid) throws Exception {
        return groupService.getCsrGroups(groupName, uid);
    }

    public String getCsrAdminUid(String accessToken) throws Exception {
        return IdConverter.idToHexString(oAuthClient.getTokenInfo(accessToken).getSub());
    }

    public CsrUpdate createCsrUpdate(String uid, String content) throws Exception {
        return updateService.createUpdate(uid, content);
    }

    public CsrUpdate getCsrUpdate(String updateId) throws Exception {
        return updateService.getUpdate(updateId);
    }

    public CsrUpdate getCsrUpdate(String updateId, int expectedResponseCode) throws Exception {
        return updateService.getUpdate(updateId, expectedResponseCode);
    }

    public CsrUpdate patchCsrUpdate(String updateId, String uid, String content) throws Exception {
        return updateService.patchUpdate(updateId, uid, content);
    }

    public List<CsrUpdate> getCsrUpdateList(boolean isActive) throws Exception{
        return updateService.getUpdateList(isActive);
    }

    public boolean deleteCsrUpdate(String updateId) throws Exception{
        return updateService.deleteUpdate(updateId);
    }

    public CsrUpdate putCsrUpdate(String updateId, CsrUpdate update) throws Exception{
        return updateService.putUpdate(updateId, update);
    }

}
