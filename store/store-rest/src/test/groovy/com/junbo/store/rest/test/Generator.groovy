package com.junbo.store.rest.test

import com.junbo.common.enumid.CountryId
import com.junbo.common.id.PIType
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.Email
import com.junbo.store.spec.model.Address
import com.junbo.store.spec.model.billing.Instrument
import com.junbo.store.spec.model.identity.PersonalInfo
import com.junbo.store.spec.model.login.CreateUserRequest
import org.apache.commons.lang.RandomStringUtils
/**
 * The Generator class.
 */
class Generator {

    public static String cardNum = 'adyenjs_0_1_4$XOvON6cMdleOStk49ASS1gPzqoe/YLlcBMqp5fzN5uq3qnfWoJlwyXsxMpgDHh72lQ+iu1fzs9i/8B54wsTuuIf6VSKosK+XAAnhLb17M7SbNp5s02riwXpgAux3Kcm3JziiWDtKEgdsTL9xz+0z79QYbb7++q8hdwBKWc06UHWYaWPOzkCTV2Z+hkosfjTSpiO9lLNRLB8Jd61ppKR/Xq+XxFcoIoUFmgBOWPUXyHFvNzKusBxm0XUCZjkHm3VI/GJWuEG69l/U1lt6OMMWQYqG73ustctunVKY9AcUfO5ofSK0wzAHt9wN4Un1Po3On5j8f7chYWDmSEmKpi9qTg==$MFzbnMtpdO7GOMlFe3vxroxg+r5HWIiSdJ1C2v4yJrBcpcCWiqfLiiDhmAdt3e0XPFBtoDDV8za8GhZmSxTIj1RRRTiqRCYs4dKdtlQjaXlpBBohOc21UeY0dFYeHRX/H8SQ9B0mVWXi5lVsRtMtpMQi9CPpAZyd8MGk3n22+Eg6XIRkK9eaddpbPLkZ5JUqtnhpF5bNbtHJTYJVxvhEUaS1Pn2szxHg'

    static String genUserName() {
        return RandomStringUtils.randomAlphabetic(20)
    }

    static String genPassword() {
        return RandomStringUtils.randomAlphabetic(10).toLowerCase() + RandomStringUtils.randomAlphabetic(2).toUpperCase() + RandomStringUtils.randomNumeric(1) + '$'
    }

    static String genHeadline() {
        return RandomStringUtils.randomAlphabetic(20)
    }

    static String genEmail() {
        return "${RandomStringUtils.randomAlphabetic(20)}@test.com"
    }

    static String genPIN() {
        return RandomStringUtils.randomNumeric(4)
    }

    static Email genEmailModel() {
        Email email = new Email()
        email.info = genEmail()
        return email
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
        instrument.accountName = 'John Doe'
        instrument.accountNum = cardNum
        instrument.type = PIType.CREDITCARD.name()

        Address billingAddres = new Address(
            street1: '1600 Amphitheatre Parkway',
            street2: RandomStringUtils.randomAlphabetic(6),
            street3: RandomStringUtils.randomAlphabetic(6),
            postalCode: '94043',
            city: 'Mountain View',
            subCountry: 'CA',
            country: new CountryId('US')
        )
        instrument.billingAddress = billingAddres
        return instrument
    }



}
