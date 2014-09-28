/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Communication;
import com.junbo.identity.spec.v1.model.CommunicationLocale;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.RandomHelper;
import com.junbo.test.common.Validator;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangfu on 8/12/14.
 */
public class postCommunication {

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

    @Property(
            priority = Priority.BVT,
            component = Component.Identity,
            owner = "JieFeng",
            status = Status.Enable,
            description = "Test communication POST/PUT/GET",
            environment = "onebox",
            steps = {
                    "1. post a communication" +
                            "/n 2. get the communication" +
                            "/n 3. update the communication" +
                            "/n 4. search the communication"
            }
    )
    @Test(groups = "bvt")
    public void testCommunication() throws Exception {
        Communication communication = IdentityModel.DefaultCommunication();
        Communication posted = Identity.CommunicationDefault(communication);

        Communication gotten = Identity.CommunicationGet(posted.getId().toString(), null);
        gotten = Identity.CommunicationPut(gotten);
        Results<Communication> results = Identity.CommunicationSearch(null, null);
        assert results.getItems().size() != 0;
    }

    @Property(
            priority = Priority.Dailies,
            component = Component.Identity,
            owner = "JieFeng",
            status = Status.Enable,
            description = "Test communication Not valid Locale POST",
            environment = "onebox",
            steps = {
                    "1. post a communication that contains invalid locale"
            }
    )
    @Test(groups = "dailies")
    public void testCommunicationNotValidLocale() throws Exception {
        Communication communication = IdentityModel.DefaultCommunication();
        CommunicationLocale communicationLocale = new CommunicationLocale();
        communicationLocale.setDescription(RandomHelper.randomAlphabetic(100));
        communicationLocale.setName(RandomHelper.randomAlphabetic(100));
        String randomLocale = RandomHelper.randomAlphabetic(2) + "_" + "XX";
        communication.getLocales().put(randomLocale, JsonHelper.ObjectToJsonNode(communicationLocale));
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));

        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(
                Identity.IdentityV1CommunicationURI, JsonHelper.JsonSerializer(communication), HttpclientHelper.HttpRequestType.post, nvps);

        Validator.Validate("validate response code", 400, response.getStatusLine().getStatusCode());

        String errorMessage = "Input Error";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();

        communication.getLocales().remove(randomLocale);
        Identity.CommunicationDefault(communication);
    }

    @Property(
            priority = Priority.Dailies,
            component = Component.Identity,
            owner = "JieFeng",
            status = Status.Enable,
            description = "Test posted communication Get by Locale",
            environment = "onebox",
            steps = {
                    "1. post a communication" +
                            "/n 2. test get communication by locale"
            }
    )
    @Test(groups = "dailies")
    public void testCommunicationGet() throws Exception {
        Communication communication = new Communication();
        List<CountryId> regions = new ArrayList<>();
        regions.add(new CountryId("US"));
        regions.add(new CountryId("CN"));
        communication.setRegions(regions);

        List<LocaleId> translations = new ArrayList<>();
        translations.add(new LocaleId("en_US"));
        translations.add(new LocaleId("zh_CN"));
        communication.setTranslations(translations);

        CommunicationLocale enUSCommunicationLocale = new CommunicationLocale();
        enUSCommunicationLocale.setDescription("SilkCloudTest_Description_enUS");
        enUSCommunicationLocale.setName("SilkCloudTest_Name_enUS");

        CommunicationLocale zhCNCommunicationLocale = new CommunicationLocale();
        zhCNCommunicationLocale.setDescription("SilkCloudTest_Description_zhCN");
        zhCNCommunicationLocale.setName("SilkCloudTest_Name_zhCN");

        Map<String, JsonNode> locales = new HashMap<>();
        locales.put("en_US", JsonHelper.ObjectToJsonNode(enUSCommunicationLocale));
        locales.put("zh_CN", JsonHelper.ObjectToJsonNode(zhCNCommunicationLocale));
        communication.setLocales(locales);

        Communication posted = Identity.CommunicationDefault(communication);

        Communication gotten = Identity.CommunicationGet(posted.getId().toString(), "en_US");
        assert gotten.getLocales().get("zh_CN") == null;
        assert gotten.getLocales().get("en_US") != null;
        Validator.Validate("Validate Locale Accuracy", gotten.getLocaleAccuracy(), "HIGH");
        CommunicationLocale communicationLocale = (CommunicationLocale) JsonHelper.JsonNodeToObject(gotten.getLocales().get("en_US"),
                CommunicationLocale.class);
        Validator.Validate("Validate locale description value", communicationLocale.getDescription(), "SilkCloudTest_Description_enUS");
        Validator.Validate("Validate Locale name value", communicationLocale.getName(), "SilkCloudTest_Name_enUS");

        gotten = Identity.CommunicationGet(posted.getId().toString(), "zh_CN");
        assert gotten.getLocales().get("zh_CN") != null;
        assert gotten.getLocales().get("en_US") == null;
        Validator.Validate("Validate Locale Accuracy", gotten.getLocaleAccuracy(), "HIGH");
        communicationLocale = (CommunicationLocale) JsonHelper.JsonNodeToObject(gotten.getLocales().get("zh_CN"),
                CommunicationLocale.class);
        Validator.Validate("Validate locale description value", communicationLocale.getDescription(), "SilkCloudTest_Description_zhCN");
        Validator.Validate("Validate Locale name value", communicationLocale.getName(), "SilkCloudTest_Name_zhCN");

        gotten = Identity.CommunicationGet(posted.getId().toString(), "ja_JP");
        assert gotten.getLocales().get("zh_CN") == null;
        assert gotten.getLocales().get("en_US") == null;
        assert gotten.getLocales().get("ja_JP") != null;
        Validator.Validate("Validate Locale Accuracy", gotten.getLocaleAccuracy(), "LOW");
        communicationLocale = (CommunicationLocale) JsonHelper.JsonNodeToObject(gotten.getLocales().get("ja_JP"),
                CommunicationLocale.class);
        Validator.Validate("Validate locale description value", communicationLocale.getDescription(), "SilkCloudTest_Description_enUS");
        Validator.Validate("Validate Locale name value", communicationLocale.getName(), "SilkCloudTest_Name_enUS");
    }

    @Property(
            priority = Priority.Dailies,
            component = Component.Identity,
            owner = "JieFeng",
            status = Status.Enable,
            description = "Test communication Search",
            environment = "onebox",
            steps = {
                    "1. post a communication" +
                            "/n 2. search the communication"
            }
    )
    @Test(groups = "dailies")
    public void testCommunicationSearch() throws Exception {
        Communication communication = IdentityModel.DefaultCommunication();
        Communication posted = Identity.CommunicationDefault(communication);

        Results<Communication> communications = Identity.CommunicationSearch(null, null);
        assert communications.getItems().size() != 0;

        communications = Identity.CommunicationSearch("US", null);
        assert communications.getItems().size() != 0;
        for (int index = 0; index < communications.getItems().size(); index++) {
            Communication gotten = communications.getItems().get(index);
            assert gotten.getRegions().size() != 0;
            int regionIndex = 0;
            for (; regionIndex < gotten.getRegions().size(); regionIndex++) {
                if (gotten.getRegions().get(regionIndex).getValue().equalsIgnoreCase("US")) {
                    break;
                }
            }

            assert regionIndex != gotten.getRegions().size();
        }

        communications = Identity.CommunicationSearch("US", "en_US");
        assert communications.getItems().size() != 0;
        for (int index = 0; index < communications.getItems().size(); index++) {
            Communication gotten = communications.getItems().get(index);
            assert gotten.getRegions().size() != 0;
            int regionIndex = 0;
            for (; regionIndex < gotten.getRegions().size(); regionIndex++) {
                if (gotten.getRegions().get(regionIndex).getValue().equalsIgnoreCase("US")) {
                    break;
                }
            }
            assert regionIndex != gotten.getRegions().size();
            for (regionIndex = 0; regionIndex < gotten.getTranslations().size(); regionIndex++) {
                if (gotten.getTranslations().get(regionIndex).getValue().equalsIgnoreCase("en_US")) {
                    break;
                }
            }
            assert regionIndex != gotten.getTranslations().size();
        }

        communications = Identity.CommunicationSearch("US", "ja_JP");
        assert communications.getItems().size() == 0;
    }
}
