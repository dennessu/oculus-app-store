/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.authorization.spec.model.Role;
import com.junbo.common.id.RoleId;
import com.junbo.common.model.Results;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

/**
 * @author dw
 */
public class Authorization {

    public static final String AuthorizationEndPointV1 = ConfigHelper.getSetting("defaultIdentityEndpoint");
    public static final String RolesV1URI = AuthorizationEndPointV1 + "/roles";
    public static final String RoleAssignmentsV1URL = AuthorizationEndPointV1 + "/role-assignments";

    private Authorization() {

    }

    public static <T> T authorizationPost(String requestURI, String objJson, Class<T> cls) throws Exception {
        HttpPost httpPost = new HttpPost(requestURI);
        httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
        httpPost.addHeader("Authorization", Identity.httpAuthorizationHeader);
        httpPost.setEntity(new StringEntity(objJson, "utf-8"));
        return HttpclientHelper.Execute(httpPost, cls);
    }

    public static <T> T authorizationPut(String requestURI, String objJson, Class<T> cls) throws Exception {
        HttpPut httpPut = new HttpPut(requestURI);
        httpPut.addHeader("Content-Type", "application/json; charset=utf-8");
        httpPut.addHeader("Authorization", Identity.httpAuthorizationHeader);
        httpPut.setEntity(new StringEntity(objJson, "utf-8"));
        return HttpclientHelper.Execute(httpPut, cls);
    }

    public static <T> T authorizationGet(String requestURI, Class<T> cls) throws Exception {
        HttpGet httpGet = new HttpGet(requestURI);
        httpGet.addHeader("Content-Type", "application/json");
        httpGet.addHeader("Authorization", Identity.httpAuthorizationHeader);
        return HttpclientHelper.Execute(httpGet, cls);
    }

    public static void authorizationDelete(String requestURI) throws Exception {
        HttpDelete httpDelete = new HttpDelete(requestURI);
        httpDelete.addHeader("Content-Type", "application/json");
        httpDelete.addHeader("Authorization", Identity.httpAuthorizationHeader);
        HttpclientHelper.Execute(httpDelete);
    }

    public static Role rolePostDefault() throws Exception {
        return (Role) authorizationPost(RolesV1URI,
                JsonHelper.JsonSerializer(AuthorizationModel.DefaultRole()), Role.class);
    }

    public static Role rolePut(Role role) throws Exception {
        return (Role) authorizationPut(RolesV1URI + "/" + role.getId().getValue(),
                JsonHelper.JsonSerializer(role), Role.class);
    }

    public static Role roleGetByRoleId(RoleId roleId) throws Exception {
        return (Role) authorizationGet(RolesV1URI + "/" + roleId.getValue(), Role.class);
    }

    public static Role roleGetByMultiFields(Role role) throws Exception {
        JsonNode jsonNode = JsonHelper.ObjectToJsonNode((authorizationGet(RolesV1URI
                        + "?name=" + role.getName()
                        + "&targetType=" + role.getTarget().getTargetType()
                        + "&filterType=" + role.getTarget().getFilterType()
                        + "&filterLink=" + role.getTarget().getFilterLink().getHref(),
                (Results.class)).getItems().get(0)));
        return (Role) JsonHelper.JsonNodeToObject(jsonNode, Role.class);
    }
}
