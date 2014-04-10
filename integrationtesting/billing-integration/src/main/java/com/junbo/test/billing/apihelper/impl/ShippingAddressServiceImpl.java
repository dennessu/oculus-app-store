/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing.apihelper.impl;

import com.junbo.billing.spec.model.ShippingAddress;
import com.junbo.common.id.ShippingAddressId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.billing.apihelper.ShippingAddressService;
import com.junbo.test.common.apihelper.HttpClientBase;

import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RestUrl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yunlong on 3/24/14.
 */
public class ShippingAddressServiceImpl extends HttpClientBase implements ShippingAddressService {

    private static String shippingAddressUrl = RestUrl.getRestUrl(RestUrl.ComponentName.COMMERCE);
    private static ShippingAddressService instance;

    public static synchronized ShippingAddressService getInstance() {
        if (instance == null) {
            instance = new ShippingAddressServiceImpl();
        }
        return instance;
    }

    @Override
    public String postShippingAddressToUser(String uid, ShippingAddress address) throws Exception {
        return postShippingAddressToUser(uid, address, 200);
    }

    @Override
    public String postShippingAddressToUser(String uid, ShippingAddress address,
                                            int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, shippingAddressUrl
                + "users/" + uid + "/ship-to-info", address);

        ShippingAddress shippingResult =
                new JsonMessageTranscoder().decode(
                        new TypeReference<ShippingAddress>() {
                        }, responseBody);

        String addressId = IdConverter.idToHexString(shippingResult.getAddressId());
        Master.getInstance().addShippingAddress(addressId, shippingResult);

        return addressId;
    }

    @Override
    public List<String> getShippingAddresses(String uid) throws Exception {
        return getShippingAddresses(uid, 200);
    }

    @Override
    public List<String> getShippingAddresses(String uid, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, shippingAddressUrl +
                "users/" + uid + "/ship-to-info", expectedResponseCode);

        Results<ShippingAddress> shippingAddressResults =
                new JsonMessageTranscoder().decode(
                        new TypeReference<Results<ShippingAddress>>() {
                        }, responseBody);

        List<String> addressIdList = new ArrayList<>();
        for (ShippingAddress address : shippingAddressResults.getItems()) {
            String addressId = IdConverter.idLongToHexString(
                    ShippingAddressId.class, address.getAddressId().getValue());
            Master.getInstance().addShippingAddress(addressId, address);
            addressIdList.add(addressId);
        }

        return addressIdList;
    }

    @Override
    public String getShippingAddress(String uid, String addressId) throws Exception {
        return getShippingAddress(uid, addressId, 200);
    }

    @Override
    public String getShippingAddress(String uid, String addressId, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, shippingAddressUrl +
                "users/" + uid + "/ship-to-info/" + addressId, expectedResponseCode);

        ShippingAddress shippingResult =
                new JsonMessageTranscoder().decode(
                        new TypeReference<ShippingAddress>() {
                        }, responseBody);

        addressId = IdConverter.idToHexString(shippingResult.getAddressId());
        Master.getInstance().addShippingAddress(addressId, shippingResult);

        return addressId;

    }

    @Override
    public void deleteShippingAddress(String uid, String addressId) throws Exception {
        deleteShippingAddress(uid, addressId, 204);

    }

    @Override
    public void deleteShippingAddress(String uid, String addressId, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.DELETE, shippingAddressUrl +
                "users/" + uid + "/ship-to-info/" + addressId, expectedResponseCode);

        ShippingAddress shippingResult =
                new JsonMessageTranscoder().decode(
                        new TypeReference<ShippingAddress>() {
                        }, responseBody);

        addressId = IdConverter.idToHexString(shippingResult.getAddressId());
        Master.getInstance().removeShippingAddress(addressId);
    }

}
