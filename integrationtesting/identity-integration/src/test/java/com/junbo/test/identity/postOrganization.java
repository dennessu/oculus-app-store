/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Group;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.RandomHelper;
import com.junbo.test.common.Validator;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
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
        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1OrganizationURI,
                JsonHelper.JsonSerializer(org), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response error code", 400, response.getStatusLine().getStatusCode());
        String errorMessage = "Can't create validated organization";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));

        org.setIsValidated(false);
        Organization posted = Identity.OrganizationPostDefault(org);
        posted.setIsValidated(true);
        Identity.OrganizationPut(posted);
        org.setIsValidated(RandomHelper.randomBoolean());
        response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1OrganizationURI,
                JsonHelper.JsonSerializer(org), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response error code", 409, response.getStatusLine().getStatusCode());
        errorMessage = "Field value is duplicate";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
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
    public void testOrganizationUnicode() throws Exception {
        Organization organization = IdentityModel.DefaultOrganization();
        organization.setName("赵云龙&testcase" + RandomHelper.randomAlphabetic(15));
        organization = Identity.OrganizationPostDefault(organization);

        Organization newOrg = Identity.OrganizationGetByOrganizationId(organization.getId());
        assert newOrg.getName().equalsIgnoreCase(organization.getName());
    }
}
