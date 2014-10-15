/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Group;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.test.common.*;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dw
 */
public class postOrganization {

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
    public void postOrganization() throws Exception {
        Organization org = IdentityModel.DefaultOrganization();
        Organization posted = Identity.OrganizationPostDefault(org);
        Validator.Validate("validate organization name is correct", true,
                org.getName().equals(posted.getName()));
    }

    @Test(groups = "dailies")
    //https://oculus.atlassian.net/browse/SER-441
    public void postOrganizationDuplicateNameIsValidatedTrue() throws Exception {
        Organization org = IdentityModel.DefaultOrganization();
        org.setIsValidated(true);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(Identity.IdentityV1OrganizationURI,
                JsonHelper.JsonSerializer(org), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response error code", 400, response.getStatusLine().getStatusCode());
        String errorMessage = "Can't create validated organization";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();

        org.setIsValidated(false);
        Organization posted = Identity.OrganizationPostDefault(org);
        posted.setIsValidated(true);
        Identity.OrganizationPut(posted);
        org.setIsValidated(RandomHelper.randomBoolean());
        response = HttpclientHelper.GetHttpResponse(Identity.IdentityV1OrganizationURI,
                JsonHelper.JsonSerializer(org), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response error code", 409, response.getStatusLine().getStatusCode());
        errorMessage = "Field value is duplicate";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();
    }

    @Test(groups = "dailies")
    public void testOrganizationSearch() throws Exception {
        Organization org = IdentityModel.DefaultOrganization();
        for (int i = 0; i < 10; i++) {
            Identity.OrganizationPostDefault(org);
        }
        Results<Organization> results = Identity.OrganizationByName(org.getName(), null, null);
        Validator.Validate("validate organization with no parameter", 10, results.getItems().size());
        Validator.Validate("validate total organization size", 10L, results.getTotal());
        Validator.Validate("validate organization next null", true, results.getNext() == null);

        results = Identity.OrganizationByName(org.getName(), 5, 15);
        Validator.Validate("validate organization with offset", 0, results.getItems().size());
        Validator.Validate("validate total organization size", 10L, results.getTotal());
        Validator.Validate("validate organization next null", true, results.getNext() == null);

        results = Identity.OrganizationByName(org.getName(), 5, 5);
        Validator.Validate("validate organization with offset and count", 5, results.getItems().size());
        Validator.Validate("validate total organization size", 10L, results.getTotal());
        Validator.Validate("validate organization next null", true, results.getNext() == null);

        results = Identity.OrganizationByName(org.getName() + "Fake", 0, 0);
        Validator.Validate("validate organization with wrong name", 0, results.getItems().size());
        Validator.Validate("validate total organization size", 0L, results.getTotal());

        results = Identity.OrganizationByName(org.getName(), 1, 2);
        Validator.Validate("validate total organization size", 10L, results.getTotal());
        Validator.Validate("validate organization count", true, results.getNext().getHref().contains("count=1"));
        Validator.Validate("validate organization cursor", true, results.getNext().getHref().contains("cursor=3"));
    }

    @Test(groups = "dailies")
    //https://oculus.atlassian.net/browse/SER-610
    //https://oculus.atlassian.net/browse/SER-605
    public void testGroupDelete() throws Exception {
        Organization org = IdentityModel.DefaultOrganization();
        org = Identity.OrganizationPostDefault(org);

        Group group = IdentityModel.DefaultGroup(org.getId());

        group = Identity.GroupPostDefault(group);

        Identity.GroupDelete(group);
    }

    @Test(groups = "dailies")
    // https://oculus.atlassian.net/browse/SER-683
    public void testOrganizationGetAll() throws Exception {
        Organization org = IdentityModel.DefaultOrganization();
        for (int i = 0; i < 10; i++) {
            Identity.OrganizationPostDefault(org);
        }

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(Identity.IdentityV1OrganizationURI,
                null, HttpclientHelper.HttpRequestType.get, nvps);
        Validator.Validate("validate response error code", 400, response.getStatusLine().getStatusCode());
        String errorMessage = "Query parameter is required";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();

        String originalHeader = Identity.httpAuthorizationHeader;
        Identity.httpAuthorizationHeader = getCurlToolAuthorizationHeader();
        Results<Organization> results = Identity.OrganizationGetAll(null, null);
        Validator.Validate("validate organization count", true, results.getItems().size() >= 10);
        Validator.Validate("validate organization total count", true, results.getTotal() >= 10);
        Validator.Validate("validate organization next null", true, results.getNext() == null);

        results = Identity.OrganizationGetAll(1, null);
        Validator.Validate("validate organization count", true, results.getItems().size() == 1);
        Validator.Validate("validate organization total count", true, results.getTotal() >= 10);
        Validator.Validate("validate organization next cursor not null", true, results.getNext().getHref().contains("cursor=1"));
        Validator.Validate("validate organization next count not null", true, results.getNext().getHref().contains("count=1"));

        results = Identity.OrganizationGetAll(1, 2);
        Validator.Validate("validate organization count", true, results.getItems().size() == 1);
        Validator.Validate("validate organization total count", true, results.getTotal() >= 10);
        Validator.Validate("validate organization next cursor not null", true, results.getNext().getHref().contains("cursor=3"));
        Validator.Validate("validate organization next count not null", true, results.getNext().getHref().contains("count=1"));

        Identity.httpAuthorizationHeader = originalHeader;
    }

    public static String getCurlToolAuthorizationHeader() throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("grant_type", "client_credentials"));
        nvps.add(new BasicNameValuePair("client_id", "curationTool"));
        nvps.add(new BasicNameValuePair("client_secret", "secret"));
        nvps.add(new BasicNameValuePair("scope", "identity organization.group.admin"));

        HttpPost httpPost = new HttpPost(ConfigHelper.getSetting("defaultOauthEndpoint") + "/oauth2/token");
        httpPost.addHeader("oculus-internal", String.valueOf(true));
        httpPost.setConfig(RequestConfig.custom().setRedirectsEnabled(true).build());
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        CloseableHttpResponse response = HttpclientHelper.Execute(httpPost);

        String[] results = EntityUtils.toString(response.getEntity(), "UTF-8").split(",");
        for (String s : results) {
            if (s.contains("access_token")) {
                return "Bearer" + s.split(":")[1].replace("\"", "");
            }
        }

        return null;
    }

    @Test(groups = "dailies")
    public void testOrganizationUnicode() throws Exception {
        Organization organization = IdentityModel.DefaultOrganization();
        organization.setName("赵云�testcase" + RandomHelper.randomAlphabetic(15));
        organization = Identity.OrganizationPostDefault(organization);

        Organization newOrg = Identity.OrganizationGetByOrganizationId(organization.getId());
        assert newOrg.getName().equalsIgnoreCase(organization.getName());
    }
}
