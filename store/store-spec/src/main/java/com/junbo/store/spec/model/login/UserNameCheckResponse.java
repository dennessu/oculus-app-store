/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.login;

import com.junbo.store.spec.model.BaseResponse;

import java.util.List;

/**
 * The UserNameCheckResponse class.
 */
public class UserNameCheckResponse extends BaseResponse {

    private List<String> userNameSuggestions;

    public List<String> getUserNameSuggestions() {
        return userNameSuggestions;
    }

    public void setUserNameSuggestions(List<String> userNameSuggestions) {
        this.userNameSuggestions = userNameSuggestions;
    }
}
