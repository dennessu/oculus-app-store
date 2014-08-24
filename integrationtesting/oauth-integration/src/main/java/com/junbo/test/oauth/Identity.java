/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.oauth;

import com.junbo.identity.spec.v1.model.migration.OculusInput;
import com.junbo.identity.spec.v1.model.migration.OculusOutput;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiali_000 on 2014/8/24.
 */
public class Identity {
    public static final String IdentityEndPointV1 = ConfigHelper.getSetting("defaultIdentityEndPointV1");
    public static final String IdentityV1ImportsURI = IdentityEndPointV1 + "/imports";
    public static String httpAuthorizationHeader = "";

    public static OculusOutput ImportMigrationData(OculusInput oculusInput) throws Exception {
        return (OculusOutput) IdentityPost(IdentityV1ImportsURI,
                JsonHelper.JsonSerializer(oculusInput),
                OculusOutput.class);
    }

    public static <T> T IdentityPost(String requestURI, String objJson, Class<T> cls) throws Exception {
        HttpPost httpPost = new HttpPost(requestURI);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("Authorization", httpAuthorizationHeader);
        httpPost.setEntity(new StringEntity(objJson));
        return HttpclientHelper.SimpleHttpPost(httpPost, cls);
    }

    public static void GetHttpAuthorizationHeaderForMigration() throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("grant_type", "client_credentials"));
        nvps.add(new BasicNameValuePair("client_id", "migration"));
        nvps.add(new BasicNameValuePair("client_secret", "secret"));
        nvps.add(new BasicNameValuePair("scope", "identity.service identity.migration"));
        CloseableHttpResponse response = HttpclientHelper.SimplePost(ConfigHelper.getSetting("defaultTokenURI"), nvps);
        String[] results = EntityUtils.toString(response.getEntity(), "UTF-8").split(",");
        for (String s : results) {
            if (s.contains("access_token")) {
                httpAuthorizationHeader = "Bearer" + s.split(":")[1].replace("\"", "");
                break;
            }
        }
    }
}
