/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity.impl;

import com.junbo.common.id.TosId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosAgreementId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserTosAgreement;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.identity.UserTosAgreementService;
import com.junbo.test.common.libs.IdConverter;

import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 * time 5/13/2014
 * Implementation of User Tos Agreement related APIs, including get/post/put/delete UserTosAgreement.
 */
public class UserTosAgreementServiceImpl extends HttpClientBase implements UserTosAgreementService {

        private final String utaUrl = ConfigHelper.getSetting("defaultIdentityEndpoint") + "/users/%s/tos-agreements";
        private static UserTosAgreementService instance;

    public static synchronized UserTosAgreementService instance() {
        if (instance == null) {
            instance = new UserTosAgreementServiceImpl();
        }
        return instance;
    }

    private UserTosAgreementServiceImpl() {
    }

    //Create a User TOS Agreement
    public UserTosAgreement postUserTosAgreement(UserId userId, UserTosAgreement uta) throws Exception {
        return this.postUserTosAgreement(userId, uta, 201);
    }

    public UserTosAgreement postUserTosAgreement(UserId userId, UserTosAgreement uta, int expectedResponseCode) throws Exception {
        String url = String.format(utaUrl, IdConverter.idToHexString(userId));
        String responseBody = restApiCall(HTTPMethod.POST, url, uta, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<UserTosAgreement>() {}, responseBody);
    }

    //Get a User TOS Agreement info
    public UserTosAgreement getUserTosAgreement(UserId userId, UserTosAgreementId utaId) throws Exception{
        return this.getUserTosAgreement(userId, utaId, 200);
    }

    public UserTosAgreement getUserTosAgreement(UserId userId, UserTosAgreementId utaId, int expectedResponseCode) throws Exception {
        String url = String.format(utaUrl, IdConverter.idToHexString(userId)) + "/" + IdConverter.idToHexString(utaId);
        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<UserTosAgreement>() {}, responseBody);
    }

    //Get User TOS Agreement info list
    public Results<UserTosAgreement> getUserTosAgreementList(UserId userId, TosId tosId) throws Exception {
        return this.getUserTosAgreementList(userId, tosId, 200);
    }

    public Results<UserTosAgreement> getUserTosAgreementList(UserId userId, TosId tosId,
                                                             int expectedResponseCode) throws Exception {
        String url = String.format(utaUrl, IdConverter.idToHexString(userId));
        if (tosId != null) {
            url = url.concat(String.format("?tosId=%s", IdConverter.idToHexString(tosId)));
        }

        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<UserTosAgreement>>() {}, responseBody);
    }

    public Results<UserTosAgreement> getUserTosAgreementList(UserId userId, HashMap<String, List<String>> getOptions) throws Exception {
        return this.getUserTosAgreementList(userId, getOptions, 200);
    }

    public Results<UserTosAgreement> getUserTosAgreementList(UserId userId, HashMap<String, List<String>> getOptions,
                                                             int expectedResponseCode) throws Exception {
        String url = String.format(utaUrl, IdConverter.idToHexString(userId));
        String responseBody = restApiCall(HTTPMethod.GET, url, getOptions, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<UserTosAgreement>>() {}, responseBody);
    }

    //Update a User TOS Agreement
    public UserTosAgreement putUserTosAgreement(UserId userId, UserTosAgreementId utaId, UserTosAgreement uta) throws Exception {
        return this.putUserTosAgreement(userId, utaId, uta, 200);
    }

    public UserTosAgreement putUserTosAgreement(UserId userId, UserTosAgreementId utaId, UserTosAgreement uta, int expectedResponseCode)
            throws Exception {
        String url = String.format(utaUrl, IdConverter.idToHexString(userId)) + "/" + IdConverter.idToHexString(utaId);
        String responseBody = restApiCall(HTTPMethod.PUT, url, uta, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<UserTosAgreement>() {}, responseBody);
    }

    //Delete a User TOS Agreement
    public void deleteUserTosAgreement(UserId userId, UserTosAgreementId utaId) throws Exception {
        this.deleteUserTosAgreement(userId, utaId, 200);
    }
    public void deleteUserTosAgreement(UserId userId, UserTosAgreementId utaId, int expectedResponseCode) throws Exception {
        String url = String.format(utaUrl, IdConverter.idToHexString(userId)) + "/" + IdConverter.idToHexString(utaId);
        restApiCall(HTTPMethod.DELETE, url, expectedResponseCode);
    }

}
