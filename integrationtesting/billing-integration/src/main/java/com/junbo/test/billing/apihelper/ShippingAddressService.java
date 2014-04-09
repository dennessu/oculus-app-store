/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing.apihelper;

import com.junbo.billing.spec.model.ShippingAddress;

import java.util.List;

/**
 * Created by Yunlong on 3/24/14.
 */
public interface ShippingAddressService {
    String postShippingAddressToUser(String uid, ShippingAddress address) throws Exception;

    String postShippingAddressToUser(String uid, ShippingAddress address, int expectedResponseCode) throws Exception;

    List<String> getShippingAddresses(String uid) throws Exception;

    List<String> getShippingAddresses(String uid, int expectedResponseCode) throws Exception;

    String getShippingAddress(String uid, String addressId) throws Exception;

    String getShippingAddress(String uid, String addressId, int expectedResponseCode) throws Exception;

    void deleteShippingAddress(String uid, String addressId) throws Exception;

    void deleteShippingAddress(String uid, String addressId, int expectedResponseCode) throws Exception;

}
