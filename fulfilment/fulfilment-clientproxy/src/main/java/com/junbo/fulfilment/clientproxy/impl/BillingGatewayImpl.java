/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.fulfilment.clientproxy.BillingGateway;
import com.junbo.fulfilment.spec.error.AppErrors;
import com.junbo.fulfilment.spec.fusion.ShippingAddress;
import com.junbo.identity.spec.v1.model.Address;
import com.junbo.identity.spec.v1.model.UserPersonalInfo;
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions;
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * BillingGatewayImpl.
 */
public class BillingGatewayImpl implements BillingGateway {
    private static final UserId BLIND_USERID_MAPPING = null;
    private static final UserPersonalInfoId BLIND_ADDRESSID_MAPPING = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingGatewayImpl.class);

    /*static {
        Utils.registerClassMap(
                ClassMapBuilder.map(Address.class, ShippingAddress.class)
                        .field("street1", "street")
                        .field("street2", "street1")
                        .field("street3", "street2")
                        .field("city", "city")
                        .field("postalCode", "postalCode")
                        .field("countryId.value", "country")
                        .field("firstName", "firstName")
                        .field("lastName", "lastName")
                        .field("phoneNumber", "phoneNumber")
                        .field("subCountryCode", "state")
                        // excludes, must provide a dummy but valid fieldB
                        // exclude aToB
                        .fieldMap("countryName", "userId").exclude().add()
                        .fieldMap("isWellFormed", "userId").exclude().add()
                        .fieldMap("isNormalized", "userId").exclude().add()
                        .fieldMap("subCountryName", "userId").exclude().add()
                        // exclude bToA
                        .fieldMap("countryName", "userId").exclude().add()
                        .fieldMap("countryName", "addressId").exclude().add()
                        .fieldMap("countryName", "middleName").exclude().add()
                        .fieldMap("countryName", "description").exclude().add()
                        .toClassMap()
        );
    }*/

    @Autowired
    @Qualifier("shippingAddressClient")
    private UserPersonalInfoResource userPersonalInfoResource;

    @Override
    public ShippingAddress getShippingAddress(Long userId, Long shippingAddressId) {
        try {
            UserPersonalInfoId addressId = new UserPersonalInfoId(shippingAddressId);
            UserPersonalInfo retrieved = userPersonalInfoResource.get(addressId, new UserPersonalInfoGetOptions()).get();

            if (retrieved == null) {
                LOGGER.error("ShippingAddress [" + shippingAddressId + "] does not exist");
                throw AppErrors.INSTANCE.shippingAddressNotFound(addressId).exception();
            }

            return wash(retrieved);
        } catch (Exception e) {
            LOGGER.error("Error occurred during calling [Identity] component.", e);
            throw AppErrors.INSTANCE.gatewayFailure("Identity").exception();
        }
    }

    private ShippingAddress wash(UserPersonalInfo source) {

        if (!"ADDRESS".equals(source.getType())) {
            String message = "UserPersonalInfo [" + source.getId().getValue() + "] is not an address, actual type is [" + source.getType() + "]";
            LOGGER.error(message);
            throw AppCommonErrors.INSTANCE.fieldInvalid("shippingAddress", message).exception();
        }

        Address address;
        try {
            address = ObjectMapperProvider.instance().treeToValue(source.getValue(), Address.class);
        } catch (Exception e) {
            String message = "UserPersonalInfo [" + source.getId().getValue() + "] failed to cast to Address.";
            LOGGER.error(message + " Value: [" + source.getValue() + "]");
            throw AppCommonErrors.INSTANCE.fieldInvalid("shippingAddress", message).exception();
        }

        /*ShippingAddress result = Utils.map(address, ShippingAddress.class);
        result.setUserId(source.getUserId().getValue());
        result.setAddressId(source.getId().getValue());
        result.setDescription(source.getLabel());*/

        return new ShippingAddress();
    }
}
