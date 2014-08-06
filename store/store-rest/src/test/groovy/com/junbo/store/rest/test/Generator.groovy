package com.junbo.store.rest.test

import com.junbo.common.enumid.CountryId
import com.junbo.common.id.PIType
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.Address
import com.junbo.store.spec.model.billing.Instrument
import com.junbo.store.spec.model.identity.PersonalInfo
import com.junbo.store.spec.model.login.CreateUserRequest
import org.apache.commons.lang.RandomStringUtils
/**
 * The Generator class.
 */
class Generator {

    public static String cardNum = 'adyenjs_0_1_4$eB7SFZPd76Hc0cWWHgHUKYPCAQdoCQMXpHCLexH2GW+UtjIkJbd0YLPBX3qdz5MSnslN22+iuUbu9Q1ByewDwcrB0zMtd1/OXyDwdcj0FB0C6qtvB/45r4cyHiHnxL678SXkjH6/HRRpU4lk79iNnaXrGj6CHtRVI3OzHf4N+8aCp0ckC/JtDOFzqARddvjiwC6pXJ8KaCYKU3fdQGebduQ55E6IGNgLcSvX7XIRfZbzRHEHfOpzp8faE2bkZg2VXCWORf8ZjMC7zDeOf3w5zL0GIgtjANRo1Rducc+rJhCBn8xRiV0qCLaDY9RwyferElFX68BH7qQfHrwXZaeELQ==$ShHxmtfYn+ojmRML6hzti+YrwPrWw0YEXGD70lKBsdAL4+M+/7FB9HfAiOcWv6e508wuC/gxI5WOLux6yuWFlq0YiBgTUHzC4y5+GOZwsh63NOW5cjxjtk4qW5+quSklJc/s/56TrfQu9g0hCsPFe0vEbgLqpkieSJFe+gaJG4rDA6N02MrX4p4A1BX91bi62PJwOmk1LiNfqHbQJ5+eZtiq6dJeLqyZ'

    static String genUserName() {
        return RandomStringUtils.randomAlphabetic(20)
    }

    static String genPassword() {
        return RandomStringUtils.randomAlphabetic(10).toLowerCase() + RandomStringUtils.randomAlphabetic(2).toUpperCase() + RandomStringUtils.randomNumeric(1) + '$'
    }

    static String genEmail() {
        return "${RandomStringUtils.randomAlphabetic(20)}@test.com"
    }

    static String genPIN() {
        return RandomStringUtils.randomNumeric(4)
    }

    static CreateUserRequest genCreateUserRequest(String username, String password, String email, String pinCode) {
        return new CreateUserRequest(
                username: username,
                firstName: 'First',
                lastName: 'Last',
                password: password,
                pinCode: pinCode,
                dob: new Date(System.currentTimeMillis() - 8 * 365L * 24 * 3600 * 1000),
                preferredLocale: 'en_US',
                email: email,
                cor: 'US'
        )
    }

    static Instrument generateCreditCardInstrument() {
        Instrument instrument = new Instrument()
        instrument.accountName = RandomStringUtils.randomAlphabetic(5)
        instrument.accountNum = cardNum
        instrument.type = PIType.CREDITCARD.name()
        instrument.creditCardType = 'VISA'

        Address billingAddres = new Address(
            street1: '1600 Amphitheatre Parkway',
            street2: RandomStringUtils.randomAlphabetic(6),
            street3: RandomStringUtils.randomAlphabetic(6),
            postalCode: '94043',
            city: 'Mountain View',
            subCountry: 'CA',
            countryId: new CountryId('US')
        )
        instrument.billingAddress = new PersonalInfo(type: 'ADDRESS', value: ObjectMapperProvider.instance().valueToTree(billingAddres))
        return instrument
    }



}
