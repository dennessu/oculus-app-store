/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core;

import org.glassfish.grizzly.http.server.Request;

import java.util.regex.Pattern;

/**
 * IpUtil.
 */
public class IpUtil {
    private IpUtil() {
    }

    private static final String STRING_255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    private static final Pattern PATTERN = Pattern.compile("^(?:" + STRING_255 + "\\.){3}" + STRING_255 + "$");
    private static final String OCULUS_END_USER_IP = "oculus-end-user-ip";

    private static long ipV4ToLong(String ip) {
        String[] octets = ip.split("\\.");
        return (Long.parseLong(octets[0]) << 24) + (Integer.parseInt(octets[1]) << 16) +
                (Integer.parseInt(octets[2]) << 8) + Integer.parseInt(octets[3]);
    }

    private static boolean isIPv4Private(String ip) {
        long longIp = ipV4ToLong(ip);
        return (longIp >= ipV4ToLong("10.0.0.0") && longIp <= ipV4ToLong("10.255.255.255")) ||
                (longIp >= ipV4ToLong("172.16.0.0") && longIp <= ipV4ToLong("172.31.255.255")) ||
                (longIp >= ipV4ToLong("192.168.0.0") && longIp <= ipV4ToLong("192.168.255.255"));
    }

    private static boolean isIPv4Valid(String ip) {
        return PATTERN.matcher(ip).matches();
    }

    public static String getClientIpFromRequest(Request request) {
        String endUserIp = request.getHeader(OCULUS_END_USER_IP);
        if (endUserIp == null) {
            String forwardedFor;
            if ((forwardedFor = request.getHeader("x-forwarded-for")) != null) {
                String[] ips = forwardedFor.split(",");
                for (String ip : ips) {
                    if (isIPv4Valid(ip) && !isIPv4Private(ip)) {
                        endUserIp = ip;
                        break;
                    }
                }
            } else {
                endUserIp = request.getRemoteAddr();
            }
        }

        return endUserIp;
    }

    public static boolean match(String pattern, String ipAddress) {
        Pattern ipPattern = createIpPattern(pattern);
        return ipPattern.matcher(ipAddress).matches();
    }

    private static Pattern createIpPattern(String ip) {
        return Pattern.compile(ip.replace(".", "\\.").replace("*", "\\d+"));
    }
}
