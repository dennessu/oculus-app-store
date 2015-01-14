/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The configuration context of current service instance. The properties are used in resolve property overriding.
 * The shards are the specified shards of the server. Database layer is expected to generate IDs from the specified
 * range.
 */
public class ConfigContext {
    private static final Logger logger = LoggerFactory.getLogger(ConfigContext.class);

    private String baseEnv;
    private String environment;
    private String datacenter;
    private Set<Integer> shards;
    private List<String> ipAddresses;

    public ConfigContext(String environment) {
        this.environment = environment;
        this.baseEnv = parseBaseEnv(environment);
    }

    public ConfigContext complete(String datacenter, String subnet, String shardRange) {
        if (datacenter == null || datacenter.length() == 0) {
            throw new RuntimeException("ERROR: datacenter is not set.");
        }
        if (subnet == null || subnet.length() == 0) {
            throw new RuntimeException("ERROR: subnet is not set.");
        }

        this.datacenter = datacenter;
        this.ipAddresses = getIpAddresses(subnet);

        this.shards = parseShardRange(shardRange);

        return this;
    }

    public String getBaseEnvironment() {
        return baseEnv;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getDataCenter() {
        return datacenter;
    }

    public Set<Integer> getShards() {
        return shards;
    }

    public List<String> getIpAddresses() {
        return ipAddresses;
    }

    public boolean isSelfIpAddress(String ipAddress) {
        return ipAddresses.contains(ipAddress);
    }

    //region get ip addresses

    private List<String> getIpAddresses(String ipv4Subnet) {
        Pattern subnetPattern = Pattern.compile("^(\\d+\\.\\d+\\.\\d+\\.\\d+)/(\\d+)$");
        Matcher subnetMatcher = subnetPattern.matcher(ipv4Subnet);
        if (!subnetMatcher.matches()) {
            throw new RuntimeException("Invalid configuration ipv4Subnet: " + ipv4Subnet);
        }

        int subnetIp = 0;
        int mask = 0;
        try {
            String subnetIpStr = subnetMatcher.group(1);
            String subnetBitsStr = subnetMatcher.group(2);

            int subnetBits = Integer.parseInt(subnetBitsStr);
            if (!(0 <= subnetBits && subnetBits <= 32)) {
                throw new RuntimeException("Subnet mask bits out of range: " + subnetBits);
            }

            mask = (int) (0xFFFFFFFFL << (32 - subnetBits));     // cast to long to prevent subnetBits == 32

            subnetIp = parseIpAddress(subnetIpStr) & mask;
        } catch (Exception ex) {
            throw new RuntimeException("Invalid subnet configuration: " + ipv4Subnet);
        }

        List<String> ipAddresses = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nie = NetworkInterface.getNetworkInterfaces();
            while (nie.hasMoreElements()) {
                NetworkInterface ni = nie.nextElement();

                Enumeration<InetAddress> iae = ni.getInetAddresses();
                while (iae.hasMoreElements()) {
                    InetAddress addr = iae.nextElement();

                    if (addr instanceof Inet4Address) {
                        ipAddresses.add(addr.getHostAddress());
                    }
                }
            }
        } catch (Exception ex) {
            if (environment != null && environment.equals("onebox")) {
                logger.warn("Failed to get system ipAddress. Using 127.0.0.1 as default.", ex);
                final String localhost = "127.0.0.1";
                if (!ipAddresses.contains(localhost)) {
                    ipAddresses.add(localhost);
                }
            } else {
                throw new RuntimeException(ex);
            }
        }

        List<String> result = new ArrayList<>();
        for (String address : ipAddresses) {
            int ipAddress = parseIpAddress(address);
            if (subnetIp == (ipAddress & mask)) {
                // IP address is in the subnet.
                result.add(address);
                logger.info("Add ip address: " + address);
            }
        }
        if (result.size() == 0) {
            throw new RuntimeException("No ip address found.");
        }

        return result;
    }

    private static int parseIpAddress(String ipAddress) {
        Pattern ipv4Pattern = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)$");

        Matcher ipAddressMatcher = ipv4Pattern.matcher(ipAddress);
        if (!ipAddressMatcher.matches()) {
            throw new RuntimeException("Invalid ipAddress: " + ipAddress);
        }

        int ipAddressNumeric = ((parseIpAddressPart(ipAddressMatcher.group(1), ipAddress) & 0xFF) << 24) |
                ((parseIpAddressPart(ipAddressMatcher.group(2), ipAddress) & 0xFF) << 16) |
                ((parseIpAddressPart(ipAddressMatcher.group(3), ipAddress) & 0xFF) << 8) |
                ((parseIpAddressPart(ipAddressMatcher.group(4), ipAddress) & 0xFF) << 0);

        return ipAddressNumeric;
    }

    private static int parseIpAddressPart(String str, String ipAddress) {
        try {
            int p = Integer.parseInt(str);
            if (0 <= p && p <= 255) {
                return p;
            }
            throw new RuntimeException("Invalid ipAddress: " + ipAddress);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Invalid ipAddress: " + ipAddress);
        }
    }

    //endregion

    //region get environment

    private String parseBaseEnv(String env) {
        int index = env.indexOf(".");
        if (index == -1) {
            return null;
        }
        return env.substring(0, index);
    }

    //endregion

    private Set<Integer> parseShardRange(String shardRange) {
        Set<Integer> result = new HashSet<>();
        for (String range : shardRange.split(",")) {
            if (StringUtils.isEmpty(range)) {
                continue;
            }

            String[] fromAndTo = range.trim().split("\\.\\.");
            if (fromAndTo.length == 1) {
                result.add(Integer.parseInt(fromAndTo[0]));
            } else if (fromAndTo.length == 2) {
                int from = Integer.parseInt(fromAndTo[0]);
                int to = Integer.parseInt(fromAndTo[1]);
                if (from > to) {
                    throw new RuntimeException("from > to in shardRange: " + shardRange);
                }
                for (int i = from; i <= to; i++) {
                    result.add(i);
                }
            } else {
                throw new RuntimeException("Invalid shardRange: " + shardRange);
            }
        }
        return result;
    }
}
