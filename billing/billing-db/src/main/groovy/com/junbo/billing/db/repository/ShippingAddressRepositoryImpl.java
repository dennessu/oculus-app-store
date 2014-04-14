/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.repository;

import com.junbo.oom.core.MappingContext;
import com.junbo.billing.db.entity.ShippingAddressEntity;
import com.junbo.billing.db.dao.ShippingAddressEntityDao;
import com.junbo.billing.db.mapper.ModelMapper;
import com.junbo.billing.spec.model.ShippingAddress;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xmchen on 14-2-19.
 */
public class ShippingAddressRepositoryImpl implements ShippingAddressRepository {

    @Autowired
    private ShippingAddressEntityDao shippingAddressEntityDao;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public ShippingAddress saveShippingAddress(ShippingAddress address) {

        ShippingAddressEntity sae = modelMapper.toShippingAddressEntity(address, new MappingContext());

        sae.setCreatedBy("BILLING");
        sae.setCreatedTime(new Date());
        sae.setRequestorId("GOD");

        shippingAddressEntityDao.save(sae);

        return getShippingAddress(sae.getAddressId());
    }

    @Override
    public List<ShippingAddress> getShippingAddresses(Long userId) {

        List<ShippingAddressEntity> entities = shippingAddressEntityDao.findByUserId(userId);
        List<ShippingAddress> addresses = new ArrayList<>();

        if(entities != null && entities.size() > 0) {
            for(ShippingAddressEntity entity : entities) {
                ShippingAddress address = modelMapper.toShippingAddress(entity, new MappingContext());
                addresses.add(address);
            }
        }

        return addresses;
    }

    @Override
    public ShippingAddress getShippingAddress(Long addressId) {

        ShippingAddressEntity entity = shippingAddressEntityDao.get(addressId);

        if(entity == null) {
            return null;
        }

        return modelMapper.toShippingAddress(entity, new MappingContext());
    }

    @Override
    public void deleteShippingAddress(Long addressId) {

        ShippingAddressEntity entity = shippingAddressEntityDao.get(addressId);

        if(entity == null) {
            return;
        }

        shippingAddressEntityDao.softDelete(addressId);
    }
}
