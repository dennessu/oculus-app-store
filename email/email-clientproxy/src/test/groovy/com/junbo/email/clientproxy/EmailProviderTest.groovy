package com.junbo.email.clientproxy

import com.junbo.email.spec.model.Email
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.annotation.Resource

/**
 * EmailProvider Test.
 */
class EmailProviderTest extends BaseTest {

    @Resource
    private EmailProvider emailProvider

    private Email email;

    @BeforeMethod
    void genEmail() {
        email = new Email()
        email.setLocale('en_US')
        email.setSource('oculus')
        email.setAction('welcome')
    }

    @Test(enabled = false)
    void testSendEmail() {
        def ret = emailProvider.sendEmail(email).wrapped().get()
        assert ret != null
        assert ret.status == 'FAILED'
    }
}
