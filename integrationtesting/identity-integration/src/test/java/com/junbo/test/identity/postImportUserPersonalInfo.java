/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.identity.spec.v1.model.*;
import com.junbo.identity.spec.v1.model.migration.OculusInput;
import com.junbo.identity.spec.v1.model.migration.OculusOutput;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.RandomHelper;
import com.junbo.test.common.Validator;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dw
 */
public class postImportUserPersonalInfo {

    @BeforeClass(alwaysRun = true)
    public void run() throws Exception {
        HttpclientHelper.CreateHttpClient();
        Identity.GetHttpAuthorizationHeaderForMigration();
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

    @Test(groups = "dailies")
    public void importMigrationData() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        OculusOutput oculusOutput = Identity.ImportMigrationData(oculusInput);

        User user = Identity.UserGetByUserId(oculusOutput.getUserId());
        UserPersonalInfo userLoginInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(user.getUsername());
        UserLoginName userLoginName = (UserLoginName) JsonHelper.JsonNodeToObject(userLoginInfo.getValue(), UserLoginName.class);
        Validator.Validate("validate user name", oculusInput.getUsername(), userLoginName.getUserName());
        Validator.Validate("validate user status",
                oculusInput.getStatus().equals(IdentityModel.MigrateUserStatus.ARCHIVE.name()) ?
                        "DELETED" : "ACTIVE", user.getStatus()
        );

        UserCredential uc = Identity.CredentialsGetByUserId(user.getId());
        Validator.Validate("validate forceResetPassword", oculusInput.getForceResetPassword(),
                uc.getChangeAtNextLogin());

        List<UserPersonalInfo> userPersonalInfos = Identity.UserPersonalInfosGetByUserId(user.getId());
        for (UserPersonalInfo upi : userPersonalInfos) {
            if (upi.getType().equals("NAME")) {
                UserName userName = (UserName) JsonHelper.JsonNodeToObject(upi.getValue(), UserName.class);
                Validator.Validate("validate given name", oculusInput.getFirstName(), userName.getGivenName());
                Validator.Validate("validate family name", oculusInput.getLastName(), userName.getFamilyName());
            } else if (upi.getType().equals("EMAIL")) {
                Email email = (Email) JsonHelper.JsonNodeToObject(upi.getValue(), Email.class);
                Validator.Validate("validate email", oculusInput.getEmail(), email.getInfo());
            } else if (upi.getType().equals("GENDER")) {
                UserGender userGender = (UserGender) JsonHelper.JsonNodeToObject(upi.getValue(), UserGender.class);
                Validator.Validate("validate gender", oculusInput.getGender(), userGender.getInfo());
            } else if (upi.getType().equals("DOB")) {
                UserDOB userDob = (UserDOB) JsonHelper.JsonNodeToObject(upi.getValue(), UserDOB.class);
                Validator.Validate("validate dob", oculusInput.getDob(), userDob.getInfo());
            } else if (upi.getType().equals("profile")) {
                UserProfile userProfile = (UserProfile) JsonHelper.JsonNodeToObject(upi.getValue(), UserProfile.class);
                Validator.Validate("validate user profile headline", oculusInput.getProfile().getHeadline(),
                        userProfile.getHeadline());
                Validator.Validate("validate user profile summary", oculusInput.getProfile().getSummary(),
                        userProfile.getSummary());
                Validator.Validate("validate user profile url", oculusInput.getProfile().getUrl(),
                        userProfile.getWebpage());
                Validator.Validate("validate user profile avatar",
                        JsonHelper.ObjectToJsonNode(oculusInput.getProfile().getAvatar()),
                        JsonHelper.ObjectToJsonNode(userProfile.getAvatar()));
            }
        }

        /*
        Organization organization = Identity.OrganizationGetByOrganizationId(oculusOutput.getOrganizationId());
        Validator.Validate("validate organization name", oculusInput.getCompany().getName(), organization.getName());
        Validator.Validate("validate organization type", oculusInput.getCompany().getType(), organization.getType());
        Validator.Validate("validate imported organization isValidated == false", false, organization.getIsValidated());
        UserPersonalInfo companyAddress = Identity.UserPersonalInfoGetByUserPersonalInfoId(
                organization.getShippingAddress());
        Validator.Validate("validate company address type", IdentityModel.UserPersonalInfoType.ADDRESS.name(),
                companyAddress.getType());
        Address address = (Address) JsonHelper.JsonNodeToObject(companyAddress.getValue(), Address.class);
        Validator.Validate("validate company address street", oculusInput.getCompany().getAddress(),
                address.getStreet1());
        Validator.Validate("validate company address city", oculusInput.getCompany().getCity(),
                address.getCity());
        Validator.Validate("validate company address state", oculusInput.getCompany().getState(),
                address.getStreet2());
        Validator.Validate("validate company address postal code", oculusInput.getCompany().getPostalCode(),
                address.getPostalCode());
        UserPersonalInfo companyPhone = Identity.UserPersonalInfoGetByUserPersonalInfoId(
                organization.getShippingPhone());
        Validator.Validate("validate company phone type", IdentityModel.UserPersonalInfoType.PHONE.name(),
                companyPhone.getType());
        PhoneNumber phoneNumber = (PhoneNumber) JsonHelper.JsonNodeToObject(companyPhone.getValue(), PhoneNumber.class);
        Validator.Validate("validate company phone number", oculusInput.getCompany().getPhone(),
                phoneNumber.getInfo());
        */
    }

    @Test(groups = "dailies")
    public void importMigrationDataWithActiveStatus() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        oculusInput.setStatus("ACTIVE");
        OculusOutput oculusOutput = Identity.ImportMigrationData(oculusInput);

        User user = Identity.UserGetByUserId(oculusOutput.getUserId());
        UserPersonalInfo userLoginInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(user.getUsername());
        UserLoginName userLoginName = (UserLoginName) JsonHelper.JsonNodeToObject(userLoginInfo.getValue(), UserLoginName.class);
        Validator.Validate("validate user name", oculusInput.getUsername(), userLoginName.getUserName());
        Validator.Validate("validate user status",
                oculusInput.getStatus().equals(IdentityModel.MigrateUserStatus.ARCHIVE.name()) ?
                        "DELETED" : "ACTIVE", user.getStatus()
        );

        UserCredential uc = Identity.CredentialsGetByUserId(user.getId());
        Validator.Validate("validate forceResetPassword", oculusInput.getForceResetPassword(),
                uc.getChangeAtNextLogin());

        List<UserPersonalInfo> userPersonalInfos = Identity.UserPersonalInfosGetByUserId(user.getId());
        for (UserPersonalInfo upi : userPersonalInfos) {
            if (upi.getType().equals("NAME")) {
                UserName userName = (UserName) JsonHelper.JsonNodeToObject(upi.getValue(), UserName.class);
                Validator.Validate("validate given name", oculusInput.getFirstName(), userName.getGivenName());
                Validator.Validate("validate family name", oculusInput.getLastName(), userName.getFamilyName());
            } else if (upi.getType().equals("EMAIL")) {
                Email email = (Email) JsonHelper.JsonNodeToObject(upi.getValue(), Email.class);
                Validator.Validate("validate email", oculusInput.getEmail(), email.getInfo());
                Validator.Validate("validate email verified", true, upi.getIsValidated());
            } else if (upi.getType().equals("GENDER")) {
                UserGender userGender = (UserGender) JsonHelper.JsonNodeToObject(upi.getValue(), UserGender.class);
                Validator.Validate("validate gender", oculusInput.getGender(), userGender.getInfo());
            } else if (upi.getType().equals("DOB")) {
                UserDOB userDob = (UserDOB) JsonHelper.JsonNodeToObject(upi.getValue(), UserDOB.class);
                Validator.Validate("validate dob", oculusInput.getDob(), userDob.getInfo());
            } else if (upi.getType().equals("profile")) {
                UserProfile userProfile = (UserProfile) JsonHelper.JsonNodeToObject(upi.getValue(), UserProfile.class);
                Validator.Validate("validate user profile headline", oculusInput.getProfile().getHeadline(),
                        userProfile.getHeadline());
                Validator.Validate("validate user profile summary", oculusInput.getProfile().getSummary(),
                        userProfile.getSummary());
                Validator.Validate("validate user profile url", oculusInput.getProfile().getUrl(),
                        userProfile.getWebpage());
                Validator.Validate("validate user profile avatar",
                        JsonHelper.ObjectToJsonNode(oculusInput.getProfile().getAvatar()),
                        JsonHelper.ObjectToJsonNode(userProfile.getAvatar()));
            }
        }
    }

    @Test(groups = "dailies")
    public void importMigrationDataWithDuplicateUserNameDifferentCurrentId() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        Identity.ImportMigrationData(oculusInput);
        oculusInput.setCurrentId(RandomHelper.randomLong());
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(Identity.IdentityV1ImportsURI,
                JsonHelper.JsonSerializer(oculusInput), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response error code", 400, response.getStatusLine().getStatusCode());
        String errorMessage = "Field value is invalid. username is already used by others";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
    }

    @Test(groups = "dailies")
    public void importMigrationDataWithDuplicateCompanyNameDifferentCompanyId() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        OculusOutput oculusOutput1 = Identity.ImportMigrationData(oculusInput);
        /*
        Company company = oculusInput.getCompany();
        company.setCompanyId(RandomHelper.randomLong());
        oculusInput.setCompany(company);
        OculusOutput oculusOutput2 = Identity.ImportMigrationData(oculusInput);
        Validator.Validate("validate created 2 companies", true,
                oculusOutput1.getOrganizationId() != oculusOutput2.getOrganizationId());
        */
    }

    @Test(groups = "dailies")
    public void importMigrationDataWithDuplicateEmailDifferentCurrentIdDifferentUserName() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        Identity.ImportMigrationData(oculusInput);
        oculusInput.setCurrentId(RandomHelper.randomLong());
        oculusInput.setUsername(RandomHelper.randomAlphabetic(20));
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(Identity.IdentityV1ImportsURI,
                JsonHelper.JsonSerializer(oculusInput), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response error code", 409, response.getStatusLine().getStatusCode());
        String errorMessage = String.format("Email %s is already used", oculusInput.getEmail());
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
    }

    @Test(groups = "dailies")
    public void importMigrationDataWithDuplicateCurrentIdDifferentUserNameDifferentEmail() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        OculusOutput oculusOutput = Identity.ImportMigrationData(oculusInput);
        User user1 = Identity.UserGetByUserId(oculusOutput.getUserId());
        UserPersonalInfoLink upil1 = user1.getEmails().get(0);
        UserPersonalInfo upi1 = Identity.UserPersonalInfoGetByUserPersonalInfoId(upil1.getValue());
        Validator.Validate("validate original email", true, upi1.getValue().toString().contains(oculusInput.getEmail()));

        String newUserName = RandomHelper.randomAlphabetic(20);
        String newUserEmail = RandomHelper.randomAlphabetic(10) + "@163.com";
        oculusInput.setUsername(newUserName);
        oculusInput.setEmail(newUserEmail);
        Identity.ImportMigrationData(oculusInput);
        User user2 = Identity.UserGetByUserId(oculusOutput.getUserId());
        UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(user2.getUsername());
        UserLoginName userLoginName = (UserLoginName) JsonHelper.JsonNodeToObject(userPersonalInfo.getValue(), UserLoginName.class);
        Validator.Validate("validate user name is updated", newUserName, userLoginName.getUserName());
        UserPersonalInfoLink upil2 = user2.getEmails().get(0);
        UserPersonalInfo upi2 = Identity.UserPersonalInfoGetByUserPersonalInfoId(upil2.getValue());
        Validator.Validate("validate original email", true, upi2.getValue().toString().contains(newUserEmail));
    }

    @Test(groups = "dailies")
    public void importMigrationDataWithDuplicateCompanyIdDifferentCompanyName() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        OculusOutput oculusOutput = Identity.ImportMigrationData(oculusInput);
        String newCompanyName = RandomHelper.randomAlphabetic(20);
        /*
        Company company = oculusInput.getCompany();
        company.setName(newCompanyName);
        oculusInput.setCompany(company);
        Identity.ImportMigrationData(oculusInput);
        Organization organization = Identity.OrganizationGetByOrganizationId(oculusOutput.getOrganizationId());
        Validator.Validate("validate company name is updated", newCompanyName, organization.getName());
        */
    }

    @Test(groups = "dailies")
    public void importMigrationDataWithRandomPassword() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        oculusInput.setPassword(RandomHelper.randomAlphabetic(80));
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(
                Identity.IdentityV1ImportsURI, JsonHelper.JsonSerializer(oculusInput),
                HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response error code", 500, response.getStatusLine().getStatusCode());
        String errorMessage = "password only accept version 1";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
    }

    @Test(groups = "dailies")
    public void importMigrationDataValidateUserOrganizationGroup() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        OculusOutput oculusOutput = Identity.ImportMigrationData(oculusInput);
        /*
        Boolean userIsAdmin = oculusInput.getCompany().getIsAdmin();
        Group group = Identity.SearchOrganizationGroup(oculusOutput.getOrganizationId(),
                userIsAdmin ? IdentityModel.MigrateCompanyGroup.admin.name()
                        : IdentityModel.MigrateCompanyGroup.developer.name()
        );
        UserGroup userGroup = Identity.SearchUserGroup(group.getId(), false);
        Validator.Validate("validate user is in correct group", oculusOutput.getUserId(), userGroup.getUserId());
        */
    }

    @Test(groups = "dailies")
    public void importMigrationDataSwitchUserOrganizationGroup() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        OculusOutput oculusOutput = Identity.ImportMigrationData(oculusInput);
        /*
        Boolean userIsAdmin = oculusInput.getCompany().getIsAdmin();
        Company company = oculusInput.getCompany();
        company.setIsAdmin(userIsAdmin ? false : true);
        Identity.ImportMigrationData(oculusInput);
        userIsAdmin = oculusInput.getCompany().getIsAdmin();
        Group groupWithUser = Identity.SearchOrganizationGroup(oculusOutput.getOrganizationId(),
                userIsAdmin ? IdentityModel.MigrateCompanyGroup.admin.name()
                        : IdentityModel.MigrateCompanyGroup.developer.name()
        );
        UserGroup userGroup = Identity.SearchUserGroup(groupWithUser.getId(), false);
        Validator.Validate("validate user is in correct group", oculusOutput.getUserId(), userGroup.getUserId());
        Group groupWithoutUser = Identity.SearchOrganizationGroup(oculusOutput.getOrganizationId(),
                userIsAdmin ? IdentityModel.MigrateCompanyGroup.developer.name()
                        : IdentityModel.MigrateCompanyGroup.admin.name()
        );
        Identity.SearchUserGroup(groupWithoutUser.getId(), true);
        */
    }

    @Test(groups = "dailies")
    public void importMigrationDataUserSwitchOrganization() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        OculusOutput oculusOutput1 = Identity.ImportMigrationData(oculusInput);
        /*
        Company company = oculusInput.getCompany();
        company.setCompanyId(RandomHelper.randomLong());
        company.setName(RandomHelper.randomAlphabetic(20));
        oculusInput.setCompany(company);
        OculusOutput oculusOutput2 = Identity.ImportMigrationData(oculusInput);

        Validator.Validate("validate there are 2 organizations generated", true,
                oculusOutput1.getOrganizationId() != oculusOutput2.getOrganizationId());

        Boolean userIsAdmin = oculusInput.getCompany().getIsAdmin();
        Group group1 = Identity.SearchOrganizationGroup(oculusOutput1.getOrganizationId(),
                userIsAdmin ? IdentityModel.MigrateCompanyGroup.admin.name()
                        : IdentityModel.MigrateCompanyGroup.developer.name()
        );
        Identity.SearchUserGroup(group1.getId(), true);
        Group group2 = Identity.SearchOrganizationGroup(oculusOutput2.getOrganizationId(),
                userIsAdmin ? IdentityModel.MigrateCompanyGroup.admin.name()
                        : IdentityModel.MigrateCompanyGroup.developer.name()
        );
        UserGroup userGroup = Identity.SearchUserGroup(group2.getId(), false);
        Validator.Validate("validate user is in correct group", oculusOutput1.getUserId(), userGroup.getUserId());
        */
    }

    @Test(groups = "dailies")
    public void importMigrationDataUserLogin() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        oculusInput.setStatus(IdentityModel.MigrateUserStatus.ACTIVE.name());
        oculusInput.setPassword("1:lbQHMu5377aBJxK5xMDc:Ur8nE66R5Qr7S1a4b1bO:"
                + "a9015659a978b37441d76d7839fa66b847e32311f5fd1da65130ca5051dd6ef1");
        Identity.ImportMigrationData(oculusInput);
        CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(
                oculusInput.getEmail(), "oculustest1234", false);
        Validator.Validate("validate response code", 412, response.getStatusLine().getStatusCode());

        oculusInput = IdentityModel.DefaultOculusInput();
        oculusInput.setStatus(IdentityModel.MigrateUserStatus.ACTIVE.name());
        oculusInput.setPassword("1:8UFAbK26VrPLL75jE9P2:PRo4D7r23hrfv3FBxqBv:"
                + "b87637b9ec5abd43db01d7a299612a49550230a813239fb3e28eec2a88c0df67");
        Identity.ImportMigrationData(oculusInput);
        Identity.UserCredentialAttemptesPostDefault(oculusInput.getEmail(), "radiant555");
    }

    @Test(groups = "dailies")
    public void importMigrationDataUserStatusChange() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        oculusInput.setStatus(IdentityModel.MigrateUserStatus.PENDING.toString());
        oculusInput.setPassword("1:lbQHMu5377aBJxK5xMDc:Ur8nE66R5Qr7S1a4b1bO:"
                + "a9015659a978b37441d76d7839fa66b847e32311f5fd1da65130ca5051dd6ef1");
        OculusOutput output = Identity.ImportMigrationData(oculusInput);
        User user = Identity.UserGetByUserId(output.getUserId());
        UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(user.getEmails().get(0).getValue());
        UserPersonalInfoId id = user.getEmails().get(0).getValue();
        Email email = (Email)JsonHelper.JsonNodeToObject(userPersonalInfo.getValue(), Email.class);
        Validator.Validate("Validate Email equals", oculusInput.getEmail(), email.getInfo());
        Validator.Validate("Validate EmailVerified status", true, userPersonalInfo.getLastValidateTime() == null);

        oculusInput.setStatus(IdentityModel.MigrateUserStatus.ACTIVE.toString());
        output = Identity.ImportMigrationData(oculusInput);
        user = Identity.UserGetByUserId(output.getUserId());
        userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(user.getEmails().get(0).getValue());
        email = (Email)JsonHelper.JsonNodeToObject(userPersonalInfo.getValue(), Email.class);
        Validator.Validate("Validate Email equals", oculusInput.getEmail(), email.getInfo());
        Validator.Validate("Validate EmailVerified status", true, userPersonalInfo.getLastValidateTime() != null);
        Validator.Validate("Validate Update operation", id, user.getEmails().get(0).getValue());

        oculusInput.setStatus(IdentityModel.MigrateUserStatus.PENDING.toString());
        output = Identity.ImportMigrationData(oculusInput);
        user = Identity.UserGetByUserId(output.getUserId());
        userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(user.getEmails().get(0).getValue());
        email = (Email)JsonHelper.JsonNodeToObject(userPersonalInfo.getValue(), Email.class);
        Validator.Validate("Validate Email equals", oculusInput.getEmail(), email.getInfo());
        Validator.Validate("Validate EmailVerified status", true, userPersonalInfo.getLastValidateTime() == null);
        Validator.Validate("Validate Update operation", id, user.getEmails().get(0).getValue());
    }

    @Test(groups = "dailies")
    public void importMigrationDataNoDuplicateUpdate() throws Exception {
        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        OculusOutput oculusOutput = Identity.ImportMigrationData(oculusInput);
        Validator.Validate("Validate organization null", true, oculusOutput.getOrganizationId() == null);
        /*
        User user1 = Identity.UserGetByUserId(oculusOutput.getUserId());
        Organization organization1 = Identity.OrganizationGetByOrganizationId(oculusOutput.getOrganizationId());

        oculusOutput = Identity.ImportMigrationData(oculusInput);
        User user2 = Identity.UserGetByUserId(oculusOutput.getUserId());
        Organization organization2 = Identity.OrganizationGetByOrganizationId(oculusOutput.getOrganizationId());

        Validator.Validate("Validate username not updated", user1.getUsername(), user2.getUsername());
        Validator.Validate("Validate name not updated", user1.getName(), user2.getName());
        Validator.Validate("Validate dob not updated", user1.getDob(), user2.getDob());
        Validator.Validate("Validate emails not updated", user1.getEmails().get(0).getValue(), user2.getEmails().get(0).getValue());
        Validator.Validate("Validate userId not updated", user1.getId(), user2.getId());
        Validator.Validate("Validate version not updated", user1.getRev(), user2.getRev());

        Validator.Validate("Validate organization Id", organization1.getId(), organization2.getId());
        Validator.Validate("Validate organization revision", organization1.getRev(), organization2.getRev());
        */
    }

    @Test(groups = "dailies")
    public void importMigrationDataWithUserId() throws Exception {
        User user = IdentityModel.DefaultUser();
        User userPosted = createUser(RandomHelper.randomAlphabetic(15), RandomHelper.randomAlphabetic(15));

        OculusInput oculusInput = IdentityModel.DefaultOculusInput();
        oculusInput.setUserId(userPosted.getId());
        OculusOutput oculusOutput = Identity.ImportMigrationData(oculusInput);

        User migratedUser = Identity.UserGetByUserId(oculusOutput.getUserId());
        Validator.Validate("Validate userId correct", userPosted.getId(), migratedUser.getId());

        oculusInput = IdentityModel.DefaultOculusInput();
        oculusInput.setCurrentId(null);

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(
                Identity.IdentityV1ImportsURI, JsonHelper.JsonSerializer(oculusInput), HttpclientHelper.HttpRequestType.post, nvps);

        String errorMessage = "Migration must have user's currentId or userId";
        Validator.Validate("Validate response code", 400, response.getStatusLine().getStatusCode());

        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));

        response.close();
    }

    protected static User createUser(String username, String nickName) throws Exception {
        User user = IdentityModel.DefaultUser();
        user.setNickName(nickName);
        user = Identity.UserPostDefault(user);
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setUserId(user.getId());
        userPersonalInfo.setType(IdentityModel.UserPersonalInfoType.USERNAME.toString());
        UserLoginName loginName = new UserLoginName();
        loginName.setUserName(username);
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(loginName));
        UserPersonalInfo loginInfo = Identity.UserPersonalInfoPost(user.getId(), userPersonalInfo);
        user.setIsAnonymous(false);
        user.setUsername(loginInfo.getId());
        return Identity.UserPut(user);
    }
}