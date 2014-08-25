/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity.impl;

import com.junbo.common.id.TosId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Tos;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.TosState;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.identity.TosService;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RandomFactory;

import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 * time 4/29/2014
 * Implementation of TOS related APIs, including get/post/put/delete tos.
 */
public class TosServiceImpl extends HttpClientBase implements TosService {

    private final String tosUrl = ConfigHelper.getSetting("defaultIdentityEndpoint") + "/tos";
    private static TosService instance;

    public static synchronized TosService instance() {
        if (instance == null) {
            instance = new TosServiceImpl();
        }
        return instance;
    }

    private TosServiceImpl() {
    }

    //Create a default TOS
    public Tos postTos() throws Exception {
        Tos tos = new Tos();
        tos.setTitle(RandomFactory.getRandomStringOfAlphabet(10));
        tos.setContent(RandomFactory.getRandomStringOfAlphabetOrNumeric(20));
        tos.setState(TosState.DRAFT.getState());
        return this.postTos(tos);
    }

    public Tos postTos(Tos tos) throws Exception {
        return this.postTos(tos, 201);
    }

    public Tos postTos(Tos tos, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, tosUrl, tos, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Tos>() {}, responseBody);
    }

    //Get a TOS info
    public Tos getTos(TosId tosId) throws Exception {
        return this.getTos(tosId, 200);
    }

    public Tos getTos(TosId tosId, int expectedResponseCode) throws Exception {
        String url = tosUrl + "/" + IdConverter.idToHexString(tosId);
        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Tos>() {}, responseBody);
    }

    //Get TOS info list
    public Results<Tos> getTosList(HashMap<String, List<String>> getOptions) throws Exception {
        return this.getTosList(getOptions, 200);
    }

    public Results<Tos> getTosList(HashMap<String, List<String>> getOptions, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, tosUrl, getOptions, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<Tos>>() {}, responseBody);
    }

    //Update a TOS
    public Tos putTos(TosId tosId, Tos tos) throws Exception {
        return this.putTos(tosId, tos, 200);
    }

    public Tos putTos(TosId tosId, Tos tos, int expectedResponseCode) throws Exception {
        String putUrl = tosUrl + "/" + IdConverter.idToHexString(tosId);
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, tos, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Tos>() {}, responseBody);
    }

    //Delete a tos
    public void deleteTos(TosId tosId) throws Exception {
        this.deleteTos(tosId, 200);
    }

    public void deleteTos(TosId tosId, int expectedResponseCode) throws Exception {
        String url = tosUrl + "/" + IdConverter.idToHexString(tosId);
        restApiCall(HTTPMethod.DELETE, url, expectedResponseCode);
    }

}
