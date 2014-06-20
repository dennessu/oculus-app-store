/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.Validator;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author dw
 */
public class postOrganization {

    @BeforeMethod
    public void setup() {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    public void postOrganization() throws Exception {
        Organization org = Identity.OrganizationPostDefault(null);
    }

    @Test(groups = "dailies")
    public void postOrganizationDuplicateNameIsValidatedTrue() throws Exception {
        Organization org = IdentityModel.DefaultOrganization();
        Identity.OrganizationPostDefault(org);
        org.setIsValidated(IdentityModel.RandomBoolean());
        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(Identity.DefaultIdentityV1OrganizationURI,
                JsonHelper.JsonSerializer(org), 2);
        Validator.Validate("validate response error code", 409, response.getStatusLine().getStatusCode());
        String errorMessage = "Field name duplicate.";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
    }

}
