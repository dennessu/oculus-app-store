/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.apihelper;

import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.login.CreateUserRequest;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public interface LoginService {
    AuthTokenResponse CreateUser(CreateUserRequest createUserRequest) throws Exception;

    AuthTokenResponse CreateUser(CreateUserRequest createUserRequest, int expectedResponseCode) throws Exception;

}
