/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;
// CHECKSTYLE:OFF

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.id.*;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.common.model.Results;
import com.junbo.common.util.IdFormatter;
import com.junbo.identity.spec.v1.model.*;
import com.junbo.identity.spec.v1.model.Currency;
import com.junbo.identity.spec.v1.model.Locale;
import com.junbo.identity.spec.v1.model.PIType;
import com.junbo.identity.spec.v1.model.migration.OculusInput;
import com.junbo.identity.spec.v1.model.migration.OculusOutput;
import com.junbo.identity.spec.v1.model.migration.UsernameMailBlocker;
import com.junbo.test.common.*;
import com.junbo.test.common.libs.IdConverter;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.*;

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
    public static final String IdentityV1UsernameMailBlockerURI = IdentityEndPointV1 + "/imports/username-email-block";
    public static final String IdentityV1TosURI = IdentityEndPointV1 + "/tos";
    public static final String IdentityV1UserTosAgreementURI = IdentityEndPointV1 + "/tos-agreements";
    public static final String IdentityV1UserAuthenticatorURI = IdentityEndPointV1 + "/authenticators";
    public static final String IdentityV1PITypeURI = IdentityEndPointV1 + "/payment-instrument-types";
    public static final String IdentityV1UserCommunicationURI = IdentityEndPointV1 + "/opt-ins";
    public static final String IdentityV1UserTosURI = IdentityEndPointV1 + "/tos-agreements";
    public static final String IdentityV1UserAttributeURI = IdentityEndPointV1 + "/user-attributes";
    public static final String IdentityV1UserAttributeDefinitionURI = IdentityEndPointV1 + "/user-attribute-definitions";

    public static String httpAuthorizationHeader = "";

    private Identity() {

    }

    public static <T> T IdentityPost(String requestURI, String objJson, Class<T> cls) throws Exception {
        HttpPost httpPost = new HttpPost(requestURI);
        httpPost.addHeader("accept-encoding","gzip;q=0,deflate;q=0");
        httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
        httpPost.addHeader("Authorization", httpAuthorizationHeader);
        httpPost.setEntity(new StringEntity(objJson, "utf-8"));
        return HttpclientHelper.Execute(httpPost, cls);
    }

    public static <T> T IdentityPut(String requestURI, String objJson, Class<T> cls) throws Exception {
        HttpPut httpPut = new HttpPut(requestURI);
        httpPut.addHeader("accept-encoding","gzip;q=0,deflate;q=0");
        httpPut.addHeader("Content-Type", "application/json; charset=utf-8");
        httpPut.addHeader("Authorization", httpAuthorizationHeader);
        httpPut.setEntity(new StringEntity(objJson, "utf-8"));
        return HttpclientHelper.Execute(httpPut, cls);
    }

    public static <T> T IdentityGet(String requestURI, Class<T> cls) throws Exception {
        HttpGet httpGet = new HttpGet(requestURI);
        httpGet.addHeader("accept-encoding","gzip;q=0,deflate;q=0");
        httpGet.addHeader("Content-Type", "application/json");
        httpGet.addHeader("Authorization", httpAuthorizationHeader);
        return HttpclientHelper.Execute(httpGet, cls);
    }

    public static void IdentityDelete(String requestURI) throws Exception {
        HttpDelete httpDelete = new HttpDelete(requestURI);
        httpDelete.addHeader("accept-encoding","gzip;q=0,deflate;q=0");
        httpDelete.addHeader("Content-Type", "application/json");
        httpDelete.addHeader("Authorization", httpAuthorizationHeader);
        HttpclientHelper.Execute(httpDelete);
    }

    public static void GetHttpAuthorizationHeaderForMigration() throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("grant_type", "client_credentials"));
        nvps.add(new BasicNameValuePair("client_id", "migration"));
        nvps.add(new BasicNameValuePair("client_secret", "secret"));
        nvps.add(new BasicNameValuePair("scope", "identity.service identity.migration"));

        HttpPost httpPost = new HttpPost(ConfigHelper.getSetting("defaultOauthEndpoint") + "/oauth2/token");
        httpPost.addHeader("oculus-internal", String.valueOf(true));
        httpPost.setConfig(RequestConfig.custom().setRedirectsEnabled(true).build());
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        CloseableHttpResponse response = HttpclientHelper.Execute(httpPost);

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

        HttpPost httpPost = new HttpPost(ConfigHelper.getSetting("defaultOauthEndpoint") + "/oauth2/token");
        httpPost.addHeader("oculus-internal", String.valueOf(true));
        httpPost.setConfig(RequestConfig.custom().setRedirectsEnabled(true).build());
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        CloseableHttpResponse response = HttpclientHelper.Execute(httpPost);

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

    public static List<Country> CountriesSearchSort(String locale, String sortBy) throws Exception {
        String url = "";
        if (StringUtils.isEmpty(locale) && StringUtils.isEmpty(sortBy)) {
            url = IdentityV1CountryURI;

        } else if (!StringUtils.isEmpty(locale)) {
            url = IdentityV1CountryURI + "?locale=" + locale;
        } else if (!StringUtils.isEmpty(sortBy)) {
            url = IdentityV1CountryURI + "?sortBy=" + sortBy;
        } else {
            url = IdentityV1CountryURI + "?locale=" + locale + "&sortBy=" + sortBy;
        }

        List<Country> countries = new ArrayList<>();
        for (Object obj : IdentityGet(url, (Results.class)).getItems()) {
            countries.add((Country) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj), Country.class));
        }
        return countries;
    }

    public static Results<Country> CountriesSearch(String defaultLocale, String defaultCurrency, Integer limit) throws Exception {
        String url = "";
        if (StringUtils.isEmpty(defaultCurrency) && StringUtils.isEmpty(defaultLocale)) {
            url = IdentityV1CountryURI;
        } else if (StringUtils.isEmpty(defaultLocale)) {
            url = IdentityV1CountryURI + "?defaultCurrencyId=" + defaultCurrency;
        } else if (StringUtils.isEmpty(defaultCurrency)) {
            url = IdentityV1CountryURI + "?defaultLocaleId=" + defaultLocale;
        } else {
            url = IdentityV1CountryURI + "?defaultCurrencyId=" + defaultCurrency + "&defaultLocaleId=" + defaultLocale;
        }

        if (limit != null) {
            if (url.contains("?")) {
                url = url + "&count=" + limit;
            } else {
                url = url + "?count=" + limit;
            }
        }

        Results<Country> results = IdentityGet(url, Results.class);
        List<Country> countries = new ArrayList<>();
        for (Object obj : results.getItems()) {
            countries.add((Country)JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj), Country.class));
        }
        results.setItems(countries);
        return results;
    }

    public static void CountryDeleteByCountryId(String countryId) throws Exception {
        IdentityDelete(IdentityV1CountryURI + "/" + countryId);
    }

    public static Results<Currency> CurrencySearch(Integer limit) throws Exception {
        String url = IdentityV1CurrencyURI;

        if (limit != null) {
            url = url + "?count=" + limit;
        }

        Results<Currency> results = IdentityGet(url, Results.class);
        List<Currency> currencies = new ArrayList<>();
        for (Object obj : results.getItems()) {
            currencies.add((Currency)JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj), Currency.class));
        }
        results.setItems(currencies);
        return results;
    }

    public static Currency CurrencyPut(Currency currency) throws Exception {
        return IdentityPut(IdentityV1CurrencyURI + "/" + currency.getId().toString(), JsonHelper.JsonSerializer(currency), Currency.class);
    }

    public static com.junbo.common.error.Error CurrencyPutWithoutToken(Currency currency) throws Exception {
        HttpPut httpPut = new HttpPut(IdentityV1CurrencyURI + "/" + currency.getId().toString());
        httpPut.addHeader("Content-Type", "application/json; charset=utf-8");
        httpPut.setEntity(new StringEntity(JsonHelper.JsonSerializer(currency), "utf-8"));
        return HttpclientHelper.Execute(httpPut, com.junbo.common.error.Error.class);
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

    public static Results<Locale> LocaleGetAll(Integer limit) throws Exception {
        Results<Locale> results = new Results<>();
        results.setItems(new ArrayList<Locale>());
        String url = limit == null ? IdentityV1LocaleURI : IdentityV1LocaleURI + "?count=" + limit;
        Results res = IdentityGet(url, Results.class);
        List<Locale> locales = new ArrayList<>();
        for (Object obj : res.getItems()) {
            locales.add((Locale) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj),
                            Locale.class)
            );
        }
        results.setItems(locales);
        results.setTotal(res.getTotal());
        results.setNext(res.getNext());
        results.setSelf(res.getSelf());

        return results;
    }

    public static void LocaleDeleteByLocaleId(String localeId) throws Exception {
        IdentityDelete(IdentityV1LocaleURI + "/" + localeId);
    }

    public static User UserPostDefaultWithMail(String username, String email) throws Exception {
        User user = UserPostDefaultWithName(username);
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

    public static User UserPostDefaultWithName(String username) throws Exception {
        User user = UserPostDefault(IdentityModel.DefaultUser());
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setUserId(user.getId());
        userPersonalInfo.setType("USERNAME");
        UserLoginName loginName = new UserLoginName();
        loginName.setUserName(username);
        userPersonalInfo.setValue(ObjectMapperProvider.instance().valueToTree(loginName));
        UserPersonalInfo loginInfo = UserPersonalInfoPost(user.getId(), userPersonalInfo);
        user.setIsAnonymous(false);
        user.setUsername(loginInfo.getId());
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

    public static Tos TosPostDefault(Tos tos) throws Exception {
        return IdentityPost(IdentityV1TosURI, JsonHelper.JsonSerializer(tos), Tos.class);
    }

    public static Tos TosGet(TosId tosId) throws Exception {
        return IdentityGet(IdentityV1TosURI + "/" + tosId.getValue(), Tos.class);
    }

    public static Results<Tos> TosSearch(String title, String type, String state, String country) throws Exception {
        List<Tos> tosList = new ArrayList<Tos>();
        Results<Tos> tosResults = IdentityGet(buildTosSearchURI(title, state, type, country), Results.class);
        for (Object obj : tosResults.getItems()) {
            tosList.add((Tos) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj), Tos.class));
        }
        tosResults.setItems(tosList);
        return tosResults;
    }

    private static String buildTosSearchURI(String title, String state, String type, String country) {
        if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(state) && !StringUtils.isEmpty(type) && !StringUtils.isEmpty(country)) {
            return IdentityV1TosURI + "?title=" + title + "&state=" + state + "&type=" + type + "&country=" + country;
        } else if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(state) && !StringUtils.isEmpty(type)) {
            return IdentityV1TosURI + "?title=" + title + "&state=" + state + "&type=" + type;
        } else if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(state) && !StringUtils.isEmpty(country)) {
            return IdentityV1TosURI + "?title=" + title + "&state=" + state + "&country=" + country;
        } else if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(type) && !StringUtils.isEmpty(country)) {
            return IdentityV1TosURI + "?title=" + title + "&type=" + type + "&country=" + country;
        } else if (!StringUtils.isEmpty(state) && !StringUtils.isEmpty(type) && !StringUtils.isEmpty(country)) {
            return IdentityV1TosURI + "?state=" + state + "&type=" + type + "&country=" + country;
        } else if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(state)) {
            return IdentityV1TosURI + "?title=" + title + "&state=" + state;
        } else if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(type)) {
            return IdentityV1TosURI + "?title=" + title + "&type=" + type;
        } else if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(country)) {
            return IdentityV1TosURI + "?title=" + title + "&country=" + country;
        } else if (!StringUtils.isEmpty(state) && !StringUtils.isEmpty(type)) {
            return IdentityV1TosURI + "?state=" + state + "&type=" + type;
        } else if (!StringUtils.isEmpty(state) && !StringUtils.isEmpty(country)) {
            return IdentityV1TosURI + "?state=" + state + "&country=" + country;
        } else if (!StringUtils.isEmpty(type) && !StringUtils.isEmpty(country)) {
            return IdentityV1TosURI + "?type=" + type + "&country=" + country;
        } else if (!StringUtils.isEmpty(title)) {
            return IdentityV1TosURI + "?title=" + title;
        } else if (!StringUtils.isEmpty(state)) {
            return IdentityV1TosURI + "?state=" + state;
        } else if (!StringUtils.isEmpty(type)) {
            return IdentityV1TosURI + "?type=" + type;
        } else if (!StringUtils.isEmpty(country)) {
            return IdentityV1TosURI + "?country=" + country;
        } else {
            return IdentityV1TosURI;
        }
    }

    public static UserTosAgreement UserTosAgreementPost(UserId userId, TosId tosId) throws Exception {
        UserTosAgreement userTosAgreement = new UserTosAgreement();
        userTosAgreement.setUserId(userId);
        userTosAgreement.setTosId(tosId);
        userTosAgreement.setAgreementTime(new Date());
        return IdentityPost(IdentityV1UserTosURI, JsonHelper.JsonSerializer(userTosAgreement), UserTosAgreement.class);
    }

    public static UserTosAgreement UserTosAgreementGet(UserTosAgreement userTosAgreement) throws Exception {
        return IdentityGet(IdentityV1UserTosURI + "/" + userTosAgreement.getId().toString(), UserTosAgreement.class);
    }

    public static Results<UserTosAgreement> UserTosAgreementSearch(UserId userId, TosId tosId, Integer limit) throws Exception {
        String url = IdentityV1UserTosURI;
        if (userId != null && tosId != null) {
            url = url + "?tosId=" + tosId.toString() + "&userId=" + GetHexLongId(userId.getValue());
        } else if (userId != null) {
            url = url + "?userId=" + GetHexLongId(userId.getValue());
        } else if (tosId != null) {
            url = url + "?tosId=" + tosId.toString();
        }

        if (limit != null) {
            url = url + "&count=" + limit;
        }

        Results results = IdentityGet(url, Results.class);
        List<UserTosAgreement> userTosAgreements = new ArrayList<>();
        for (Object obj : results.getItems()) {
            userTosAgreements.add((UserTosAgreement)JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj), UserTosAgreement.class));
        }
        results.setItems(userTosAgreements);
        return results;
    }

    public static User UserPut(User user) throws Exception {
        return (User) IdentityPut(IdentityV1UserURI + "/" + IdFormatter.encodeId(user.getId()),
                JsonHelper.JsonSerializer(user), User.class);
    }

    public static User UserPartialPost(User user) throws Exception {
        return IdentityPost(IdentityV1UserURI + "/" + IdFormatter.encodeId(user.getId()),
                JsonHelper.JsonSerializer(user), User.class);
    }

    public static void UserDelete(User user) throws Exception {
        IdentityDelete(IdentityV1UserURI + "/" + IdFormatter.encodeId(user.getId()));
    }

    public static void UsernameMailBlockerPost(UsernameMailBlocker usernameMailBlocker) throws Exception {
        IdentityPost(IdentityV1UsernameMailBlockerURI, JsonHelper.JsonSerializer(usernameMailBlocker), null);
    }

    public static List<User> UserSearchByUsername(String username) throws Exception {
        List<User> userList = new ArrayList<User>();
        for (Object obj : IdentityGet(
                IdentityV1UserURI + "?username=" + username,
                (Results.class)).getItems()) {
            userList.add((User) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj), User.class)
            );
        }
        return userList;
    }

    public static List<User> UserSearchByEmail(String email) throws Exception {
        List<User> userList = new ArrayList<User>();
        for (Object obj : IdentityGet(
                IdentityV1UserURI + "?primaryMail=" + URLEncoder.encode(email, "UTF-8"),
                (Results.class)).getItems()) {
            userList.add((User) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj), User.class)
            );
        }
        return userList;
    }

    public static User UserGetByUserId(UserId userId) throws Exception {
        return (User) IdentityGet(IdentityV1UserURI + "/" + IdFormatter.encodeId(userId), User.class);
    }

    public static UserTosAgreement UserTosAgreementPost(UserId userId, TosId tosId) throws Exception {
        UserTosAgreement userTosAgreement = new UserTosAgreement();
        userTosAgreement.setTosId(tosId);
        userTosAgreement.setUserId(userId);
        userTosAgreement.setAgreementTime(new Date());
        return IdentityPost(IdentityV1UserTosAgreementURI, JsonHelper.JsonSerializer(userTosAgreement), UserTosAgreement.class);
    }

    public static CloseableHttpResponse UserPersonalInfoPost(UserPersonalInfo upi, boolean validResponse) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("Authorization", httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(IdentityV1UserPersonalInfoURI,
                JsonHelper.JsonSerializer(upi), HttpclientHelper.HttpRequestType.post, nvps);
        if (validResponse) {
            Validator.Validate("validate response code", 201, response.getStatusLine().getStatusCode());
        }
        return response;
    }

    public static UserPersonalInfo UserPersonalInfoPost(UserId userId, UserPersonalInfo upi) throws Exception {
        upi.setUserId(userId);
        return (UserPersonalInfo) IdentityPost(IdentityV1UserPersonalInfoURI,
                JsonHelper.JsonSerializer(upi), UserPersonalInfo.class);
    }

    public static UserPersonalInfo UserPersonalInfoPatch(UserPersonalInfoId userPersonalInfoId, UserPersonalInfo upi) throws Exception {
        return IdentityPost(IdentityV1UserPersonalInfoURI + "/" + IdConverter.idToHexString(userPersonalInfoId),
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

    public static Boolean UserHtmlUpdate(UserId userId) throws Exception {
        return IdentityPost(IdentityV1ImportsURI + "/update-html/" + IdConverter.idToHexString(userId),
                "", Boolean.class);
    }

    public static Boolean GetUsernameEmailBlocker(String username, String email) throws Exception {
        return IdentityGet(IdentityV1UserURI + "/check-legacy-username-email?username=" + username + "&email=" + URLEncoder.encode(email, "UTF-8"),
                Boolean.class);
    }

    public static void ImportUsernameMailBlocker(UsernameMailBlocker usernameMailBlocker) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("Authorization", httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(IdentityV1UsernameMailBlockerURI,
                JsonHelper.JsonSerializer(usernameMailBlocker), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response code", 200, response.getStatusLine().getStatusCode());
        response.close();
    }

    public static com.junbo.common.error.Error ImportUsernameMailBlockerError(String username, String email) throws Exception {
        UsernameMailBlocker usernameMailBlocker = new UsernameMailBlocker();
        usernameMailBlocker.setUsername(username);
        usernameMailBlocker.setEmail(email);

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(IdentityV1UsernameMailBlockerURI,
                JsonHelper.JsonSerializer(usernameMailBlocker), HttpclientHelper.HttpRequestType.post, nvps);
        com.junbo.common.error.Error error = JsonHelper.JsonDeserializer(new InputStreamReader(response.getEntity().getContent()),
                com.junbo.common.error.Error.class);
        response.close();
        return error;
    }

    public static UserCredential CredentialsGetByUserId(UserId userId) throws Exception {
        JsonNode jsonNode = JsonHelper.ObjectToJsonNode((IdentityGet(IdentityV1UserURI
                        + "/" + IdFormatter.encodeId(userId) + "/credentials?credentialType=PASSWORD",
                (Results.class)).getItems().get(0)));
        return (UserCredential) JsonHelper.JsonNodeToObject(jsonNode, UserCredential.class);
    }

    public static Results<UserGroup> UserGroupSearch(GroupId groupId, UserId userId, Integer limit) throws Exception {
        String url = "";
        if (userId != null && groupId != null) {
            url = IdentityV1UserGroupMemberURI + "?userId=" + IdConverter.idToHexString(userId) + "&groupId=" + IdConverter.idToHexString(groupId);
        } else if (groupId != null) {
            url = IdentityV1UserGroupMemberURI + "?groupId=" + IdConverter.idToHexString(groupId);
        } else if (userId != null) {
            url = IdentityV1UserGroupMemberURI + "?userId=" + IdConverter.idToHexString(userId);
        }

        if (limit != null) {
            url = url + "&count=" + limit;
        }
        Results<UserGroup> userGroups = IdentityGet(url, Results.class);
        List<UserGroup> userGroupList = new ArrayList<>();
        for (Object obj : userGroups.getItems()) {
            userGroupList.add((UserGroup) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj), UserGroup.class));
        }

        userGroups.setItems(userGroupList);
        return userGroups;
    }

    public static Results<UserGroup> UserGroupSearch(GroupId groupId, UserId userId) throws Exception {
        String url = "";
        if (userId != null && groupId != null) {
            url = IdentityV1UserGroupMemberURI + "?userId=" + IdConverter.idToHexString(userId) + "&groupId=" + IdConverter.idToHexString(groupId);
        } else if (groupId != null) {
            url = IdentityV1UserGroupMemberURI + "?groupId=" + IdConverter.idToHexString(groupId);
        } else if (userId != null) {
            url = IdentityV1UserGroupMemberURI + "?userId=" + IdConverter.idToHexString(userId);
        }

        Results<UserGroup> userGroups = IdentityGet(url, Results.class);
        List<UserGroup> userGroupList = new ArrayList<>();
        for (Object obj : userGroups.getItems()) {
            userGroupList.add((UserGroup) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj), UserGroup.class));
        }

        userGroups.setItems(userGroupList);
        return userGroups;
    }

    public static UserGroup UserGroupPost(UserId userId, GroupId groupId) throws Exception {
        UserGroup userGroup = new UserGroup();
        userGroup.setUserId(userId);
        userGroup.setGroupId(groupId);
        return IdentityPost(IdentityV1UserGroupMemberURI, JsonHelper.JsonSerializer(userGroup), UserGroup.class);
    }

    public static UserGroup UserGroupPut(UserGroup userGroup) throws Exception {
        return IdentityPut(IdentityV1UserGroupMemberURI + "/" + userGroup.getId().getValue(),
                JsonHelper.JsonSerializer(userGroup), UserGroup.class);
    }

    public static UserCommunication UserCommunicationPost(UserId userId, CommunicationId communicationId) throws Exception {
        UserCommunication userCommunication = new UserCommunication();
        userCommunication.setUserId(userId);
        userCommunication.setCommunicationId(communicationId);
        return IdentityPost(IdentityV1UserCommunicationURI, JsonHelper.JsonSerializer(userCommunication), UserCommunication.class);
    }

    public static UserCommunication UserCommunicationPut(UserCommunication userCommunication) throws Exception {
        return IdentityPut(IdentityV1UserCommunicationURI + "/" + userCommunication.getId().toString(), JsonHelper.JsonSerializer(userCommunication),
                UserCommunication.class);
    }

    public static UserCommunication UserCommunicationGet(UserCommunicationId userCommunicationId) throws Exception {
        return IdentityGet(IdentityV1UserCommunicationURI + "/" + userCommunicationId.toString(), UserCommunication.class);
    }

    public static void UserCommunicationDelete(UserCommunicationId userCommunicationId) throws Exception {
        IdentityDelete(IdentityV1UserCommunicationURI + "/" + userCommunicationId.toString());
    }

    public static Results<UserCommunication> UserCommunicationSearch(UserId userId, CommunicationId communicationId, Integer limit) throws Exception {
        String url = IdentityV1UserCommunicationURI;

        if (userId != null && communicationId != null) {
            url = url + "?userId=" + IdConverter.idToHexString(userId) + "&communicationId=" + communicationId.toString();
        } else if (userId != null) {
            url = url + "?userId=" + IdConverter.idToHexString(userId);
        } else if (communicationId != null) {
            url = url + "?communicationId=" + communicationId.toString();
        }

        if (limit != null) {
            url = url + "&count=" + limit;
        }
        Results results = IdentityGet(url, Results.class);
        List<UserCommunication> userCommunications = new ArrayList<>();
        for (Object obj : results.getItems()) {
            userCommunications.add((UserCommunication)JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj), UserCommunication.class));
        }
        results.setItems(userCommunications);
        return results;
    }

    public static UserAuthenticator UserAuthenticatorPost(UserId userId, String externalRefId) throws Exception {
        UserAuthenticator userAuthenticator = new UserAuthenticator();
        userAuthenticator.setUserId(userId);
        userAuthenticator.setType("GOOGLE");
        userAuthenticator.setExternalId(externalRefId);
        return IdentityPost(IdentityV1UserAuthenticatorURI, JsonHelper.JsonSerializer(userAuthenticator), UserAuthenticator.class);
    }

    public static UserAuthenticator UserAuthenticatorPost(UserId userId) throws Exception {
        return UserAuthenticatorPost(userId, RandomHelper.randomAlphabetic(15));
    }

    public static UserAuthenticator UserAuthenticatorGet(UserAuthenticatorId userAuthenticatorId) throws Exception {
        return IdentityGet(IdentityV1UserAuthenticatorURI + "/" + userAuthenticatorId.toString(), UserAuthenticator.class);
    }

    public static UserAuthenticator UserAuthenticatorPut(UserAuthenticator userAuthenticator) throws Exception {
        return IdentityPut(IdentityV1UserAuthenticatorURI + "/" + userAuthenticator.getId().getValue(), JsonHelper.JsonSerializer(userAuthenticator), UserAuthenticator.class);
    }

    public static Results<UserAuthenticator> UserAuthenticatorSearch(UserId userId, String type, String externalId, Integer limit) throws Exception {
        String url = IdentityV1UserAuthenticatorURI;
        if (userId != null && type != null && externalId != null) {
            url = url + "?userId=" + IdConverter.idToHexString(userId) + "&type=" + type + "&externalId=" + externalId;
        } else if (userId != null && type != null) {
            url = url + "?userId=" + IdConverter.idToHexString(userId) + "&type=" + type;
        } else if (userId != null && externalId != null) {
            url = url + "?userId=" + IdConverter.idToHexString(userId) + "&externalId=" + externalId;
        } else if (type != null && externalId != null) {
            url = url + "?type=" + type + "&externalId=" + externalId;
        } else if (userId != null) {
            url = url + "?userId=" + IdConverter.idToHexString(userId);
        } else if (type != null) {
            url = url + "?type=" + type;
        } else if (externalId != null) {
            url = url + "?externalId=" + externalId;
        }

        if (limit != null) {
            url = url + "&count=" + limit;
        }

        Results<UserAuthenticator> userAuthenticatorResults = IdentityGet(url, Results.class);
        List<UserAuthenticator> userAuthenticators = new ArrayList<>();
        for (Object obj : userAuthenticatorResults.getItems()) {
            userAuthenticators.add((UserAuthenticator) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj), UserAuthenticator.class));
        }

        userAuthenticatorResults.setItems(userAuthenticators);
        return userAuthenticatorResults;
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

    public static void OrganizationDelete(OrganizationId organizationId) throws Exception {
        IdentityDelete(IdentityV1OrganizationURI + "/" + IdFormatter.encodeId(organizationId));
    }

    public static Results<Organization> OrganizationGetAll(Integer limit, Integer offset) throws Exception {
        String queryStr = "";
        if (limit == null && offset == null) {
            // do nothing
        } else if (limit != null && offset == null) {
            queryStr = "?count=" + limit.toString();
        } else if (limit == null && offset != null) {
            queryStr = "?cursor=" + offset.toString();
        } else {
            queryStr = "?count=" + limit.toString() + "&cursor=" + offset.toString();
        }

        return IdentityGet(
                IdentityV1OrganizationURI + queryStr, Results.class);
    }

    public static Group GroupPostDefault(Group group) throws Exception {
        return IdentityPost(IdentityV1GroupURI, JsonHelper.JsonSerializer(group), Group.class);
    }

    public static void GroupDelete(Group group) throws Exception {
        IdentityDelete(IdentityV1GroupURI + "/" + group.getId());
    }

    public static Results<Group> GroupSearch(String name, OrganizationId organizationId, UserId userId, Integer limit) throws Exception {
        String url = IdentityV1GroupURI;
        if (userId != null) {
            url = url + "?userId=" + IdConverter.idToHexString(userId);
        } else if (!StringUtils.isEmpty(organizationId) && !StringUtils.isEmpty(name)) {
            url = url + "?organizationId=" + IdConverter.idToHexString(organizationId) + "&name=" + name;
        } else if (!StringUtils.isEmpty(organizationId)) {
            url = url + "?organizationId=" + IdConverter.idToHexString(organizationId);
        }

        if (limit != null) {
            url = url + "&count=" + limit;
        }

        Results<Group> groupResults = IdentityGet(url, Results.class);
        List<Group> groups = new ArrayList<>();
        for (Object obj : groupResults.getItems()) {
            groups.add((Group)JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj), Group.class));
        }
        groupResults.setItems(groups);
        return groupResults;
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

    public static Results<DeviceType> DeviceTypeGetAll(Integer limit) throws Exception {
        Results<DeviceType> results = new Results<>();
        results.setItems(new ArrayList<DeviceType>());
        String url = limit == null ? IdentityV1DeviceTypeURI : IdentityV1DeviceTypeURI + "?count=" + limit;
        Results res = IdentityGet(url, Results.class);
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

    public static Results<ErrorInfo> ErrorInfoGetAll(Integer limit) throws Exception {
        Results<ErrorInfo> results = new Results<>();
        results.setItems(new ArrayList<ErrorInfo>());
        String url = limit == null ? IdentityV1ErrorInfoURI : IdentityV1ErrorInfoURI + "?count=" + limit;
        Results res = IdentityGet(url, Results.class);
        List<ErrorInfo> errorInfos = new ArrayList<>();
        for (Object obj : res.getItems()) {
            errorInfos.add((ErrorInfo) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj),
                    ErrorInfo.class));
        }
        results.setItems(errorInfos);
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

    public static Results<Communication> CommunicationSearch(String region, String translation, Integer limit) throws Exception {
        Results<Communication> results = new Results<>();
        results.setItems(new ArrayList<Communication>());
        Results res = IdentityGet(IdentityV1CommunicationURI + buildCommunicationQueryUrl(region, translation, limit), Results.class);
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

    public static String buildCommunicationQueryUrl(String region, String translation, Integer limit) {
        String url = "";
        if (StringUtils.isEmpty(region) && StringUtils.isEmpty(translation)) {
            url = "";
        } else if (!StringUtils.isEmpty(region) && !StringUtils.isEmpty(translation)) {
            url = "?region=" + region + "&translation=" + translation;
        } else if (!StringUtils.isEmpty(region)) {
            url = "?region=" + region;
        } else {
            url = "?translation=" + translation;
        }

        if (limit != null) {
            url = !StringUtils.isEmpty(url) ? (url + "&count=" + limit) : ("count=" + limit);
        }

        return url;
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
        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(
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
        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(
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
        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(IdentityV1UserCredentialAttemptsURI,
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
        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(IdentityV1UserCredentialAttemptsURI,
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

    public static Results<UserSecurityQuestion> UserSecurityQuestionSearch(UserId userId, Integer limit) throws Exception {
        String url = IdentityEndPointV1 + "/users/" + GetHexLongId(userId.getValue()) + "/security-questions";
        if (limit != null) {
            url = url + "?count=" + limit;
        }
        Results results = IdentityGet(url, Results.class);
        List<UserSecurityQuestion> userSecurityQuestions = new ArrayList<>();
        for (Object obj : results.getItems()) {
            userSecurityQuestions.add((UserSecurityQuestion)JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj), UserSecurityQuestion.class));
        }
        results.setItems(userSecurityQuestions);

        return results;
    }

    public static PIType PITypePostDefault(String typeCode) throws Exception {
        PIType piType = new PIType();
        piType.setTypeCode(typeCode);
        piType.setCapableOfRecurring(true);
        piType.setIsRefundable(true);
        LocaleName localeName = new LocaleName();
        localeName.setDescription(RandomHelper.randomAlphabetic(15));
        Map<String, JsonNode> locales = new HashMap<>();
        locales.put("en_US", JsonHelper.ObjectToJsonNode(localeName));
        piType.setLocales(locales);

        return IdentityPost(IdentityV1PITypeURI, JsonHelper.JsonSerializer(piType), PIType.class);
    }

    public static PIType PITypeGet(PITypeId piTypeId) throws Exception {
        return IdentityGet(IdentityV1PITypeURI + "/" + piTypeId.toString(), PIType.class);
    }

    public static PIType PITypePutDefault(PIType piType) throws Exception {
        return IdentityPut(IdentityV1PITypeURI + "/" + piType.getId().toString(), JsonHelper.JsonSerializer(piType), PIType.class);
    }

    public static Results<PIType> PITypeSearch(String piTypeCode, Integer limit) throws Exception {
        String url = IdentityV1PITypeURI;
        if (piTypeCode != null) {
            url = url + "?typeCode=" + piTypeCode;
        }

        if (limit != null) {
            if (piTypeCode != null) {
                url = url + "&count=" + limit;
            } else {
                url = url + "?count=" + limit;
            }
        }

        Results<PIType> results = IdentityGet(url, Results.class);
        List<PIType> piTypes = new ArrayList<>();
        for(Object obj : results.getItems()) {
            piTypes.add((PIType) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj),
                    PIType.class));
        }
        results.setItems(piTypes);
        return results;
    }

    public static UserSecurityQuestionVerifyAttempt UserSecurityQuestionVerifyAttemptPost(UserId userId, UserSecurityQuestionVerifyAttempt attempt)
            throws Exception {
        return IdentityPost(
                IdentityEndPointV1 + "/users/" + GetHexLongId(userId.getValue()) + "/security-question-attempts",
                JsonHelper.JsonSerializer(attempt), UserSecurityQuestionVerifyAttempt.class);
    }

    public static UserAttributeDefinition UserAttributeDefinitionPost(UserAttributeDefinition userAttributeDefinition) throws Exception {
        return IdentityPost(
                IdentityV1UserAttributeDefinitionURI, JsonHelper.JsonSerializer(userAttributeDefinition), UserAttributeDefinition.class);
    }

    public static UserAttributeDefinition UserAttributeDefinitionPut(UserAttributeDefinition userAttributeDefinition) throws Exception {
        return IdentityPut(
                IdentityV1UserAttributeDefinitionURI + "/" + userAttributeDefinition.getId().toString(),
                JsonHelper.JsonSerializer(userAttributeDefinition), UserAttributeDefinition.class);
    }

    public static UserAttributeDefinition UserAttributeDefinitionGet(UserAttributeDefinitionId userAttributeDefinitionId) throws Exception {
        return IdentityGet(IdentityV1UserAttributeDefinitionURI + "/" + userAttributeDefinitionId.toString(),UserAttributeDefinition.class);
    }

    public static void UserAttributeDefinitionDelete(UserAttributeDefinitionId userAttributeDefinitionId) throws Exception {
        IdentityDelete(IdentityV1UserAttributeDefinitionURI + "/" + userAttributeDefinitionId.toString());
    }

    public static Results<UserAttributeDefinition> UserAttributeDefinitionSearch(OrganizationId organizationId, String type, Integer limit, Integer offset) throws Exception {
        String url = IdentityV1UserAttributeDefinitionURI;
        if (limit != null && offset != null) {
            url = url + "?cursor=" + offset + "&count=" + limit;
        } else if (limit != null) {
            url = url + "?count=" + limit;
        } else if (offset != null) {
            url = url + "?cursor=" + offset;
        }

        if (url.contains("?")) {
            if (organizationId != null) {
                url = url + "&organizationId=" + IdConverter.idToHexString(organizationId);
            }
            if (type != null) {
                url = url + "&type=" + type;
            }
        } else {
            if (organizationId != null || !StringUtils.isEmpty(type)) {
                url = url + "?";
            }
            if (organizationId != null) {
                url = url + "organizationId=" + IdConverter.idToHexString(organizationId);
            }
            if (type != null) {
                url = url + "&type=" + type;
            }
        }

        Results<UserAttributeDefinition> results = new Results<>();
        Results res = IdentityGet(url, Results.class);
        results.setItems(new ArrayList<UserAttributeDefinition>());
        for (Object obj : res.getItems()) {
            results.getItems().add((UserAttributeDefinition) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj),
                            UserAttributeDefinition.class)
            );
        }
        results.setTotal(res.getTotal());
        results.setNext(res.getNext());
        results.setSelf(res.getSelf());

        return results;
    }

    public static UserAttribute UserAttributePost(UserAttribute userAttribute) throws Exception {
        return IdentityPost(IdentityV1UserAttributeURI, JsonHelper.JsonSerializer(userAttribute), UserAttribute.class);
    }

    public static UserAttribute UserAttributePut(UserAttribute userAttribute) throws Exception {
        return IdentityPut(IdentityV1UserAttributeURI + "/" + userAttribute.getId().toString(),
                JsonHelper.JsonSerializer(userAttribute), UserAttribute.class);
    }

    public static UserAttribute UserAttributeGet(UserAttributeId userAttributeId) throws Exception {
        return IdentityGet(IdentityV1UserAttributeURI + "/" + userAttributeId.toString(), UserAttribute.class);
    }

    public static void UserAttributeDelete(UserAttributeId userAttributeId) throws Exception {
        IdentityDelete(IdentityV1UserAttributeURI + "/" + userAttributeId.toString());
    }

    public static Results<UserAttribute> UserAttributeSearchByStatus(Boolean isActive) throws Exception{
        String url = IdentityV1UserAttributeURI;
        url = url + "?isActive=" + isActive;

        Results res = IdentityGet(url, Results.class);
        Results<UserAttribute> results = new Results<>();
        results.setItems(new ArrayList<UserAttribute>());
        for (Object obj : res.getItems()) {
            results.getItems().add((UserAttribute) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj), UserAttribute.class)
            );
        }
        results.setTotal(res.getTotal());
        results.setNext(res.getNext());
        results.setSelf(res.getSelf());

        return results;
    }

    public static Results<UserAttribute> UserAttributeSearch(UserId userId, UserAttributeDefinitionId userAttributeDefinitionId,
                                                             Integer limit, Integer offset) throws Exception {
        String url = IdentityV1UserAttributeURI;
        if (userId != null && userAttributeDefinitionId != null) {
            url = url + "?userId=" + IdConverter.idToHexString(userId) + "&userAttributeDefinitionId=" + IdConverter.idToHexString(userAttributeDefinitionId);
        } else if (userId != null) {
            url = url + "?userId=" + IdConverter.idToHexString(userId);
        } else if (userAttributeDefinitionId != null) {
            url = url + "?userAttributeDefinitionId=" + IdConverter.idToHexString(userAttributeDefinitionId);
        }

        if (limit != null) {
            url = url + "&cursor=" + limit;
        }
        if (offset != null) {
            url = url + "&count=" + offset;
        }

        Results res = IdentityGet(url, Results.class);
        Results<UserAttribute> results = new Results<>();
        results.setItems(new ArrayList<UserAttribute>());
        for (Object obj : res.getItems()) {
            results.getItems().add((UserAttribute) JsonHelper.JsonNodeToObject(JsonHelper.ObjectToJsonNode(obj), UserAttribute.class)
            );
        }
        results.setTotal(res.getTotal());
        results.setNext(res.getNext());
        results.setSelf(res.getSelf());

        return results;
    }

    public static String GetHexLongId(Long userId) throws Exception {
        return IdConverter.idToUrlString(UserId.class, userId);
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
