/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.fulfilment.clientproxy.BillingGateway;
import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.spec.error.AppErrors;
import com.junbo.fulfilment.spec.fusion.ShippingAddress;
import com.junbo.identity.spec.v1.model.Address;
import com.junbo.identity.spec.v1.model.UserPersonalInfo;
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions;
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource;
import ma.glasnost.orika.metadata.ClassMapBuilder;
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

    static {
        Utils.registerClassMap(
                ClassMapBuilder.map(Address.class, ShippingAddress.class)
                        .field("street1", "street")
                        .field("street2", "street1")
                        .field("street3", "street2")
                        .field("city", "city")
                        .field("postalCode", "postalCode")
                        .exclude("countryName")
                        .field("countryId.value", "country")
                        .field("firstName", "firstName")
                        .field("lastName", "lastName")
                        .field("phoneNumber", "phoneNumber")
                        .exclude("isWellFormed")
                        .exclude("isNormalized")
                        .field("subCountryCode", "state")
                        .exclude("subCountryName")
                        .exclude("addressId")
                        .exclude("userId")
                        .exclude("companyName")
                        .exclude("middleName")
                        .exclude("description")
                        .toClassMap()
        );
    }

    @Autowired
    @Qualifier("shippingAddressClient")
    private UserPersonalInfoResource userPersonalInfoResource;

    @Override
    public ShippingAddress getShippingAddress(Long userId, Long shippingAddressId) {
        try {
            UserPersonalInfo retrieved =
                    userPersonalInfoResource.get(new UserPersonalInfoId(shippingAddressId), new UserPersonalInfoGetOptions()).wrapped().get();

            if (retrieved == null) {
                LOGGER.error("ShippingAddress [" + shippingAddressId + "] does not exist");
                throw AppErrors.INSTANCE.notFound("ShippingAddress", shippingAddressId).exception();
            }

            return wash(retrieved);
        } catch (Exception e) {
            LOGGER.error("Error occurred during calling [Billing] component.", e);
            throw AppErrors.INSTANCE.gatewayFailure("billing").exception();
        }
    }

    private ShippingAddress wash(UserPersonalInfo source) {

        if (!"ADDRESS".equals(source.getType())) {
            String message = "UserPersonalInfo [" + source.getId().getValue() + "] is not an address, actual type is [" + source.getType() + "]";
            LOGGER.error(message);
            throw AppErrors.INSTANCE.validation(message).exception();
        }

        Address address;
        try {
            address = ObjectMapperProvider.instance().treeToValue(source.getValue(), Address.class);
        }
        catch (Exception e) {
            LOGGER.error("UserPersonalInfo [" + source.getId().getValue() + "] failed to cast to Address. Value: [" + source.getValue() + "]");
            throw AppErrors.INSTANCE.validation("UserPersonalInfo [" + source.getId().getValue() + "] failed to cast to Address").exception();
        }

        ShippingAddress result = Utils.map(address, ShippingAddress.class);
        result.setUserId(source.getUserId().getValue());
        result.setAddressId(source.getId().getValue());
        result.setDescription(source.getLabel());

        return result;
    }
}
