package com.junbo.email.db.repo.cloudant

import com.junbo.common.id.EmailTemplateId
import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.email.spec.model.EmailTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 *  EmailTemplateRepoCloudantTest Class.
 */
class EmailTemplateRepoCloudantTest extends BaseCloudantTest {
    @Autowired
    @Qualifier('emailTemplateCloudantRepo')
    private EmailTemplateRepository templateRepository

    private EmailTemplate template

    @BeforeClass
    private void buildTemplate() {
        template = this.buildEmailTemplate()
    }

    @Test(enabled = false)
    void testCreate() {
        def template = templateRepository.saveEmailTemplate(template).get()
        assert template != null, 'Email template create failed'
    }

    @Test(enabled = false)
    void testGet() {
        def template = templateRepository.saveEmailTemplate(template).get()
        assert template != null, 'Email template should not be null'
        template = templateRepository.getEmailTemplateByName(template.name).get()
        assert template != null, 'Email template should not be null'
        def template2 = this.buildEmailTemplate()
        template2.setLocale('zh_CN')
        template2.setName('unit.test.zh_CN')
        templateRepository.saveEmailTemplate(template2).get()
        def list = templateRepository.getEmailTemplates(null, null).get()
        assert list.size() >= 2, 'Email template list get failed'
    }

    @Test(enabled = false)
    void testUpdate() {
        def template = templateRepository.saveEmailTemplate(template).get()
        template.setPlaceholderNames(['unit','test'])
        def updated = templateRepository.updateEmailTemplate(template).get()
        assert updated != null, 'Email template update failed'
    }

    @Test(enabled = false)
    void testDelete() {
        templateRepository.saveEmailTemplate(template).get()
        templateRepository.deleteEmailTemplate(template.id.value)
        def template = templateRepository.getEmailTemplate(template.id.value).get()
        assert template == null, 'Email template delete failed'
    }

    private EmailTemplate buildEmailTemplate() {
        def template = new EmailTemplate()
        template.setId(new EmailTemplateId(idGenerator.nextId()))
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
