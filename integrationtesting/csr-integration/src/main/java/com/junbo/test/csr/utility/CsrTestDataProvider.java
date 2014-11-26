/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.csr.utility;

import com.junbo.csr.spec.def.SearchType;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.Identity.UserInfo;
import com.junbo.test.common.Utility.BaseTestDataProvider;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.csr.apihelper.SearchService;
import com.junbo.test.csr.apihelper.impl.SearchServiceImpl;

import java.util.List;

/**
 * Created by weiyu_000 on 11/25/14.
 */
public class CsrTestDataProvider extends BaseTestDataProvider {
    protected UserService identityClient = UserServiceImpl.instance();
    protected OAuthService oAuthClient = OAuthServiceImpl.getInstance();
    protected SearchService searchService = SearchServiceImpl.getInstance();

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

}
