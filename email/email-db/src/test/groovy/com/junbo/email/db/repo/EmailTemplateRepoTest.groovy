package com.junbo.email.db.repo

import com.junbo.common.id.EmailTemplateId
import com.junbo.email.db.BaseTest
import com.junbo.email.spec.model.EmailTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

/**
 * Created by Wei on 5/15/2014.
 */
class EmailTemplateRepoTest extends BaseTest {
    @Autowired
    private EmailTemplateRepository emailTemplateRepository

    private EmailTemplate template

    @BeforeMethod
    private void buildTemplate() {
        template = this.buildEmailTemplate()
    }

    @Test
    void testCreate() {
        def id = emailTemplateRepository.saveEmailTemplate(template)
        assert id != null, 'Email template create failed'
    }

    @Test
    void testGet() {
        def id = emailTemplateRepository.saveEmailTemplate(template)
        def template = emailTemplateRepository.getEmailTemplate(id)
        assert template != null, 'Email template should not be null'
        template = emailTemplateRepository.getEmailTemplateByName(template.name)
        assert template != null, 'Email template should not be null'
        def template2 = this.buildEmailTemplate()
        template2.setLocale('zh_CN')
        template2.setName('unit.test.zh_CN')
        emailTemplateRepository.saveEmailTemplate(template2)
        def list = emailTemplateRepository.getEmailTemplates(null, null)
        assert list.size() >= 2, 'Email template list get failed'
    }

    @Test
    void testUpdate() {
        def id = emailTemplateRepository.saveEmailTemplate(template)
        def template = emailTemplateRepository.getEmailTemplate(id)
        template.setPlaceholderNames(['unit','test'])
        def updateId = emailTemplateRepository.updateEmailTemplate(template)
        assert updateId != null, 'Email template update failed'
    }

    @Test
    void testDelete() {
        def id = emailTemplateRepository.saveEmailTemplate(template)
        emailTemplateRepository.deleteEmailTemplate(id)
        def template = emailTemplateRepository.getEmailTemplate(id)
        assert template == null, 'Email template delete failed'
    }

    private EmailTemplate buildEmailTemplate() {
        def template = new EmailTemplate()
        template.setId(new EmailTemplateId(generateLong()))
        template.setSource('unit')
        template.setAction('test')
        template.setLocale('en_US')
        template.setName('unit.test.en_US')
        template.setFromAddress('from@example.com')
        template.setFromName('ut')
        template.setProviderName('unittest')
        return template
    }
}
