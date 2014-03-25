/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.billing.impl;

import com.junbo.billing.spec.model.ShippingAddress;
import com.junbo.testing.common.apihelper.HttpClientBase;
import com.junbo.testing.common.apihelper.billing.ShippingAddressService;
import com.junbo.testing.common.blueprint.Master;
import com.junbo.testing.common.libs.RestUrl;
import com.junbo.testing.common.libs.LogHelper;

import java.util.List;

/**
 * Created by Yunlong on 3/24/14.
 */
public class ShippingAddressServiceImpl extends HttpClientBase implements ShippingAddressService {

    private static String shippingAddressUrl = RestUrl.getRestUrl(RestUrl.ComponentName.SHIPPING);

    private LogHelper logger = new LogHelper(ShippingAddressServiceImpl.class);

    private static ShippingAddressService instance;

    public static synchronized ShippingAddressService getInstance() {
        if (instance == null) {
            instance = new ShippingAddressServiceImpl();
        }
        return instance;
    }

    @Override
    public String addShippingAddress(String uid, ShippingAddress address) throws Exception {
        return addShippingAddress(uid, address, 200);
    }

    @Override
    public String addShippingAddress(String uid, ShippingAddress address, int expectedResponseCode) throws Exception {
        /*
        ShippingAddress shippingResult =
                restApiCall(HTTPMethod.POST, shippingAddressUrl, address, expectedResponseCode);
        String addressId = IdConverter.idToHexString(shippingResult.getAddressId());
        Master.getInstance().addShippingAddress(addressId, shippingResult);

        return addressId;
        */
        return null;
    }

    @Override
    public List<String> getShippingAddresses(String uid) throws Exception {
        /*
        Results<ShippingAddress> shippingAddressResults =
                restApiCall(HTTPMethod.GET, shippingAddressUrl + uid, 200);

        List<String> addressIdList = new ArrayList<>();
        for (ShippingAddress address : shippingAddressResults.getItems()) {
            String addressId = IdConverter.idLongToHexString(
                    ShippingAddressId.class, address.getAddressId().getValue());
            Master.getInstance().addShippingAddress(addressId, address);
            addressIdList.add(addressId);
        }

        return addressIdList;
        */
        return null;
    }

    @Override
    public List<String> getShippingAddresses(String uid, int expectedResponseCode) throws Exception {
    /*
        Results<ShippingAddress> shippingAddressResults =
                restApiCall(HTTPMethod.GET, shippingAddressUrl + uid, expectedResponseCode);

        List<String> addressIdList = new ArrayList<>();
        for (ShippingAddress address : shippingAddressResults.getItems()) {
            String addressId = IdConverter.idLongToHexString(
                    ShippingAddressId.class, address.getAddressId().getValue());
            Master.getInstance().addShippingAddress(addressId, address);
            addressIdList.add(addressId);
        }

        return addressIdList;
        */
        return null;
    }

    @Override
    public String getShippingAddress(String uid, String addressId) throws Exception {
    /*
        return getShippingAddress(uid, addressId, 200);
        */
        return null;
    }

    @Override
    public String getShippingAddress(String uid, String addressId, int expectedResponseCode) throws Exception {
    /*
        ShippingAddress shippingAddressResult =
                restApiCall(HTTPMethod.GET, shippingAddressUrl + uid, expectedResponseCode);
        String shippingAddressId = IdConverter.idLongToHexString(
                ShippingAddressId.class, shippingAddressResult.getAddressId().getValue());
        Master.getInstance().addShippingAddress(addressId, shippingAddressResult);
        return shippingAddressId;
        */
        return null;

    }

    @Override
    public void deleteShippingAddress(String uid, String addressId) throws Exception {
        restApiCall(HTTPMethod.DELETE, shippingAddressUrl + uid, 200);
    }

    @Override
    public void deleteShippingAddress(String uid, String addressId, int expectedResponseCode) throws Exception {
        restApiCall(HTTPMethod.DELETE, shippingAddressUrl + uid, expectedResponseCode);
        Master.getInstance().removeShippingAddress(addressId);
    }

}
