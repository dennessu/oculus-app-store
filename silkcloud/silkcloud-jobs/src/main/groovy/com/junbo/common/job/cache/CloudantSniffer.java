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
import com.junbo.common.cloudant.exception.CloudantException;
import com.junbo.configuration.ConfigServiceManager;
import com.junbo.langur.core.async.JunboAsyncHttpClient;
import com.junbo.langur.core.promise.Promise;
import com.ning.http.client.Realm;
import com.ning.http.client.Response;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.UriBuilder;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CloudantSniffer.
 */
public class CloudantSniffer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudantSniffer.class);

    //private static final String ALL_DB_PATH = "/_all_dbs";
    private static final String CHANGE_PATH = "/_changes";

    private static final String CLOUDANT_PREFIX_KEY = "common.cloudant.dbNamePrefix";
    private static final String CLOUDANT_HEARTBEAT_KEY = "common.cloudant.heartbeat";
    private static final String CLOUDANT_DBLIST_KEY = "common.cloudant.dblist";

    private static final String CLASSPATH_PREFIX = "classpath:";

    private static final String[] CLOUDANT_URI_KEYS = new String[]{
            "common.cloudant.url",
            "common.cloudantWithSearch.url",
            "crypto.userkey.cloudant.url",
            "crypto.itemCryptoKey.cloudant.url"
    };

    private static final String CLOUDANT_LASTSEQ_KEY = "last_seq";
    private static final String CLOUDANT_DEFAULT_PREFIX = "";

    private JunboAsyncHttpClient asyncHttpClient = JunboAsyncHttpClient.instance();

    private Integer cloudantHeartbeat;
    private List<CloudantUri> cloudantInstances;


    private static CloudantSniffer cloudantSniffer = new CloudantSniffer();

    public static CloudantSniffer instance() {
        return cloudantSniffer;
    }

    private CloudantSniffer() {
        initialize();
    }

    /*public List<String> getAllDatabases(CloudantUri cloudantUri) {
        Response response = executeGet(cloudantUri, ALL_DB_PATH, null).get();
        List<String> databases = SnifferUtils.parse(response, List.class);

        String prefix = ConfigServiceManager.instance().getConfigValue(CLOUDANT_PREFIX_KEY);
        if (prefix == null) {
            prefix = CLOUDANT_DEFAULT_PREFIX;
        }

        List<String> filtered = new ArrayList<>();
        for (String db : databases) {
            if (db.startsWith(prefix)) {
                filtered.add(db);
            }
        }

        LOGGER.info("Find [" + filtered.size() + "] matched databases.");
        return filtered;
    }*/

    public List<String> getDatabaseList() {
        String dblist = ConfigServiceManager.instance().getConfigValue(CLOUDANT_DBLIST_KEY);

        if (StringUtils.isEmpty(dblist)) {
            throw new CloudantConnectException("Cloudant database configuration file not found.");
        }
        LOGGER.info("Cloudant database configuration file is [" + dblist + "].");
        InputStream dbFileInputStream = readFile(dblist);

        if (dbFileInputStream == null) {
            throw new CloudantException("Error occurred during reading file [" + dblist + "].");
        }
        LOGGER.info("Read cloudant database list file successfully.");
        try {
            Map<String, Map<String, Object>> payload = new ObjectMapper().readValue(dbFileInputStream, Map.class);

            List<String> databases = new ArrayList<>();
            for (Map.Entry<String, Map<String, Object>> entry : payload.entrySet()) {
                databases.addAll(entry.getValue().keySet());
            }

            LOGGER.info("Read {} databases in database list file.", databases.size());
            return databases;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read all database list.", e);
        } finally {
            SnifferUtils.close(dbFileInputStream);
        }
    }

    public List<String> getAllDatabases(CloudantUri cloudantUri, List<String> databases) {
        String prefix = ConfigServiceManager.instance().getConfigValue(CLOUDANT_PREFIX_KEY);
        if (prefix == null) {
            prefix = CLOUDANT_DEFAULT_PREFIX;
        }
        LOGGER.info("Cloudant database prefix is [" + prefix + "].");

        List<String> filtered = new ArrayList<>();

        try {
            for (String db : databases) {
                String fullDatabaseName = prefix + db;
                Response response = executeGet(cloudantUri, fullDatabaseName, "", null).get();

                if (response.getStatusCode() / 100 == 2) {
                    filtered.add(fullDatabaseName);
                    LOGGER.debug("Database [" + fullDatabaseName
                            + "] is available on instance [" + cloudantUri.getDetail() + "].");
                } else {
                    LOGGER.debug("Database [" + fullDatabaseName
                            + "] doesn't exist on instance [" + cloudantUri.getDetail() + "].");
                }
            }
        } catch (Exception e) {
            throw new CloudantConnectException("Error occurred during get all cloudant databases.", e);
        }

        LOGGER.info("Detect [" + filtered.size() + "] available databases on " + cloudantUri.getDetail());
        return filtered;
    }

    public String getLastChange(CloudantUri cloudantUri, String dbName) {
        Response response = executeGet(cloudantUri, dbName, CHANGE_PATH,
                new HashMap<String, String>() {{
                    put("descending", "true");
                    put("limit", "1");
                }}).get();

        Map<String, Object> result = SnifferUtils.parse(response, Map.class);
        if (!result.containsKey(CLOUDANT_LASTSEQ_KEY)) {
            throw new CloudantConnectException("Error occurred during get last change seq");
        }

        Object lastSeqObj = result.get(CLOUDANT_LASTSEQ_KEY);
        String lastSeq = (lastSeqObj == null ? null : lastSeqObj.toString());

        LOGGER.debug("Last change sequence id on [" + cloudantUri.toString() + "] is [" + lastSeq + "].");
        return lastSeq;
    }

    public List<CloudantUri> getCloudantInstances() {
        return cloudantInstances;
    }

    public InputStream getChangeFeed(CloudantUri cloudantUri, String database, String lastChange) {
        String url = String.format("%s/%s%s?feed=continuous&heartbeat=%d%s",
                cloudantUri.getValue(),
                database,
                CHANGE_PATH,
                cloudantHeartbeat,
                StringUtils.isEmpty(lastChange) ? "" : "&since=" + lastChange);

        CredentialsProvider credsProvider = null;

        if (cloudantUri.getUsername() != null) {
            credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(cloudantUri.getUsername(), cloudantUri.getPassword()));
        }

        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();

        try {
            HttpGet httpGet = new HttpGet(url);
            if (!StringUtils.isEmpty(cloudantUri.getAccount())) {
                httpGet.addHeader("X-Cloudant-User", cloudantUri.getAccount());
            }
            return client.execute(httpGet).getEntity().getContent();
        } catch (IOException e) {
            throw new CloudantConnectException("Error occurred while receiving cloudant change feed", e);
        }
    }

    private Promise<Response> executeGet(CloudantUri cloudantUri, String path, Map<String, String> queryParams) {
        return executeGet(cloudantUri, null, path, queryParams);
    }

    private void initialize() {
        // initialize cloudant feed heartbeat interval
        this.cloudantHeartbeat = SnifferUtils.safeParseInt(SnifferUtils.getConfig(CLOUDANT_HEARTBEAT_KEY));

        // initialize cloudant instance
        Map<String, CloudantUri> cloudantUriMap = new HashMap<>();

        for (String key : CLOUDANT_URI_KEYS) {
            String value = ConfigServiceManager.instance().getConfigValue(key);

            if (value == null) {
                throw new IllegalStateException("Configuration value for  [" + key + "] is missing.");
            }

            CloudantGlobalUri uri = new CloudantGlobalUri(value);
            cloudantUriMap.put(uri.getCurrentDcUri().getKey(), uri.getCurrentDcUri());
        }

        cloudantInstances = new ArrayList(cloudantUriMap.values());
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
        }
        if (!StringUtils.isEmpty(cloudantUri.getAccount())) {
            requestBuilder.addHeader("X-Cloudant-User", cloudantUri.getAccount());
        }

        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                requestBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        try {
            return requestBuilder.execute()
                    .recover(new Promise.Func<Throwable, Promise<Response>>() {
                        public Promise<Response> apply(Throwable e) {
                            throw new CloudantConnectException("Error occurred while executing request to cloudant DB", e);
                        }
                    });
        } catch (IOException e) {
            throw new CloudantConnectException("Error occurred while executing request to cloudant DB", e);
        }
    }

    private InputStream readFile(String fileName) {
        InputStream inputStream = null;

        if (fileName.startsWith(CLASSPATH_PREFIX)) {
            fileName = fileName.substring(CLASSPATH_PREFIX.length());
            inputStream = getClass().getResourceAsStream(fileName);
            return inputStream;
        }

        try {
            inputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            LOGGER.error("Error occurred during reading file [" + fileName + "].");
        }

        return inputStream;
    }
}
