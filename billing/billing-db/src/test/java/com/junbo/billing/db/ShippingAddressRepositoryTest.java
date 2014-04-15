/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db;

import com.junbo.billing.db.repository.ShippingAddressRepository;
import com.junbo.billing.spec.model.ShippingAddress;
import com.junbo.common.id.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by xmchen on 14-2-7.
 */
public class ShippingAddressRepositoryTest extends BaseTest {
    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    @Test
    public void testInsertGet() {
        Long userId = generateUserId();

        ShippingAddress shippingAddress = buildShippingAddress(userId);
        shippingAddress = shippingAddressRepository.saveShippingAddress(shippingAddress);

        ShippingAddress returned = shippingAddressRepository.getShippingAddress(
                shippingAddress.getAddressId().getValue());

        Assert.assertNotNull(returned.getAddressId(), "Entity id should not be null.");
        Assert.assertEquals(returned.getFirstName(), shippingAddress.getFirstName(),
                "Entity inserted and get should be same.");
    }

    @Test
    public void testFindByUserId() {
        Long userId = generateUserId();
        ShippingAddress shippingAddress = buildShippingAddress(userId);
        shippingAddress = shippingAddressRepository.saveShippingAddress(shippingAddress);

        shippingAddress = buildShippingAddress(userId);
        shippingAddress = shippingAddressRepository.saveShippingAddress(shippingAddress);

        List<ShippingAddress> addrs = shippingAddressRepository.getShippingAddresses(userId);

        Assert.assertEquals(addrs.size(), 2, "There should be 2 addresses for this user.");
    }

    @Test
    public void testSoftDelete() {
        Long userId = generateUserId();
        ShippingAddress shippingAddress = buildShippingAddress(userId);
        shippingAddress = shippingAddressRepository.saveShippingAddress(shippingAddress);

        shippingAddress = buildShippingAddress(userId);
        shippingAddress = shippingAddressRepository.saveShippingAddress(shippingAddress);

        List<ShippingAddress> addrs = shippingAddressRepository.getShippingAddresses(userId);

        Assert.assertEquals(addrs.size(), 2, "There should be 2 addresses for this user.");

        shippingAddressRepository.deleteShippingAddress(shippingAddress.getAddressId().getValue());

        addrs = shippingAddressRepository.getShippingAddresses(userId);

        Assert.assertEquals(addrs.size(), 1, "There should be 1 address left for this user.");
    }

    private ShippingAddress buildShippingAddress(Long userId) {
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setUserId(new UserId(userId));

        shippingAddress.setFirstName("Xumeng");
        shippingAddress.setLastName("Chen");
        shippingAddress.setCountry("US");
        shippingAddress.setCity("FORREST");
        shippingAddress.setState("CA");
        shippingAddress.setStreet("NO 1, Dapu RD");
        shippingAddress.setPostalCode("96045");

        return shippingAddress;
    }
}
