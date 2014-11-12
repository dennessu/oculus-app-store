/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserSecurityQuestion;
import com.junbo.identity.spec.v1.model.UserSecurityQuestionVerifyAttempt;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.Validator;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dw
 */
public class postSecurityQuestions {

    @BeforeClass(alwaysRun = true)
    public void run() throws Exception {
        HttpclientHelper.CreateHttpClient();
        Identity.GetHttpAuthorizationHeader();
        HttpclientHelper.CloseHttpClient();
    }

    @BeforeMethod(alwaysRun = true)
    public void setup() throws Exception {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod(alwaysRun = true)
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    public void postUserSecurityQuestion() throws Exception {
        User user = Identity.UserPostDefault();

        UserSecurityQuestion usq = IdentityModel.DefaultUserSecurityQuestion();
        UserSecurityQuestion posted = Identity.UserSecurityQuestionPost(user.getId(), usq);
        UserSecurityQuestion result = Identity.UserSecurityQuestionGetById(user.getId(), posted.getId());
        Validator.Validate("validate security question", usq.getSecurityQuestion(), result.getSecurityQuestion());
        Validator.Validate("validate security answer", null, result.getAnswer());
        UserSecurityQuestionVerifyAttempt attempt = IdentityModel.DefaultUserSecurityQuestionVerifyAttempt(user.getId(), posted.getId(), usq.getAnswer());
        attempt = Identity.UserSecurityQuestionVerifyAttemptPost(user.getId(), attempt);
        Validator.Validate("validate response success", true, attempt.getSucceeded());
    }

    @Test(groups = "dailies")
    //https://oculus.atlassian.net/browse/SER-491
    public void testUserSecurityQuestionAndAttemptNotMatch() throws Exception {
        User user1 = Identity.UserPostDefault();
        User user2 = Identity.UserPostDefault();

        UserSecurityQuestion usqUser1 = IdentityModel.DefaultUserSecurityQuestion();
        UserSecurityQuestion postedUser1 = Identity.UserSecurityQuestionPost(user1.getId(), usqUser1);

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        UserSecurityQuestionVerifyAttempt attempt = IdentityModel.DefaultUserSecurityQuestionVerifyAttempt(user1.getId(), postedUser1.getId(), usqUser1.getAnswer());
        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(
                Identity.IdentityEndPointV1 + "/users/" + Identity.GetHexLongId(user1.getId().getValue()) + "/security-question-attempts",
                JsonHelper.JsonSerializer(attempt), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("Validate response code", 201, response.getStatusLine().getStatusCode());
        response.close();

        response = HttpclientHelper.GetHttpResponse(
                Identity.IdentityEndPointV1 + "/users/" + Identity.GetHexLongId(user2.getId().getValue()) + "/security-question-attempts",
                JsonHelper.JsonSerializer(attempt), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("Validate response code", 409, response.getStatusLine().getStatusCode());
        String errorMessage = "Field is not writable.";
        Validator.Validate("Validate error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();

        attempt.setUserId(user2.getId());
        response = HttpclientHelper.GetHttpResponse(
                Identity.IdentityEndPointV1 + "/users/" + Identity.GetHexLongId(user2.getId().getValue()) + "/security-question-attempts",
                JsonHelper.JsonSerializer(attempt), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("Validate response code", 400, response.getStatusLine().getStatusCode());
        errorMessage = "Field value is invalid.";
        Validator.Validate("Validate error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();
    }

    @Test(groups = "dailies")
    public void UserSecurityQuestionSearch() throws Exception {
        User user = Identity.UserPostDefault();

        UserSecurityQuestion userSecurityQuestion1 = IdentityModel.DefaultUserSecurityQuestion();
        UserSecurityQuestion userSecurityQuestion2 = IdentityModel.DefaultUserSecurityQuestion();

        Identity.UserSecurityQuestionPost(user.getId(), userSecurityQuestion1);
        Identity.UserSecurityQuestionPost(user.getId(), userSecurityQuestion2);

        Results results = Identity.UserSecurityQuestionSearch(user.getId(), null);
        assert results.getTotal() == 2;
        assert results.getItems().size() == 2;

        results = Identity.UserSecurityQuestionSearch(user.getId(), 1);
        assert results.getTotal() == 2;
        assert results.getItems().size() == 1;

        results = Identity.UserSecurityQuestionSearch(user.getId(), 0);
        assert results.getTotal() == 2;
        assert results.getItems().size() == 0;

        user = Identity.UserPostDefault();
        results = Identity.UserSecurityQuestionSearch(user.getId(), null);
        assert results.getTotal() == 0;
        assert results.getItems().size() == 0;
    }
}
