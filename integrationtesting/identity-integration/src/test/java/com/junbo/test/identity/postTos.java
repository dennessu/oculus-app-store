/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.Tos;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.RandomHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 10/11/14.
 */
public class postTos {

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

    @Test(groups = "bvt")
    // This is the basic
    public void postTos() throws Exception {
        Tos tos = IdentityModel.DefaultTos();
        tos = Identity.TosPostDefault(tos);

        Tos tosGet = Identity.TosGet(tos.getId());
        assert tos.getState().equalsIgnoreCase(tosGet.getState());
        assert tos.getType().equalsIgnoreCase(tosGet.getType());
        assert tos.getContent().equalsIgnoreCase(tosGet.getContent());
        assert tos.getTitle().equalsIgnoreCase(tosGet.getTitle());
    }

    @Test(groups = "dailies")
    //https://oculus.atlassian.net/browse/SER-676
    public void postTosSearch() throws Exception {
        List<String> supportedCountries = new ArrayList<>();
        supportedCountries.add("US");
        supportedCountries.add("CN");
        supportedCountries.add("ZZ");
        Tos tos1 = IdentityModel.DefaultTos(RandomHelper.randomAlphabetic(15), "EULA", "APPROVED", supportedCountries);
        Tos tos1Get = Identity.TosPostDefault(tos1);

        Tos tos2 = IdentityModel.DefaultTos(RandomHelper.randomAlphabetic(15), "TOS", "DRAFT", supportedCountries);
        Tos tos2Get = Identity.TosPostDefault(tos2);

        List<Tos> tosList = Identity.TosSearch(RandomHelper.randomAlphabetic(15), null, null, null);
        assert tosList.size() == 0;
        tosList = Identity.TosSearch(tos1.getTitle(), null, null, null);
        assert tosList.size() == 1;

        tosList = Identity.TosSearch(null, RandomHelper.randomAlphabetic(15), null, null);
        assert tosList.size() == 0;
        tosList = Identity.TosSearch(null, tos1.getType(), null, null);
        assert tosList.size() != 0;

        tosList = Identity.TosSearch(null, null, RandomHelper.randomAlphabetic(15), null);
        assert tosList.size() == 0;
        tosList = Identity.TosSearch(null, null, "APPROVED", null);
        assert tosList.size() != 0;

        tosList = Identity.TosSearch(null, null, null, "HK");
        assert tosList.size() > 0;
        tosList = Identity.TosSearch(null, null, null, "US");
        assert tosList.size() > 0;

        tosList = Identity.TosSearch(tos1.getTitle(), null, null, null);
        assert tosList.size() == 1;

        tosList = Identity.TosSearch(tos1.getTitle(), "EULA", null, null);
        assert tosList.size() == 1;
        tosList = Identity.TosSearch(tos1.getTitle(), "TOS", null, null);
        assert tosList.size() == 0;

        tosList = Identity.TosSearch(tos1.getTitle(), null, "APPROVED", null);
        assert tosList.size() == 1;
        tosList = Identity.TosSearch(tos1.getTitle(), null, "DRAFT", null);
        assert tosList.size() == 0;

        tosList = Identity.TosSearch(tos1.getTitle(), null, null, "US");
        assert tosList.size() == 1;
        tosList = Identity.TosSearch(tos1.getTitle(), null, null, "HK");
        assert tosList.size() == 1;

        tosList = Identity.TosSearch(null, "EULA", null, null);
        assert tosList.size() != 0;

        tosList = Identity.TosSearch(null, "EULA", "APPROVED", null);
        assert tosList.size() != 0;
        tosList = Identity.TosSearch(null, "EULA", "DRAFT", null);
        assert tosList.size() == 0;

        tosList = Identity.TosSearch(null, "EULA", null, "US");
        assert tosList.size() != 0;
        tosList = Identity.TosSearch(null, "EULA", null, "HK");
        assert tosList.size() != 0;

        tosList = Identity.TosSearch(null, null, "APPROVED", "US");
        assert tosList.size() != 0;
        tosList = Identity.TosSearch(null, null, "APPROVED", "HK");
        assert tosList.size() != 0;

        tosList = Identity.TosSearch(tos1.getTitle(), tos1.getType(), tos1.getState(), null);
        assert tosList.size() == 1;
        tosList = Identity.TosSearch(tos1.getTitle(), RandomHelper.randomAlphabetic(14), RandomHelper.randomAlphabetic(14), null);
        assert tosList.size() == 0;

        tosList = Identity.TosSearch(tos1.getTitle(), null, tos1.getState(), "US");
        assert tosList.size() == 1;
        tosList = Identity.TosSearch(tos1.getTitle(), null, tos1.getState(), "HK");
        assert tosList.size() == 1;

        tosList = Identity.TosSearch(null, tos1.getType(), tos1.getState(), "US");
        assert tosList.size() != 0;
        tosList = Identity.TosSearch(null, tos1.getType(), tos1.getState(), "HK");
        assert tosList.size() != 0;
    }
}
