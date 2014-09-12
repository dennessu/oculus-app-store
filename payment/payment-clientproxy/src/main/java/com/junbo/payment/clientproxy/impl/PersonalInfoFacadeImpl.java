/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.junbo.common.id.AddressId;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.identity.spec.v1.model.UserPersonalInfo;
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions;
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.PersonalInfoFacade;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.identity.spec.v1.model.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * personal info client proxy facade implementation.
 */
public class PersonalInfoFacadeImpl implements PersonalInfoFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonalInfoFacadeImpl.class);
    private UserPersonalInfoResource piiClient;

    @Override
    public Promise<AddressId> createBillingAddress(final Long userId, final Address address) {
        UserPersonalInfo addressPii = new UserPersonalInfo(){
            {
                setUserId(new UserId(userId));
                setType("ADDRESS");
                setValue(ObjectMapperProvider.instance().valueToTree(address));
            }
        };
        return piiClient.create(addressPii)
                .then(new Promise.Func<UserPersonalInfo, Promise<AddressId>>() {
                    @Override
                    public Promise<AddressId> apply(UserPersonalInfo userPersonalInfo) {
                        return Promise.pure(new AddressId(userPersonalInfo.getId().getValue()));
                    }
                });
    }

    @Override
    public Promise<com.junbo.payment.spec.model.Address> getBillingAddress(final Long billingAddressId) {
        if(billingAddressId == null){
            return Promise.pure(null);
        }
        return piiClient.get(new UserPersonalInfoId(billingAddressId), new UserPersonalInfoGetOptions())
                .then(new Promise.Func<UserPersonalInfo, Promise<com.junbo.payment.spec.model.Address>>() {
                    @Override
                    public Promise<com.junbo.payment.spec.model.Address> apply(UserPersonalInfo userPersonalInfo) {
                        if (userPersonalInfo == null) {
                            throw AppClientExceptions.INSTANCE.billingAddressNotFound(billingAddressId.toString()).exception();
                        }
                        try {
                            Address address = ObjectMapperProvider.instance().treeToValue(
                                    userPersonalInfo.getValue(), Address.class);
                            return Promise.pure(mapAddress(address));
                        } catch (JsonProcessingException e) {
                            LOGGER.error("error parse json for address:" + billingAddressId, e);
                            throw AppClientExceptions.INSTANCE.billingAddressNotFound(billingAddressId.toString()).exception();
                        }
                    }
                });
    }

    private com.junbo.payment.spec.model.Address mapAddress(Address address){
        if(address == null){
            return null;
        }
        com.junbo.payment.spec.model.Address myAddress = new com.junbo.payment.spec.model.Address();
        myAddress.setAddressLine1(address.getStreet1());
        myAddress.setAddressLine2(address.getStreet2());
        myAddress.setAddressLine3(address.getStreet3());
        myAddress.setCountry(address.getCountryId().getValue());
        myAddress.setPostalCode(address.getPostalCode());
        myAddress.setCity(address.getCity());
        myAddress.setState(address.getSubCountry());
        myAddress.setUnitNumber(address.getStreet1());
        return myAddress;
    }

    @Override
    public Promise<String> getPhoneNumber(Long phoneNumberId) {
        return null;
    }

    @Override
    public Promise<String> getEmail(Long emailId) {
        return null;
    }

    public void setPiiClient(UserPersonalInfoResource piiClient) {
        this.piiClient = piiClient;
    }
}
