package com.junbo.email.db.repo.sql

import com.junbo.common.id.EmailTemplateId
import com.junbo.email.db.BaseTest
import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.email.spec.model.EmailTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * EmailTemplateRepoSqlTest Class.
 */
class EmailTemplateRepoSqlTest extends BaseTest {
    @Autowired
    @Qualifier('emailTemplateSqlRepo')
    private EmailTemplateRepository emailTemplateRepository

    private EmailTemplate template

    @BeforeClass
    private void buildTemplate() {
        template = this.buildEmailTemplate()
    }

    @Test(enabled = false)
    void testCreate() {
        def template = emailTemplateRepository.saveEmailTemplate(template).get()
        assert template != null, 'Email template create failed'
    }

    @Test(enabled = false)
    void testGet() {
        def template = emailTemplateRepository.saveEmailTemplate(template).get()
        assert template != null, 'Email template should not be null'
        template = emailTemplateRepository.getEmailTemplateByName(template.name).get()
        assert template != null, 'Email template should not be null'
        def template2 = this.buildEmailTemplate()
        template2.setLocale('zh_CN')
        template2.setName('unit.test.zh_CN')
        emailTemplateRepository.saveEmailTemplate(template2).get()
        def list = emailTemplateRepository.getEmailTemplates(null, null).get()
        assert list.size() >= 2, 'Email template list get failed'
    }

    @Test(enabled = false)
    void testUpdate() {
        def template = emailTemplateRepository.saveEmailTemplate(template).get()
        template.setPlaceholderNames(['unit','test'])
        def updated = emailTemplateRepository.updateEmailTemplate(template).get()
        assert updated != null, 'Email template update failed'
    }

    @Test(enabled = false)
    void testDelete() {
        emailTemplateRepository.saveEmailTemplate(template).get()
        emailTemplateRepository.deleteEmailTemplate(template.id.value)
        def template = emailTemplateRepository.getEmailTemplate(template.id.value).get()
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
