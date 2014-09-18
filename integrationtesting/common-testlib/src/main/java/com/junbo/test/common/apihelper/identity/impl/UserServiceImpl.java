/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.error.*;
import com.junbo.common.error.Error;
import com.junbo.common.id.UserId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.*;
import com.junbo.identity.spec.v1.model.Address;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.oauth.spec.model.TokenInfo;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.Identity.AddressInfo;
import com.junbo.test.common.Entities.Identity.UserInfo;
import com.junbo.test.common.Entities.enums.*;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.JsonHelper;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author Jason
 *         time 3/10/2014
 *         User related API helper, including get/post/put user, update password and so on.
 */
public class UserServiceImpl extends HttpClientBase implements UserService {

    private final String identityServerURL = ConfigHelper.getSetting("defaultIdentityEndpoint") + "/users";
    private final String identityPiiURL = ConfigHelper.getSetting("defaultIdentityEndpoint") + "/personal-info";
    private static UserService instance;
    private String userPassword = "Test1234";

    private OAuthService oAuthClient = OAuthServiceImpl.getInstance();

    public static synchronized UserService instance() {
        if (instance == null) {
            instance = new UserServiceImpl();
        }
        return instance;
    }

    private UserServiceImpl() {
        componentType = ComponentType.IDENTITY;
    }


    public String PostUser(String userName, String pwd, String emailAddress) throws Exception {
        return PostUser(userName, pwd, emailAddress, null, null);
    }

    private String PostUser(String userName, String pwd, String emailAddress, String vat,
                            List<Address> billingAddresses) throws Exception {
        Master.getInstance().setCurrentUid(null);
        oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.IDENTITY);
        User userForPost = new User();
        userForPost.setIsAnonymous(true);
        userForPost.setStatus("ACTIVE");
        if (vat != null) {
            UserVAT userVAT = new UserVAT();
            userVAT.setVatNumber(vat);
            Map<String, UserVAT> vats = new HashMap<>();
            vats.put(vat.substring(0, 2).toUpperCase(), userVAT);
            userForPost.setVat(vats);
        }

        String responseBody = restApiCall(HTTPMethod.POST, identityServerURL, userForPost, 201, true);
        User userGet = new JsonMessageTranscoder().decode(new TypeReference<User>() {
        },
                responseBody);

        String userId = IdConverter.idToHexString(userGet.getId());

        UserPersonalInfo userLoginNamePersonalInfo = null;
        if (userName != null && !userName.isEmpty()) {
            userLoginNamePersonalInfo = postUserName(userName, userGet.getId());
            userGet.setUsername(userLoginNamePersonalInfo.getId());
            userGet.setIsAnonymous(false);
            this.PutUser(userId, userGet);
        } else {
            userLoginNamePersonalInfo = postUserName(RandomFactory.getRandomStringOfAlphabet(10), userGet.getId());
            userGet.setUsername(userLoginNamePersonalInfo.getId());
            userGet.setIsAnonymous(false);
            this.PutUser(userId, userGet);
        }

        userGet = Master.getInstance().getUser(userId);
        Master.getInstance().addUser(userId, userGet);
        Master.getInstance().setCurrentUid(userId);
        String password;
        if (pwd != null && !pwd.isEmpty()) {
            password = postPassword(userId, pwd);
        } else {
            password = postPassword(userId, userPassword);
        }

        oAuthClient.postUserAccessToken(userId, userName, password);

        List<UserPersonalInfoLink> addresses = new ArrayList<>();
        List<UserPersonalInfoLink> emails = new ArrayList<>();
        List<UserPersonalInfoLink> phones = new ArrayList<>();

        UserId userIdDefault = userGet.getId();

        //attach user email and address info
        UserPersonalInfo email = postEmail(userIdDefault, emailAddress);
        UserPersonalInfo address;
        boolean isDefault = true;
        if (billingAddresses != null) {
            for (Address userAddress : billingAddresses) {
                address = postBillingAddress(userIdDefault, userAddress);
                UserPersonalInfoLink piAddress = new UserPersonalInfoLink();
                piAddress.setIsDefault(isDefault);
                isDefault = false;
                piAddress.setUserId(userIdDefault);
                piAddress.setValue(address.getId());
                addresses.add(piAddress);
            }
        } else {
            address = postAddress(userIdDefault);
            UserPersonalInfoLink piAddress = new UserPersonalInfoLink();
            piAddress.setIsDefault(Boolean.TRUE);
            piAddress.setUserId(userIdDefault);
            piAddress.setValue(address.getId());
            addresses.add(piAddress);
        }
        UserPersonalInfo phone = postPhone(userIdDefault);
        UserPersonalInfo name = postName(userIdDefault);

        UserPersonalInfoLink piEmail = new UserPersonalInfoLink();
        piEmail.setIsDefault(Boolean.TRUE);
        piEmail.setUserId(userIdDefault);
        piEmail.setValue(email.getId());

        UserPersonalInfoLink piPhone = new UserPersonalInfoLink();
        piPhone.setIsDefault(true);
        piPhone.setUserId(userIdDefault);
        piPhone.setValue(phone.getId());

        emails.add(piEmail);
        phones.add(piPhone);

        userGet.setAddresses(addresses);
        userGet.setEmails(emails);
        userGet.setPhones(phones);
        userGet.setName(name.getId());

        this.PutUser(userId, userGet);

        return userId;

    }

    @Override
    public String PostUser(String vat, List<Address> addresses) throws Exception {
        return PostUser(null, null, null, vat, addresses);
    }

    @Override
    public String PostUser(String vat) throws Exception {
        return PostUser(null, null, null, vat, null);
    }

    @Override
    public String PostUser(UserInfo userInfo) throws Exception {
        return PostUser(userInfo, 200);
    }

    @Override
    public String PostUser(UserInfo userInfo, int expectedResponseCode) throws Exception {
        String cid = oAuthClient.getCid();
        oAuthClient.authorizeLoginView(cid);
        oAuthClient.authorizeRegister(cid);
        oAuthClient.registerUser(userInfo, cid);

        String emailVerifyLink  = oAuthClient.getEmailVerifyLink(cid);
        if(emailVerifyLink != null && !emailVerifyLink.isEmpty()){
            oAuthClient.accessEmailVerifyLink(emailVerifyLink);
        }


        String accessToken = oAuthClient.postUserAccessToken(userInfo.getEncodedEmails().get(0), userInfo.getPassword());
        TokenInfo tokenInfo = oAuthClient.getTokenInfo(accessToken);
        String uid = IdConverter.idToHexString(tokenInfo.getSub());
        Master.getInstance().addUserAccessToken(uid, accessToken);
        Master.getInstance().setCurrentUid(uid);

        GetUserByUserId(uid);

        User user = Master.getInstance().getUser(uid);

        List<UserPersonalInfoLink> addresses = new ArrayList<>();

        for (AddressInfo addressInfo : userInfo.getAddressInfos()) {
            UserPersonalInfo address = postAddress(user.getId(), addressInfo);
            UserPersonalInfoLink piAddress = new UserPersonalInfoLink();
            piAddress.setIsDefault(Boolean.TRUE);
            piAddress.setUserId(user.getId());
            piAddress.setValue(address.getId());
            piAddress.setIsDefault(addressInfo.isDefault());
            addresses.add(piAddress);
        }

        user.setAddresses(addresses);

        this.PutUser(uid, user);

        return uid;
    }

    private UserPersonalInfo postAddress(UserId userId, AddressInfo addressInfo) throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType("ADDRESS");
        userPersonalInfo.setUserId(userId);
        userPersonalInfo.setValue(addressInfo.getJsonString());
        return postUserPersonalInfo(userPersonalInfo);
    }

    @Override
    public String PostUser() throws Exception {
        return PostUser(UserInfo.getRandomUserInfo(), 200);
    }

    private UserPersonalInfo postBillingAddress(UserId userId, Address address) throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType("ADDRESS");
        userPersonalInfo.setUserId(userId);
        byte[] bytes = new JsonMessageTranscoder().encode(address);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(bytes);
        userPersonalInfo.setValue(value);

        return postUserPersonalInfo(userPersonalInfo);
    }

    private UserPersonalInfo postAddress(UserId userId) throws Exception {

        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType("ADDRESS");
        userPersonalInfo.setUserId(userId);
        String str = "{\"subCountry\":\"TX\",\"street1\":\"800 West Campbell Road\"," +
                "\"city\":\"Richardson\",\"postalCode\":\"75080\"," +
                "\"country\":{\"id\":\"US\"}}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(str);
        userPersonalInfo.setValue(value);

        return postUserPersonalInfo(userPersonalInfo);
    }

    private UserPersonalInfo postUserName(String userName, UserId userId) throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType("USERNAME");
        userPersonalInfo.setUserId(userId);
        String str = "{\"userName\":\"" + userName + "\"}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(str);
        userPersonalInfo.setValue(value);
        return postUserPersonalInfo(userPersonalInfo);
    }

    private UserPersonalInfo postName(UserId userId) throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType("NAME");
        userPersonalInfo.setUserId(userId);
        String str = "{\"givenName\":\"" + RandomFactory.getRandomStringOfAlphabet(5) +
                "\",\"middleName\":\"" + RandomFactory.getRandomStringOfAlphabet(5) +
                "\",\"familyName\":\"" + RandomFactory.getRandomStringOfAlphabet(5) + "\"}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(str);
        userPersonalInfo.setValue(value);
        return postUserPersonalInfo(userPersonalInfo);
    }

    private UserPersonalInfo postPhone(UserId userId) throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType("PHONE");
        userPersonalInfo.setUserId(userId);
        String str = "{\"info\":\"" + "86131" + RandomFactory.getRandomStringOfNumeric(8) + "\"}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(str);
        userPersonalInfo.setValue(value);

        return postUserPersonalInfo(userPersonalInfo);
    }

    private UserPersonalInfo postEmail(UserId userId) throws Exception {
        return postEmail(userId, RandomFactory.getRandomEmailAddress());
    }

    private UserPersonalInfo postEmail(UserId userId, String emailAddress) throws Exception {

        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType("EMAIL");
        userPersonalInfo.setUserId(userId);
        GregorianCalendar gc = new GregorianCalendar();
        userPersonalInfo.setLastValidateTime(gc.getTime());
        String str;
        if (emailAddress != null && !emailAddress.isEmpty()) {
            str = "{\"info\":\"" + emailAddress + "\"}";
        } else {
            str = "{\"info\":\"" + RandomFactory.getRandomEmailAddress() + "\"}";
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(str);
        userPersonalInfo.setValue(value);

        return postUserPersonalInfo(userPersonalInfo);
    }


    private UserPersonalInfo postUserPersonalInfo(UserPersonalInfo userPersonalInfo) throws Exception {
        return postUserPersonalInfo(userPersonalInfo, 201);
    }

    private UserPersonalInfo postUserPersonalInfo(UserPersonalInfo userPersonalInfo,
                                                  int expectedResponseCode) throws Exception {
        String serverURL = ConfigHelper.getSetting("defaultIdentityEndpoint") + "/personal-info";
        String responseBody = restApiCall(HTTPMethod.POST, serverURL, userPersonalInfo, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<UserPersonalInfo>() {
        }, responseBody);
    }

    private String postPassword(String uid, String pwd) throws Exception {
        Map params = new HashMap();
        String password;
        if (pwd != null && !pwd.isEmpty()) {
            password = pwd;
        } else {
            //password = RandomFactory.getRandomStringOfAlphabet(5);
            password = userPassword;
        }
        params.put("type", "PASSWORD");
        params.put("value", password);
        String requestBody = JSONObject.toJSONString(params);
        restApiCall(HTTPMethod.POST, identityServerURL + "/" + uid + "/" + "change-credentials",
                requestBody, 201, true);

        return password;
    }

    public String PostUser(User user) throws Exception {
        return PostUser(user, 201);
    }

    public String PostUser(User user, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.POST, identityServerURL, user, expectedResponseCode);
        User userGet = new JsonMessageTranscoder().decode(new TypeReference<User>() {
        },
                responseBody);
        String userId = IdConverter.idToHexString(userGet.getId());
        Master.getInstance().addUser(userId, userGet);

        return userId;
    }

    @Override
    public String PostUser(String userName, String emailAddress) throws Exception {
        User userForPost = new User();
        userForPost.setIsAnonymous(true);
        userForPost.setStatus("ACTIVE");

        String responseBody = restApiCall(HTTPMethod.POST, identityServerURL, userForPost, 201);
        User userGet = new JsonMessageTranscoder().decode(new TypeReference<User>() {
        },
                responseBody);

        String userId = IdConverter.idToHexString(userGet.getId());

        UserPersonalInfo userPersonalInfo = this.postUserName(StringUtils.isEmpty(userName) ? RandomFactory.getRandomStringOfAlphabet(10) : userName, userGet.getId());
        userGet.setIsAnonymous(false);
        userGet.setUsername(userPersonalInfo.getId());
        this.PutUser(userId, userGet);

        userGet = Master.getInstance().getUser(userId);
        Master.getInstance().addUser(userId, userGet);

        UserId userIdDefault = userGet.getId();

        //attach user email and address info
        UserPersonalInfo email = postEmail(userIdDefault, emailAddress);
        UserPersonalInfo address = postAddress(userIdDefault);

        UserPersonalInfoLink piEmail = new UserPersonalInfoLink();
        piEmail.setIsDefault(Boolean.TRUE);
        piEmail.setUserId(userIdDefault);
        piEmail.setValue(email.getId());

        UserPersonalInfoLink piAddress = new UserPersonalInfoLink();
        piAddress.setIsDefault(Boolean.TRUE);
        piAddress.setUserId(userIdDefault);
        piAddress.setValue(address.getId());

        List<UserPersonalInfoLink> addresses = new ArrayList<>();
        List<UserPersonalInfoLink> emails = new ArrayList<>();
        addresses.add(piAddress);
        emails.add(piEmail);

        userGet.setAddresses(addresses);
        userGet.setEmails(emails);

        this.PutUser(userId, userGet);

        return userId;
    }

    public String GetUserByUserId(String userId) throws Exception {
        return GetUserByUserId(userId, 200);
    }

    public String GetUserByUserId(String userId, int expectedResponseCode) throws Exception {

        String url = identityServerURL;
        if (userId != null && !userId.isEmpty()) {
            url = identityServerURL + "/" + userId;
        }

        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        User userGet = new JsonMessageTranscoder().decode(new TypeReference<User>() {
        },
                responseBody);


        String userPiiId = IdConverter.idToHexString(userGet.getUsername());
        String piiURL = identityPiiURL + "/" + userPiiId;
        String piiResponseBody = restApiCall(HTTPMethod.GET, piiURL);
        UserPersonalInfo userLoginName = new JsonMessageTranscoder().decode(new TypeReference<UserPersonalInfo>() {
        },
                piiResponseBody);
        UserLoginName loginName = (UserLoginName) JsonHelper.JsonNodeToObject(userLoginName.getValue(), UserLoginName.class);

        String userRtnId = IdConverter.idToHexString(userGet.getId());
        Master.getInstance().addUser(userRtnId, userGet);
        if (Master.getInstance().getUserAccessToken(userId) == null) {
            oAuthClient.postUserAccessToken(userRtnId, loginName.getUserName(), userPassword);
        }
        return userRtnId;
    }

    public List<String> GetUserByUserName(String userName) throws Exception {
        return GetUserByUserName(userName, 200);
    }

    public List<String> GetCurrentUserByUserName(String userName, int expectedResponseCode) throws Exception {
        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listUsername = new ArrayList<>();
        listUsername.add(userName);

        paraMap.put("username", listUsername);
        String responseBody = restApiCall(HTTPMethod.GET, identityServerURL, null, expectedResponseCode, paraMap, false);

        if (expectedResponseCode != 200) {
            return null;
        }
        Results<User> userGet = new JsonMessageTranscoder().decode(
                new TypeReference<Results<User>>() {
                }, responseBody);

        List<String> listUserId = new ArrayList<>();
        for (User user : userGet.getItems()) {
            Master.getInstance().addUser(IdConverter.idToHexString(user.getId()), user);
            if (Master.getInstance().getUserAccessToken(IdConverter.idToHexString(user.getId())) == null) {
                if (Master.getInstance().getUserPassword() != null && !Master.getInstance().getUserPassword().isEmpty())
                    oAuthClient.postUserAccessToken(IdConverter.idToHexString(user.getId()), userName,
                            Master.getInstance().getUserPassword());
                else {
                    oAuthClient.postUserAccessToken(IdConverter.idToHexString(user.getId()), userName, userPassword);
                }
            }
            listUserId.add(IdConverter.idToHexString(user.getId()));
        }
        if (listUserId.size() > 0) {
            Master.getInstance().setCurrentUid(listUserId.get(0));
        }

        return listUserId;
    }

    public List<String> GetUserByUserName(String userName, int expectedResponseCode) throws Exception {
        oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.IDENTITY);
        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listUsername = new ArrayList<>();
        listUsername.add(userName);

        paraMap.put("username", listUsername);
        String responseBody = restApiCall(HTTPMethod.GET, identityServerURL, null, expectedResponseCode, paraMap, true);

        Results<User> userGet = new JsonMessageTranscoder().decode(
                new TypeReference<Results<User>>() {
                }, responseBody);

        List<String> listUserId = new ArrayList<>();
        for (User user : userGet.getItems()) {
            Master.getInstance().addUser(IdConverter.idToHexString(user.getId()), user);
            if (Master.getInstance().getUserAccessToken(IdConverter.idToHexString(user.getId())) == null) {
                if (Master.getInstance().getUserPassword() != null && !Master.getInstance().getUserPassword().isEmpty())
                    oAuthClient.postUserAccessToken(IdConverter.idToHexString(user.getId()), userName,
                            Master.getInstance().getUserPassword());
                else {
                    oAuthClient.postUserAccessToken(IdConverter.idToHexString(user.getId()), userName, userPassword);
                }
            }
            listUserId.add(IdConverter.idToHexString(user.getId()));
        }
        if (listUserId.size() > 0) {
            Master.getInstance().setCurrentUid(listUserId.get(0));
        }

        return listUserId;
    }

    public String PutUser(String userId, User user) throws Exception {
        return PutUser(userId, user, 200);
    }

    public String PutUser(String userId, User user, int expectedResponseCode) throws Exception {

        String putUrl = identityServerURL + "/" + userId;
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, user, expectedResponseCode);
        User userPut = new JsonMessageTranscoder().decode(new TypeReference<User>() {
        },
                responseBody);
        String userRtnId = IdConverter.idToHexString(userPut.getId());
        Master.getInstance().addUser(userRtnId, userPut);

        return userRtnId;
    }

    public com.junbo.common.error.Error PutUserWithError(String userId, User user, int expectedResponseCode, String errorCode) throws Exception {

        String putUrl = identityServerURL + "/" + userId;
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, user, expectedResponseCode);
        Error error = new JsonMessageTranscoder().decode(new TypeReference<Error>() {
        }, responseBody);

        assert error.getCode().equalsIgnoreCase(errorCode);

        return error;
    }

    @Override
    public String PostEmailVerification(String userId, String country, String locale) throws Exception {
        return PostEmailVerification(userId, country, locale, 204);
    }

    @Override
    public String PostEmailVerification(String userId, String country, String locale,
                                        int expectedResponseCode) throws Exception {
        return oAuthClient.postEmailVerification(userId, country, locale, expectedResponseCode);
    }

    @Override
    public String UpdateUserPersonalInfo(String uid, String userName, String password) throws Exception {
        GetUserByUserId(uid);
        User userGet = Master.getInstance().getUser(uid);

        String userId = IdConverter.idToHexString(userGet.getId());

        List<UserPersonalInfoLink> addresses = new ArrayList<>();
        List<UserPersonalInfoLink> phones = new ArrayList<>();

        UserId userIdDefault = userGet.getId();

        UserPersonalInfo address;

        address = postAddress(userIdDefault);
        UserPersonalInfoLink piAddress = new UserPersonalInfoLink();
        piAddress.setIsDefault(Boolean.TRUE);
        piAddress.setUserId(userIdDefault);
        piAddress.setValue(address.getId());
        piAddress.setIsDefault(true);
        addresses.add(piAddress);

        UserPersonalInfo phone = postPhone(userIdDefault);
        UserPersonalInfo name = postName(userIdDefault);


        UserPersonalInfoLink piPhone = new UserPersonalInfoLink();
        piPhone.setIsDefault(true);
        piPhone.setUserId(userIdDefault);
        piPhone.setValue(phone.getId());

        phones.add(piPhone);

        userGet.setAddresses(addresses);
        userGet.setPhones(phones);
        userGet.setName(name.getId());

        this.PutUser(userId, userGet);

        return userId;
    }

}
