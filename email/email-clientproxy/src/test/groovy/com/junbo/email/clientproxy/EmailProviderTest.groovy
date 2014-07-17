package com.junbo.email.clientproxy

import com.junbo.common.id.EmailTemplateId
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.annotation.Resource

/**
 * EmailProvider Test.
 */
class EmailProviderTest extends BaseTest {

    @Resource
    private EmailProvider emailProvider

    private Email email
    private EmailTemplate template

    @BeforeMethod
    void genEmail() {
        email = new Email()
        email.templateId = new EmailTemplateId(0)
        email.recipients = ['csr@silkcloud.com']

        template = new EmailTemplate()
        template.id = new EmailTemplateId(0)
    }

    @Test(enabled = false)
    void testSendEmail() {
        def ret = emailProvider.sendEmail(email, template).get()
        assert ret != null
        assert ret.status == 'FAILED'
    }
}
