/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration;

import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The configuration context of current service instance. The properties are used in resolve property overriding.
 * The shards are the specified shards of the server. Database layer is expected to generate IDs from the specified
 * range.
 */
public class ConfigContext {
    public static final int MAX_SHARD_NUM = 999;

    private String environment;
    private String hostname;
    private int[] shards;

    public ConfigContext(String environment) {
        this.environment = environment;
        this.hostname = resolveHostName();
        //this.shards = parseShards(shards);
    }

    public String getEnvironment() {
        return environment;
    }

    public String getHostname() {
        return hostname;
    }

    public int[] getShards() {
        return shards;
    }

    //region shard parsing

    private static final String SHARD_PARSING_SINGLE_GROUP = "(?<from>\\d+)(\\.\\.(?<to>\\d+))?((,?\\s*)|(\\s+)|$)";
    private static final String SHARD_PARSING_FULL_GROUP = "^(" + SHARD_PARSING_SINGLE_GROUP + ")+$";

    private static int[] parseShards(String shards) {
        if (StringUtils.isEmpty(shards)) {
            // defaults to all shards
            shards = "0.." + MAX_SHARD_NUM;
        }

        if (!Pattern.matches(SHARD_PARSING_FULL_GROUP, shards)) {
            throw new RuntimeException("Invalid shards configuration: " + shards);
        }

        int[] result = new int[MAX_SHARD_NUM + 1];
        int resultCount = 0;

        // format: m..n, x..y, ...
        Pattern regex = Pattern.compile(SHARD_PARSING_SINGLE_GROUP);
        Matcher matcher = regex.matcher(shards);

        while (matcher.find()) {
            int from = Integer.parseInt(matcher.group("from"));
            int to = from;

            String groupTo = matcher.group("to");
            if (groupTo != null) {
                to = Integer.parseInt(groupTo);
            }

            if (from > to || from < 0 || to > MAX_SHARD_NUM) {
                throw new RuntimeException("Invalid shard range: " + matcher.group());
            }

            for (int i = from; i <= to; ++i) {
                result[resultCount++] = i;
            }
        }

        result = Arrays.copyOf(result, resultCount);
        return result;
    }
    //endregion

    //region get hostname

    private static String resolveHostName() {
        String result = null;

        result = System.getProperty("HOSTNAME");
        if (result == null) {
            result = System.getenv("HOSTNAME");
        }
        if (result == null) {
            result = System.getenv("COMPUTERNAME");
        }
        if (result == null) {
            try {
                result = java.net.InetAddress.getLocalHost().getHostName();
            }
            catch (Exception ex) {
                // safe to ignore the exception here
            }
        }
        if (result == null) {
            try {
                Process hostname = Runtime.getRuntime().exec("hostname");
                int ret = hostname.waitFor();
                if (ret == 0) {
                    try (BufferedReader input = new BufferedReader(new InputStreamReader(hostname.getInputStream()))) {
                        result = input.readLine();
                    }
                }
            }
            catch (Exception e) {
                // safe to ignore the exception here
            }
        }
        if (result == null) {
            // just random a host name
            result = "unknown_host_" + new SecureRandom().nextInt(1000);
        }
        return result;
    }

    //endregion
}
