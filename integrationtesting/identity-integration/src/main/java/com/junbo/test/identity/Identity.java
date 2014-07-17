/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.id.GroupId;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.model.Results;
import com.junbo.common.util.IdFormatter;
import com.junbo.identity.spec.v1.model.*;
import com.junbo.identity.spec.v1.model.migration.OculusInput;
import com.junbo.identity.spec.v1.model.migration.OculusOutput;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.Validator;
import com.junbo.test.common.libs.IdConverter;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dw
 */
public class Identity {

    public static final String IdentityEndPointV1 = ConfigHelper.getSetting("defaultIdentityEndPointV1");
    public static final String IdentityV1CountryURI = IdentityEndPointV1 + "/countries";
    public static final String IdentityV1CurrencyURI = IdentityEndPointV1 + "/currencies";
    public static final String IdentityV1GroupURI = IdentityEndPointV1 + "/groups";
    public static final String IdentityV1ImportsURI = IdentityEndPointV1 + "/imports";
    public static final String IdentityV1LocaleURI = IdentityEndPointV1 + "/locales";
    public static final String IdentityV1OrganizationURI = IdentityEndPointV1 + "/organizations";
    public static final String IdentityV1UserURI = IdentityEndPointV1 + "/users";
    public static final String IdentityV1UserCredentialAttemptsURI = IdentityEndPointV1 + "/credential-attempts";
    public static final String IdentityV1UserGroupMemberURI = IdentityEndPointV1 + "/user-group-memberships";
    public static final String IdentityV1UserPersonalInfoURI = IdentityEndPointV1 + "/personal-info";

    private Identity() {

    }

    public static Country CountryPostDefault() throws Exception {
        return CountryPostDefault(IdentityModel.DefaultCountry());
    }

    public static Country CountryPostDefault(Country country) throws Exception {
        return (Country) HttpclientHelper.SimpleJsonPost(
                IdentityV1CountryURI,
                JsonHelper.JsonSerializer(country),
                Country.class);
    }

    public static Country CountryGetByCountryId(String countryId) throws Exception {
        return (Country) HttpclientHelper.SimpleGet(
                IdentityV1CountryURI + "/" + countryId,
                Country.class);
    }

    public static List<Country> CountriesGet() throws Exception {
        List<Country> countries = new ArrayList<>();
        for (Object obj : HttpclientHelper.SimpleGet(IdentityV1CountryURI, (Results.class)).getItems()) {
            countries.add((Country) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj),
                            Country.class)
            );
        }
        return countries;
    }

    public static void CountryDeleteByCountryId(String countryId) throws Exception {
        HttpclientHelper.SimpleDelete(IdentityV1CountryURI + "/" + countryId);
    }

    public static Currency CurrencyPostDefault() throws Exception {
        return CurrencyPostDefault(IdentityModel.DefaultCurrency());
    }

    public static Currency CurrencyPostDefault(Currency currency) throws Exception {
        return (Currency) HttpclientHelper.SimpleJsonPost(
                IdentityV1CurrencyURI,
                JsonHelper.JsonSerializer(currency),
                Currency.class);
    }

    public static Currency CurrencyGetByCurrencyCode(String currencyCode) throws Exception {
        return (Currency) HttpclientHelper.SimpleGet(
                IdentityV1CurrencyURI + "/" + currencyCode,
                Currency.class);
    }

    public static void CurrencyDeleteByCurrencyCode(String currencyCode) throws Exception {
        HttpclientHelper.SimpleDelete(IdentityV1CurrencyURI + "/" + currencyCode);
    }

    public static Locale LocalePostDefault() throws Exception {
        return LocalePostDefault(IdentityModel.DefaultLocale());
    }

    public static Locale LocalePostDefault(Locale locale) throws Exception {
        return (Locale) HttpclientHelper.SimpleJsonPost(
                IdentityV1LocaleURI,
                JsonHelper.JsonSerializer(locale),
                Locale.class);
    }

    public static Locale LocaleGetByLocaleId(String localeId) throws Exception {
        return (Locale) HttpclientHelper.SimpleGet(
                IdentityV1LocaleURI + "/" + localeId,
                Locale.class);
    }

    public static void LocaleDeleteByLocaleId(String localeId) throws Exception {
        HttpclientHelper.SimpleDelete(IdentityV1LocaleURI + "/" + localeId);
    }

    public static User UserPostDefault() throws Exception {
        return UserPostDefault(IdentityModel.DefaultUser());
    }

    public static User UserPostDefault(User user) throws Exception {
        return (User) HttpclientHelper.SimpleJsonPost(
                IdentityV1UserURI,
                JsonHelper.JsonSerializer(user),
                User.class);
    }

    public static User UserPut(User user) throws Exception {
        return (User) HttpclientHelper.SimpleJsonPut(
                IdentityV1UserURI + "/" + IdFormatter.encodeId(user.getId()),
                JsonHelper.JsonSerializer(user),
                User.class);
    }

    public static User UserGetByUserId(UserId userId) throws Exception {
        return (User) HttpclientHelper.SimpleGet(
                IdentityV1UserURI + "/" + IdFormatter.encodeId(userId),
                User.class);
    }

    public static UserPersonalInfo UserPersonalInfoPost(UserId userId, UserPersonalInfo upi) throws Exception {
        upi.setUserId(userId);
        return (UserPersonalInfo) HttpclientHelper.SimpleJsonPost(
                IdentityV1UserPersonalInfoURI,
                JsonHelper.JsonSerializer(upi),
                UserPersonalInfo.class);
    }

    public static List<UserPersonalInfo> UserPersonalInfosGetByUserId(UserId userId) throws Exception {
        List<UserPersonalInfo> userPersonalInfos = new ArrayList<UserPersonalInfo>();
        for (Object obj : HttpclientHelper.SimpleGet(
                IdentityV1UserPersonalInfoURI + "?userId=" + IdFormatter.encodeId(userId),
                (Results.class)).getItems()) {
            userPersonalInfos.add((UserPersonalInfo) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj),
                            UserPersonalInfo.class)
            );
        }
        return userPersonalInfos;
    }

    public static UserPersonalInfo UserPersonalInfoGetByUserPersonalInfoId(UserPersonalInfoId userPersonalInfoId)
            throws Exception {
        return (UserPersonalInfo) HttpclientHelper.SimpleGet(
                IdentityV1UserPersonalInfoURI + "/" + IdFormatter.encodeId(userPersonalInfoId),
                UserPersonalInfo.class);
    }

    public static UserPersonalInfo UserPersonalInfoGetByUserEmail(String email) throws Exception {
        JsonNode jsonNode = JsonHelper.ObjectToJsonNode((HttpclientHelper.SimpleGet(
                IdentityV1UserPersonalInfoURI + "?email=" + email, (Results.class))).getItems().get(0));
        return (UserPersonalInfo) JsonHelper.JsonNodeToObject(jsonNode, UserPersonalInfo.class);
    }

    public static Organization OrganizationGetByOrganizationId(OrganizationId organizationId) throws Exception {
        return (Organization) HttpclientHelper.SimpleGet(
                IdentityV1OrganizationURI + "/" + IdFormatter.encodeId(organizationId),
                Organization.class);
    }

    public static OculusOutput ImportMigrationData(OculusInput oculusInput) throws Exception {
        return (OculusOutput) HttpclientHelper.SimpleJsonPost(IdentityV1ImportsURI,
                JsonHelper.JsonSerializer(oculusInput),
                OculusOutput.class);
    }

    public static UserCredential CredentialsGetByUserId(UserId userId) throws Exception {
        JsonNode jsonNode = JsonHelper.ObjectToJsonNode((HttpclientHelper.SimpleGet(IdentityV1UserURI
                        + "/" + IdFormatter.encodeId(userId) + "/credentials?credentialType=PASSWORD",
                (Results.class)
        ).getItems().get(0)));
        return (UserCredential) JsonHelper.JsonNodeToObject(jsonNode, UserCredential.class);
    }

    public static Group SearchOrganizationGroup(OrganizationId organizationId, String groupName) throws Exception {
        String requestURI = IdentityV1GroupURI + "?organizationId=" + IdFormatter.encodeId(organizationId)
                + "&name=" + groupName;
        JsonNode jsonNode = JsonHelper.ObjectToJsonNode(
                (HttpclientHelper.SimpleGet(requestURI, (Results.class)).getItems().get(0)));
        return ((Group) JsonHelper.JsonNodeToObject(jsonNode, Group.class));
    }

    public static UserGroup SearchUserGroup(GroupId groupId, Boolean emptyResult) throws Exception {
        String requestURI = IdentityV1UserGroupMemberURI + "?groupId=" + groupId;
        if (emptyResult) {
            Validator.Validate("validate result is empty", true,
                    HttpclientHelper.SimpleGet(requestURI, (Results.class)).getItems().isEmpty());
            return null;
        } else {
            JsonNode jsonNode = JsonHelper.ObjectToJsonNode(
                    (HttpclientHelper.SimpleGet(requestURI, (Results.class)).getItems().get(0)));
            return ((UserGroup) JsonHelper.JsonNodeToObject(jsonNode, UserGroup.class));
        }
    }

    public static Organization OrganizationPostDefault(Organization organization) throws Exception {
        Organization org = organization == null ? IdentityModel.DefaultOrganization() : organization;
        return (Organization) HttpclientHelper.SimpleJsonPost(
                IdentityV1OrganizationURI,
                JsonHelper.JsonSerializer(org),
                Organization.class);
    }

    public static CloseableHttpResponse UserCredentialPostDefault(UserId userId, String password) throws Exception {
        return UserCredentialPostDefault(userId, password, true);
    }

    public static CloseableHttpResponse UserCredentialPostDefault(
            UserId userId, String password, Boolean validResponse) throws Exception {
        UserCredential uc = IdentityModel.DefaultUserCredential(userId, password);
        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(
                IdentityV1UserURI + "/" + GetHexUserId(userId.getValue()) + "/change-credentials",
                JsonHelper.JsonSerializer(uc), HttpclientHelper.HttpRequestType.post);
        if (validResponse) {
            Validator.Validate("validate response code", 201, response.getStatusLine().getStatusCode());
        }
        return response;
    }

    public static CloseableHttpResponse UserCredentialAttemptesPostDefault(
            String userName, String password) throws Exception {
        return UserCredentialAttemptesPostDefault(userName, password, true);
    }

    public static CloseableHttpResponse UserCredentialAttemptesPostDefault(
            String userName, String password, Boolean validResponse) throws Exception {
        return UserCredentialAttemptesPostDefault(userName, password, null, validResponse);
    }

    public static CloseableHttpResponse UserCredentialAttemptesPostDefault(
            String userName, String password, String ip, Boolean validResponse) throws Exception {
        UserCredentialVerifyAttempt ucva = IdentityModel.DefaultUserCredentialAttempts(userName, password);
        if (ip != null) {
            ucva.setIpAddress(ip);
        }
        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(IdentityV1UserCredentialAttemptsURI,
                JsonHelper.JsonSerializer(ucva), HttpclientHelper.HttpRequestType.post);
        if (validResponse) {
            Validator.Validate("validate response code", 201, response.getStatusLine().getStatusCode());
        }
        return response;
    }

    public static String GetHexUserId(Long userId) throws Exception {
        return IdConverter.idToUrlString(UserId.class, userId);
    }

    // ****** start API sample logging ******
    public static final String MessageDefaultPostUser = "[Include In Sample][1] Description: Post_User_Default";
    public static final String MessageGetUserByUserId = "[Include In Sample][1] Description: Get_User_By_UserId";

    public static void StartLoggingAPISample(String message) {
        System.out.println(message);
    }
}
