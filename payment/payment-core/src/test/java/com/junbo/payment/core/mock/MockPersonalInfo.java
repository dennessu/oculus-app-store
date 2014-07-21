package com.junbo.payment.core.mock;

import com.junbo.common.id.AddressId;
import com.junbo.identity.spec.v1.model.Address;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.PersonalInfoFacade;

/**
 * Created by Administrator on 14-7-9.
 */
public class MockPersonalInfo implements PersonalInfoFacade {
    @Override
    public Promise<AddressId> createBillingAddress(Long userId, Address address) {
        return null;
    }

    @Override
    public Promise<com.junbo.payment.spec.model.Address> getBillingAddress(Long billingAddressId) {
        com.junbo.payment.spec.model.Address address = new com.junbo.payment.spec.model.Address();
        address.setCountry("JP");
        return Promise.pure(address);
    }

    @Override
    public Promise<String> getPhoneNumber(Long phoneNumberId) {
        return null;
    }

    @Override
    public Promise<String> getEmail(Long emailId) {
        return null;
    }
}
