/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.id.*;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.common.model.Results;
import com.junbo.common.util.IdFormatter;
import com.junbo.identity.spec.v1.model.*;
import com.junbo.identity.spec.v1.model.migration.OculusInput;
import com.junbo.identity.spec.v1.model.migration.OculusOutput;
import com.junbo.test.common.*;
import com.junbo.test.common.libs.IdConverter;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author dw
 */
public class Identity {

    public static final String IdentityEndPointV1 = ConfigHelper.getSetting("defaultIdentityEndpoint");
    public static final String IdentityV1CountryURI = IdentityEndPointV1 + "/countries";
    public static final String IdentityV1CurrencyURI = IdentityEndPointV1 + "/currencies";
    public static final String IdentityV1DeviceTypeURI = IdentityEndPointV1 + "/device-types";
    public static final String IdentityV1ErrorInfoURI = IdentityEndPointV1 + "/error-info";
    public static final String IdentityV1CommunicationURI = IdentityEndPointV1 + "/communications";
    public static final String IdentityV1GroupURI = IdentityEndPointV1 + "/groups";
    public static final String IdentityV1ImportsURI = IdentityEndPointV1 + "/imports";
    public static final String IdentityV1LocaleURI = IdentityEndPointV1 + "/locales";
    public static final String IdentityV1OrganizationURI = IdentityEndPointV1 + "/organizations";
    public static final String IdentityV1UserURI = IdentityEndPointV1 + "/users";
    public static final String IdentityV1UserCredentialAttemptsURI = IdentityEndPointV1 + "/credential-attempts";
    public static final String IdentityV1UserGroupMemberURI = IdentityEndPointV1 + "/user-group-memberships";
    public static final String IdentityV1UserPersonalInfoURI = IdentityEndPointV1 + "/personal-info";

    public static String httpAuthorizationHeader = "";

    private Identity() {

    }

    public static <T> T IdentityPost(String requestURI, String objJson, Class<T> cls) throws Exception {
        HttpPost httpPost = new HttpPost(requestURI);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("Authorization", httpAuthorizationHeader);
        httpPost.setEntity(new StringEntity(objJson));
        return HttpclientHelper.SimpleHttpPost(httpPost, cls);
    }

    public static <T> T IdentityPut(String requestURI, String objJson, Class<T> cls) throws Exception {
        HttpPut httpPut = new HttpPut(requestURI);
        httpPut.addHeader("Content-Type", "application/json");
        httpPut.addHeader("Authorization", httpAuthorizationHeader);
        httpPut.setEntity(new StringEntity(objJson));
        return HttpclientHelper.SimpleHttpPut(httpPut, cls);
    }

    public static <T> T IdentityGet(String requestURI, Class<T> cls) throws Exception {
        HttpGet httpGet = new HttpGet(requestURI);
        httpGet.addHeader("Content-Type", "application/json");
        httpGet.addHeader("Authorization", httpAuthorizationHeader);
        return HttpclientHelper.SimpleHttpGet(httpGet, cls);
    }

    public static void IdentityDelete(String requestURI) throws Exception {
        HttpDelete httpDelete = new HttpDelete(requestURI);
        httpDelete.addHeader("Content-Type", "application/json");
        httpDelete.addHeader("Authorization", httpAuthorizationHeader);
        HttpclientHelper.SimpleHttpDelete(httpDelete);
    }

    public static void GetHttpAuthorizationHeaderForMigration() throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("grant_type", "client_credentials"));
        nvps.add(new BasicNameValuePair("client_id", "migration"));
        nvps.add(new BasicNameValuePair("client_secret", "secret"));
        nvps.add(new BasicNameValuePair("scope", "identity.service identity.migration"));
        CloseableHttpResponse response = HttpclientHelper.SimplePost(
                ConfigHelper.getSetting("defaultOauthEndpoint") + "/oauth2/token", nvps);
        String[] results = EntityUtils.toString(response.getEntity(), "UTF-8").split(",");
        for (String s : results) {
            if (s.contains("access_token")) {
                httpAuthorizationHeader = "Bearer" + s.split(":")[1].replace("\"", "");
                break;
            }
        }
    }

    public static void GetHttpAuthorizationHeader() throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("grant_type", "client_credentials"));
        nvps.add(new BasicNameValuePair("client_id", "service"));
        nvps.add(new BasicNameValuePair("client_secret", "secret"));
        nvps.add(new BasicNameValuePair("scope", "identity.service identity.admin"));
        CloseableHttpResponse response = HttpclientHelper.SimplePost(
                ConfigHelper.getSetting("defaultOauthEndpoint") + "/oauth2/token", nvps);
        String[] results = EntityUtils.toString(response.getEntity(), "UTF-8").split(",");
        for (String s : results) {
            if (s.contains("access_token")) {
                httpAuthorizationHeader = "Bearer" + s.split(":")[1].replace("\"", "");
                break;
            }
        }
    }

    public static void SetHttpAuthorizationHeader(String accessToken) {
        httpAuthorizationHeader = "Bearer " + accessToken;
    }

    public static Country CountryPostDefault() throws Exception {
        return CountryPostDefault(IdentityModel.DefaultCountry());
    }

    public static Country CountryPostDefault(Country country) throws Exception {
        return (Country) IdentityPost(IdentityV1CountryURI, JsonHelper.JsonSerializer(country), Country.class);
    }

    public static Country CountryGetByCountryId(String countryId, String locale) throws Exception {
        if (StringUtils.isEmpty(locale)) {
            return CountryGetByCountryId(countryId);
        } else {
            return IdentityGet(IdentityV1CountryURI + "/" + countryId + "?locale=" + locale, Country.class);
        }
    }

    public static Country CountryGetByCountryId(String countryId) throws Exception {
        return (Country) IdentityGet(IdentityV1CountryURI + "/" + countryId, Country.class);
    }

    public static List<Country> CountriesGet() throws Exception {
        List<Country> countries = new ArrayList<>();
        for (Object obj : IdentityGet(IdentityV1CountryURI, (Results.class)).getItems()) {
            countries.add((Country) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj),
                            Country.class)
            );
        }
        return countries;
    }

    public static void CountryDeleteByCountryId(String countryId) throws Exception {
        IdentityDelete(IdentityV1CountryURI + "/" + countryId);
    }

    public static Currency CurrencyPostDefault() throws Exception {
        return CurrencyPostDefault(IdentityModel.DefaultCurrency());
    }

    public static Currency CurrencyPostDefault(Currency currency) throws Exception {
        return (Currency) IdentityPost(IdentityV1CurrencyURI, JsonHelper.JsonSerializer(currency), Currency.class);
    }

    public static Currency CurrencyGetByCurrencyCode(String currencyCode, String locale) throws Exception {
        if (StringUtils.isEmpty(locale)) {
            return CurrencyGetByCurrencyCode(currencyCode);
        } else {
            return IdentityGet(IdentityV1CurrencyURI + "/" + currencyCode + "?locale=" + locale, Currency.class);
        }
    }

    public static Currency CurrencyGetByCurrencyCode(String currencyCode) throws Exception {
        return (Currency) IdentityGet(IdentityV1CurrencyURI + "/" + currencyCode, Currency.class);
    }

    public static void CurrencyDeleteByCurrencyCode(String currencyCode) throws Exception {
        IdentityDelete(IdentityV1CurrencyURI + "/" + currencyCode);
    }

    public static Locale LocalePostDefault() throws Exception {
        return LocalePostDefault(IdentityModel.DefaultLocale());
    }

    public static Locale LocalePostDefault(Locale locale) throws Exception {
        return (Locale) IdentityPost(IdentityV1LocaleURI, JsonHelper.JsonSerializer(locale), Locale.class);
    }

    public static Locale LocaleGetByLocaleId(String localeId) throws Exception {
        return (Locale) IdentityGet(IdentityV1LocaleURI + "/" + localeId, Locale.class);
    }

    public static Results<Locale> LocaleGetAll() throws Exception {
        Results<Locale> results = new Results<>();
        results.setItems(new ArrayList<Locale>());
        Results res = IdentityGet(IdentityV1LocaleURI, Results.class);
        for (Object obj : res.getItems()) {
            results.getItems().add((Locale) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj),
                            Locale.class)
            );
        }
        results.setTotal(res.getTotal());
        results.setNext(res.getNext());
        results.setSelf(res.getSelf());

        return results;
    }

    public static void LocaleDeleteByLocaleId(String localeId) throws Exception {
        IdentityDelete(IdentityV1LocaleURI + "/" + localeId);
    }

    public static User UserPostDefaultWithMail(Integer nameLength, String email) throws Exception {
        User user = UserPostDefault(nameLength);
        Email mailPii = new Email();
        mailPii.setInfo(email);
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(mailPii));
        userPersonalInfo.setUserId(user.getId());
        userPersonalInfo.setType(IdentityModel.UserPersonalInfoType.EMAIL.toString());
        UserPersonalInfo pii = UserPersonalInfoPost(user.getId(), userPersonalInfo);
        pii.setLastValidateTime(new Date());
        pii = UserPersonalInfoPut(user.getId(), pii);
        UserPersonalInfoLink link = new UserPersonalInfoLink();
        link.setValue(pii.getId());
        link.setLabel(RandomHelper.randomAlphabetic(15));
        link.setUserId(user.getId());
        link.setIsDefault(true);
        List<UserPersonalInfoLink> links = new ArrayList<>();
        links.add(link);
        user.setEmails(links);
        return UserPut(user);
    }

    public static User UserPostDefault(Integer nameLength) throws Exception {
        User user = UserPostDefault(IdentityModel.DefaultUser());
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setUserId(user.getId());
        userPersonalInfo.setType("USERNAME");
        UserLoginName loginName = new UserLoginName();
        loginName.setUserName(RandomHelper.randomAlphabetic(nameLength));
        userPersonalInfo.setValue(ObjectMapperProvider.instance().valueToTree(loginName));
        UserPersonalInfo loginInfo = UserPersonalInfoPost(user.getId(), userPersonalInfo);
        user.setIsAnonymous(false);
        user.setUsername(loginInfo.getId());
        return UserPut(user);
    }

    public static User UserPostDefault() throws Exception {
        return UserPostDefault(15);
    }

    public static User UserPostDefault(User user) throws Exception {
        return (User) IdentityPost(IdentityV1UserURI, JsonHelper.JsonSerializer(user), User.class);
    }

    public static User UserPut(User user) throws Exception {
        return (User) IdentityPut(IdentityV1UserURI + "/" + IdFormatter.encodeId(user.getId()),
                JsonHelper.JsonSerializer(user), User.class);
    }

    public static void UserDelete(User user) throws Exception {
        IdentityDelete(IdentityV1UserURI + "/" + IdFormatter.encodeId(user.getId()));
    }

    public static User UserGetByUserId(UserId userId) throws Exception {
        return (User) IdentityGet(IdentityV1UserURI + "/" + IdFormatter.encodeId(userId), User.class);
    }

    public static UserPersonalInfo UserPersonalInfoPost(UserId userId, UserPersonalInfo upi) throws Exception {
        upi.setUserId(userId);
        return (UserPersonalInfo) IdentityPost(IdentityV1UserPersonalInfoURI,
                JsonHelper.JsonSerializer(upi), UserPersonalInfo.class);
    }

    public static UserPersonalInfo UserPersonalInfoPut(UserId userId, UserPersonalInfo upi) throws Exception {
        return IdentityPut(IdentityV1UserPersonalInfoURI + "/" + GetHexLongId(upi.getId().getValue()),
                JsonHelper.JsonSerializer(upi), UserPersonalInfo.class);
    }

    public static List<UserPersonalInfo> UserPersonalInfosGetByUserId(UserId userId) throws Exception {
        List<UserPersonalInfo> userPersonalInfos = new ArrayList<UserPersonalInfo>();
        for (Object obj : IdentityGet(
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
        return (UserPersonalInfo) IdentityGet(
                IdentityV1UserPersonalInfoURI + "/" + IdFormatter.encodeId(userPersonalInfoId),
                UserPersonalInfo.class);
    }

    public static UserPersonalInfo UserPersonalInfoGetByUserEmail(String email) throws Exception {
        String requestURI = IdentityV1UserPersonalInfoURI + "?email=" + URLEncoder.encode(email, "UTF-8");
        JsonNode jsonNode = JsonHelper.ObjectToJsonNode((IdentityGet(
                requestURI, (Results.class))).getItems().get(0));
        return (UserPersonalInfo) JsonHelper.JsonNodeToObject(jsonNode, UserPersonalInfo.class);
    }

    public static UserTFA UserTFAPost(UserId userId, UserTFA userTFA) throws Exception {
        return (UserTFA) IdentityPost(IdentityV1UserURI + "/" + GetHexLongId(userId.getValue()) + "/tfa",
                JsonHelper.JsonSerializer(userTFA),
                UserTFA.class);
    }

    public static Organization OrganizationGetByOrganizationId(OrganizationId organizationId) throws Exception {
        return (Organization) IdentityGet(
                IdentityV1OrganizationURI + "/" + IdFormatter.encodeId(organizationId),
                Organization.class);
    }

    public static OculusOutput ImportMigrationData(OculusInput oculusInput) throws Exception {
        return (OculusOutput) IdentityPost(IdentityV1ImportsURI,
                JsonHelper.JsonSerializer(oculusInput),
                OculusOutput.class);
    }

    public static UserCredential CredentialsGetByUserId(UserId userId) throws Exception {
        JsonNode jsonNode = JsonHelper.ObjectToJsonNode((IdentityGet(IdentityV1UserURI
                        + "/" + IdFormatter.encodeId(userId) + "/credentials?credentialType=PASSWORD",
                (Results.class)).getItems().get(0)));
        return (UserCredential) JsonHelper.JsonNodeToObject(jsonNode, UserCredential.class);
    }

    public static Group SearchOrganizationGroup(OrganizationId organizationId, String groupName) throws Exception {
        String requestURI = IdentityV1GroupURI + "?organizationId=" + IdFormatter.encodeId(organizationId)
                + "&name=" + groupName;
        JsonNode jsonNode = JsonHelper.ObjectToJsonNode(
                (IdentityGet(requestURI, (Results.class)).getItems().get(0)));
        return ((Group) JsonHelper.JsonNodeToObject(jsonNode, Group.class));
    }

    public static UserGroup SearchUserGroup(GroupId groupId, Boolean emptyResult) throws Exception {
        String requestURI = IdentityV1UserGroupMemberURI + "?groupId=" + groupId;
        if (emptyResult) {
            Validator.Validate("validate result is empty", true,
                    IdentityGet(requestURI, (Results.class)).getItems().isEmpty());
            return null;
        } else {
            JsonNode jsonNode = JsonHelper.ObjectToJsonNode(
                    (IdentityGet(requestURI, (Results.class)).getItems().get(0)));
            return ((UserGroup) JsonHelper.JsonNodeToObject(jsonNode, UserGroup.class));
        }
    }

    public static Organization OrganizationPostDefault(Organization organization) throws Exception {
        Organization org = organization == null ? IdentityModel.DefaultOrganization() : organization;
        return (Organization) IdentityPost(IdentityV1OrganizationURI,
                JsonHelper.JsonSerializer(org), Organization.class);
    }

    public static Organization OrganizationPut(Organization organization) throws Exception {
        return (Organization) IdentityPut(
                IdentityV1OrganizationURI + "/" + GetHexLongId(organization.getId().getValue()),
                JsonHelper.JsonSerializer(organization), Organization.class);
    }

    public static Results<Organization> OrganizationByName(String name, Integer limit, Integer offset) throws Exception {
        return IdentityGet(
                IdentityV1OrganizationURI + "?name=" + name.toLowerCase() + buildIdentityCount(limit) + buildIdentityCursor(offset), Results.class);
    }

    public static DeviceType DeviceTypeDefault(DeviceType deviceType) throws Exception {
        DeviceType type = deviceType == null ? IdentityModel.DefaultDeviceType(null) : deviceType;
        return IdentityPost(IdentityV1DeviceTypeURI, JsonHelper.JsonSerializer(type), DeviceType.class);
    }

    public static DeviceType DeviceTypePut(DeviceType deviceType) throws Exception {
        return IdentityPut(IdentityV1DeviceTypeURI + "/" + deviceType.getTypeCode(),
                JsonHelper.JsonSerializer(deviceType), DeviceType.class);
    }

    public static DeviceType DeviceTypeGet(String deviceTypeCode) throws Exception {
        return IdentityGet(IdentityV1DeviceTypeURI + "/" + deviceTypeCode, DeviceType.class);
    }

    public static Results<DeviceType> DeviceTypeGetAll() throws Exception {
        Results<DeviceType> results = new Results<>();
        results.setItems(new ArrayList<DeviceType>());
        Results res = IdentityGet(IdentityV1DeviceTypeURI, Results.class);
        for (Object obj : res.getItems()) {
            results.getItems().add((DeviceType) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj),
                            DeviceType.class)
            );
        }
        results.setTotal(res.getTotal());
        results.setNext(res.getNext());
        results.setSelf(res.getSelf());

        return results;
    }

    public static void DeviceTypeDelete(String deviceTypeCode) throws Exception {
        IdentityDelete(IdentityV1DeviceTypeURI + "/" + deviceTypeCode);
    }

    public static ErrorInfo ErrorInfoDefault(ErrorInfo errorInfo) throws Exception {
        ErrorInfo info = errorInfo == null ? IdentityModel.DefaultErrorInfo() : errorInfo;
        return IdentityPost(IdentityV1ErrorInfoURI, JsonHelper.JsonSerializer(info), ErrorInfo.class);
    }

    public static ErrorInfo ErrorInfoPut(ErrorInfo errorInfo) throws Exception {
        return IdentityPut(IdentityV1ErrorInfoURI + "/" + errorInfo.getErrorIdentifier(), JsonHelper.JsonSerializer(errorInfo), ErrorInfo.class);
    }

    public static ErrorInfo ErrorInfoGet(String errorIdentifier) throws Exception {
        return IdentityGet(IdentityV1ErrorInfoURI + "/" + errorIdentifier, ErrorInfo.class);
    }

    public static Results<ErrorInfo> ErrorInfoGetAll() throws Exception {
        Results<ErrorInfo> results = new Results<>();
        results.setItems(new ArrayList<ErrorInfo>());
        Results res = IdentityGet(IdentityV1ErrorInfoURI, Results.class);
        for (Object obj : res.getItems()) {
            results.getItems().add((ErrorInfo) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj),
                    ErrorInfo.class));
        }

        results.setTotal(res.getTotal());
        results.setNext(res.getNext());
        results.setSelf(res.getSelf());
        return results;
    }

    public static Communication CommunicationDefault(Communication communication) throws Exception {
        Communication newCommunication = communication == null ? IdentityModel.DefaultCommunication() : communication;
        return IdentityPost(IdentityV1CommunicationURI, JsonHelper.JsonSerializer(communication), Communication.class);
    }

    public static Communication CommunicationPut(Communication communication) throws Exception {
        return IdentityPut(IdentityV1CommunicationURI + "/" + communication.getId().toString(), JsonHelper.JsonSerializer(communication), Communication.class);
    }

    public static Communication CommunicationGet(String communicationId, String locale) throws Exception {
        return IdentityGet(IdentityV1CommunicationURI + "/" + communicationId + buildCommunicationLocale(locale), Communication.class);
    }

    public static Results<Communication> CommunicationSearch(String region, String translation) throws Exception {
        Results<Communication> results = new Results<>();
        results.setItems(new ArrayList<Communication>());
        Results res = IdentityGet(IdentityV1CommunicationURI + buildCommunicationQueryUrl(region, translation), Results.class);
        for (Object obj : res.getItems()) {
            results.getItems().add((Communication) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj),
                    Communication.class));
        }

        results.setTotal(res.getTotal());
        results.setNext(res.getNext());
        results.setSelf(res.getSelf());
        return results;
    }

    public static String buildCommunicationLocale(String locale) {
        if (StringUtils.isEmpty(locale)) {
            return "";
        } else {
            return "?locale=" + locale;
        }
    }

    public static String buildCommunicationQueryUrl(String region, String translation) {
        if (StringUtils.isEmpty(region) && StringUtils.isEmpty(translation)) {
            return "";
        } else if (!StringUtils.isEmpty(region) && !StringUtils.isEmpty(translation)) {
            return "?region=" + region + "&translation=" + translation;
        } else if (!StringUtils.isEmpty(region)) {
            return "?region=" + region;
        } else {
            return "?translation=" + translation;
        }
    }

    public static List<UserCredentialVerifyAttempt> UserCredentialAttemptList(UserId userId, String credentialType) throws Exception {
        List<UserCredentialVerifyAttempt> attempts = new ArrayList<>();
        for (Object obj : IdentityGet(
                IdentityV1UserCredentialAttemptsURI + "?userId=" + GetHexLongId(userId.getValue()) + "&credentialType=" + credentialType, (Results.class)).getItems()) {
            attempts.add((UserCredentialVerifyAttempt) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj),
                    UserCredentialVerifyAttempt.class)
            );
        }
        return attempts;
    }

    public static List<UserCredential> UserCredentialList(UserId userId, String credentialType) throws Exception {
        List<UserCredential> credentials = new ArrayList<>();
        for (Object obj : IdentityGet(
                IdentityEndPointV1 + "/users/" + GetHexLongId(userId.getValue()) + "/credentials?credentialType=" + credentialType, (Results.class)).getItems()) {
            credentials.add((UserCredential) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj), UserCredential.class)
            );
        }
        return credentials;
    }

    public static CloseableHttpResponse UserCredentialPostDefault(UserId userId, String oldPassword, String password) throws Exception {
        return UserCredentialPostDefault(userId, oldPassword, password, true);
    }

    public static CloseableHttpResponse UserCredentialPostDefault(
            UserId userId, String oldPassword, String password, Boolean validResponse) throws Exception {
        UserCredential uc = IdentityModel.DefaultUserCredential(userId, oldPassword, password);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("Authorization", httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(
                IdentityV1UserURI + "/" + GetHexLongId(userId.getValue()) + "/change-credentials",
                JsonHelper.JsonSerializer(uc), HttpclientHelper.HttpRequestType.post, nvps);
        if (validResponse) {
            Validator.Validate("validate response code", 201, response.getStatusLine().getStatusCode());
        }
        return response;
    }

    public static CloseableHttpResponse UserPinCredentialPostDefault(UserId userId, String oldPassword,
                                                                     String pin, Boolean validReponse) throws Exception {
        UserCredential pinCredential = IdentityModel.DefaultUserPin(userId, oldPassword, pin);
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(
                IdentityV1UserURI + "/" + GetHexLongId(userId.getValue()) + "/change-credentials",
                JsonHelper.JsonSerializer(pinCredential), HttpclientHelper.HttpRequestType.post, nvps);
        if (validReponse) {
            Validator.Validate("validate response code", 201, response.getStatusLine().getStatusCode());
        }

        return response;
    }

    public static CloseableHttpResponse UserCredentialAttemptesPostDefault(
            String userName, String password) throws Exception {
        return UserCredentialAttemptesPostDefault(userName, password, true);
    }

    public static CloseableHttpResponse UserPinCredentialAttemptPostDefault(String username, String pin) throws Exception {
        return UserPinCredentialAttemptPostDefault(username, pin, true);
    }

    public static CloseableHttpResponse UserCredentialAttemptesPostDefault(
            String userName, String password, Boolean validResponse) throws Exception {
        return UserCredentialAttemptesPostDefault(userName, password, null, validResponse);
    }

    public static CloseableHttpResponse UserPinCredentialAttemptPostDefault(String username, String pin,
                                                                            Boolean validateResponse) throws Exception {
        return UserPinCredentialAttemptPostDefault(username, pin, null, validateResponse);
    }

    public static CloseableHttpResponse UserCredentialAttemptesPostDefault(
            String userName, String password, String ip, Boolean validResponse) throws Exception {
        UserCredentialVerifyAttempt ucva = IdentityModel.DefaultUserCredentialAttempts(userName, password);
        if (ip != null) {
            ucva.setIpAddress(ip);
        }
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("Authorization", httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(IdentityV1UserCredentialAttemptsURI,
                JsonHelper.JsonSerializer(ucva), HttpclientHelper.HttpRequestType.post, nvps);
        if (validResponse) {
            Validator.Validate("validate response code", 201, response.getStatusLine().getStatusCode());
        }
        return response;
    }

    public static CloseableHttpResponse UserPinCredentialAttemptPostDefault(String username, String pin, String ip,
                                                                            Boolean validResponse) throws Exception {
        UserCredentialVerifyAttempt ucva = IdentityModel.DefaultUserPinAttempts(username, pin);
        if (ip != null) {
            ucva.setIpAddress(ip);
        }
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("Authorization", httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(IdentityV1UserCredentialAttemptsURI,
                JsonHelper.JsonSerializer(ucva), HttpclientHelper.HttpRequestType.post, nvps);
        if (validResponse) {
            Validator.Validate("validate response code", 201, response.getStatusLine().getStatusCode());
        }
        return response;
    }

    public static UserSecurityQuestion UserSecurityQuestionPostDefault(UserId userId) throws Exception {
        return UserSecurityQuestionPost(userId, IdentityModel.DefaultUserSecurityQuestion());
    }

    public static UserSecurityQuestion UserSecurityQuestionPost(UserId userId, UserSecurityQuestion usq)
            throws Exception {
        return (UserSecurityQuestion) IdentityPost(
                IdentityEndPointV1 + "/users/" + GetHexLongId(userId.getValue()) + "/security-questions",
                JsonHelper.JsonSerializer(usq),
                UserSecurityQuestion.class);
    }

    public static UserSecurityQuestion UserSecurityQuestionGetById(UserId userId, UserSecurityQuestionId usqId)
            throws Exception {
        return (UserSecurityQuestion) IdentityGet(
                IdentityEndPointV1 + "/users/" + GetHexLongId(userId.getValue()) +
                        "/security-questions/" + usqId.getValue(), UserSecurityQuestion.class);
    }

    public static UserSecurityQuestionVerifyAttempt UserSecurityQuestionVerifyAttemptPost(UserId userId, UserSecurityQuestionVerifyAttempt attempt)
            throws Exception {
        return IdentityPost(
                IdentityEndPointV1 + "/users/" + GetHexLongId(userId.getValue()) + "/security-question-attempts",
                JsonHelper.JsonSerializer(attempt), UserSecurityQuestionVerifyAttempt.class);
    }

    public static String GetHexLongId(Long userId) throws Exception {
        return IdConverter.idToUrlString(UserId.class, userId);
    }

    // ****** start API sample logging ******
    public static final String MessageDefaultPostUser = "[Include In Sample][1] Description: Post_User_Default";
    public static final String MessageGetUserByUserId = "[Include In Sample][1] Description: Get_User_By_UserId";

    public static void StartLoggingAPISample(String message) {
        System.out.println(message);
    }

    public static String buildIdentityCount(Integer count) {
        if (count == null) {
            return "";
        } else {
            return "&count=" + count.toString();
        }
    }

    public static String buildIdentityCursor(Integer cursor) {
        if (cursor == null) {
            return "";
        } else {
            return "&cursor=" + cursor.toString();
        }
    }
}
