/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.ClientId;
import com.junbo.common.id.UserCredentialVerifyAttemptId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserCredentialVerifyAttempt extends PropertyAssignedAwareResourceMeta<UserCredentialVerifyAttemptId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] The Link to user credential attempt resource.")
    @JsonProperty("self")
    private UserCredentialVerifyAttemptId id;

    @ApiModelProperty(position = 2, required = false, value = "The user resource for the credential attempt.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 3, required = true, value = "his is required during PIN/Password/Email login. If the user typed a username & password, " +
            "this will be the (username part) actual text as typed by the end-user. If the user typed an email & password, " +
            "this will be the (email part) actual text as typed by the end-user. In any case, " +
            "it is not necessarily the same as the username associated with the \"user\" link.")
    private String username;

    @ApiModelProperty(position = 4, required = true,
            value = "When internal component call \"POST\" to submit a credential attempt, this field will be raw pin or password as typed by the end user. " +
                    "When client call Get credential attempt from REST API, this attribute will not be available in the response.")
    private String value;

    @ApiModelProperty(position = 5, required = true, value = "The credential type, must be in [PASSWORD, PIN].")
    private String type;

    @ApiModelProperty(position = 6, required = false, value = "[Nullable]The client ip of the verify attempt caller. When do POST request, " +
            "client side can decide whether to provide ipAddress. For browsers, it often provides ipAddress, but for non-Browser client, " +
            "client may choose to not provide the ipAddress. So when do the GET request, server will return the information in the record. " +
            "This value will be NULL if the original POST request doesn't contain this info")
    private String ipAddress;

    @ApiModelProperty(position = 7, required = false, value = "[Nullable]The user agent of the verify attempt caller, same as ipAddress, " +
            "client side may choose to not provide userAgent information.")
    private String userAgent;

    @ApiModelProperty(position = 8, required = false, value = "Link to the Client ID of the Verify attempt caller. " +
            "Client ID is the identifier of an OAuth Client. " +
            "When developer submit a base game item, he will get a unique Client ID string to identify what this game is. " +
            "Developer need to later embed this unique Client ID string in his game binary, and always pass this Client ID when log on to Oculus platform. " +
            "In Credential Verify Attempt, we record the logon attempts, and save the Client ID in this attribute. " +
            "[Please note: in the base game case, a client ID has 1:1 mapping with a base game item ID. " +
            "There are other cases that client ID has no corresponding Item (eg. Oculus dev center), " +
            "and Item has no corresponding Client ID (eg. In App Purchase Item)].")
    private ClientId clientId;

    @ApiModelProperty(position = 9, required = false,
            value = "[Nullable]Whether the verify attempt is succeed. When do a POST call internally, the object will be null as " +
                    "client side shouldn't pass in this value. When do a GET call from REST API, it will either be TRUE or FALSE.")
    @JsonProperty("wasSuccessful")
    private Boolean succeeded;

    public UserCredentialVerifyAttemptId getId() {
        return id;
    }

    public void setId(UserCredentialVerifyAttemptId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
        support.setPropertyAssigned("user");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        support.setPropertyAssigned("username");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        support.setPropertyAssigned("value");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        support.setPropertyAssigned("type");
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        support.setPropertyAssigned("ipAddress");
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        support.setPropertyAssigned("userAgent");
    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
        support.setPropertyAssigned("clientId");
    }

    public Boolean getSucceeded() {
        return succeeded;
    }

    public void setSucceeded(Boolean succeeded) {
        this.succeeded = succeeded;
        support.setPropertyAssigned("succeeded");
        support.setPropertyAssigned("wasSuccessful");
    }
}
