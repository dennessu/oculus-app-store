/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 SilkCloud and/or its affiliates. All rights reserved.
 */
package com.junbo.common.job.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.cloudant.client.CloudantGlobalUri;
import com.junbo.common.cloudant.client.CloudantUri;
import com.junbo.common.cloudant.exception.CloudantConnectException;
import com.junbo.configuration.ConfigServiceManager;
import com.junbo.langur.core.async.JunboAsyncHttpClient;
import com.junbo.langur.core.promise.Promise;
import com.ning.http.client.Realm;
import com.ning.http.client.Response;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture;

/**
 * CloudantSniffer.
 */
public enum CloudantSniffer {
    INSTANCE;

    private static final String ALL_DB_PATH = "/_all_dbs";
    private static final String LAST_CHANGE_PATH = "_changes?descending=true&limit=1";
    private static final String CHANGE_PATH = "_changes?feed=continuous&heartbeat=60000&since=%s";

    private static final String[] CLOUDANT_URI_KEYS = new String[]{
            "common.cloudant.url",
            "common.cloudantWithSearch.url",
            "encrypt.user.personalinfo.cloudant.url",
            "crypto.userkey.cloudant.url",
            "crypto.itemCryptoKey.cloudant.url"
    };

    private List<CloudantUri> cloudantInstances;

    private JunboAsyncHttpClient asyncHttpClient = JunboAsyncHttpClient.instance();
    private ObjectMapper mapper = new ObjectMapper();

    CloudantSniffer() {
        initialize();
    }

    private void initialize() {
        Map<String, CloudantUri> cloudantUriMap = new HashMap<>();

        for (String key : CLOUDANT_URI_KEYS) {
            String value = ConfigServiceManager.instance().getConfigValue(key);

            if (value == null) {
                throw new IllegalStateException("Configuration value for  [" + key + "] is missing.");
            }

            CloudantGlobalUri uri = new CloudantGlobalUri(value);
            String instanceKey = uri.getCurrentDcUri().getValue() + "#"
                    + isNull(uri.getCurrentDcUri().getUsername(), "");

            cloudantUriMap.put(instanceKey, uri.getCurrentDcUri());
        }

        cloudantInstances = new ArrayList(cloudantUriMap.values());
    }

    public void listen() {
        for (CloudantUri cloudantUri : cloudantInstances) {
            Response response = executeGet(cloudantUri, ALL_DB_PATH, null).get();
            List<String> databases = parse(response, List.class);

            for (String dbName : databases) {
                process(dbName, cloudantUri);
            }
        }
    }

    private void process(String dbName, CloudantUri cloudantUri) {

    }

    private Promise<Response> executeGet(CloudantUri cloudantUri, String path, Map<String, String> queryParams) {
        return executeGet(cloudantUri, null, path, queryParams);
    }

    private <T> T parse(Response response, Class<T> clazz) {
        if (response == null) {
            throw new CloudantConnectException("Response from cloudant DB is invalid");
        }

        try {
            String payload = response.getResponseBody();
            return (T) mapper.readValue(payload, clazz);
        } catch (IOException e) {
            throw new CloudantConnectException("Error occurred while parsing response from cloudant DB", e);
        }
    }

    private Promise<Response> executeGet(CloudantUri cloudantUri, String dbName, String path, Map<String, String> queryParams) {
        UriBuilder uriBuilder = UriBuilder.fromUri(cloudantUri.getValue());

        if (dbName != null) {
            uriBuilder.path(dbName);
        }
        uriBuilder.path(path);

        JunboAsyncHttpClient.BoundRequestBuilder requestBuilder = asyncHttpClient.prepareGet(uriBuilder.toTemplate());

        if (!StringUtils.isEmpty(cloudantUri.getUsername())) {
            Realm realm = new Realm.RealmBuilder()
                    .setPrincipal(cloudantUri.getUsername())
                    .setPassword(cloudantUri.getPassword())
                    .setUsePreemptiveAuth(true)
                    .setScheme(Realm.AuthScheme.BASIC).build();

            requestBuilder.setRealm(realm);
            requestBuilder.setHeader("X-Cloudant-User", cloudantUri.getUsername());
        }

        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                requestBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        try {
            return Promise.wrap(asGuavaFuture(requestBuilder.execute()))
                    .recover(new Promise.Func<Throwable, Promise<Response>>() {
                        public Promise<Response> apply(Throwable e) {
                            throw new CloudantConnectException("Error occurred while executing request to cloudant DB", e);
                        }
                    });
        } catch (IOException e) {
            throw new CloudantConnectException("Error occurred while executing request to cloudant DB", e);
        }
    }

    private String isNull(String input, String replace) {
        return input == null ? replace : input;
    }
}
