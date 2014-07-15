package com.junbo.email.db.repo.cloudant
import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.email.spec.model.EmailTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testng.annotations.AfterClass
import org.testng.annotations.Test
/**
 *  EmailTemplateRepoCloudantTest Class.
 */
class EmailTemplateRepoCloudantTest extends BaseCloudantTest {
    @Autowired
    @Qualifier('emailTemplateCloudantRepo')
    private EmailTemplateRepository templateRepository

    private List<String> ids = new ArrayList<>()

    @Test
    void testCreate() {
        def template = templateRepository.saveEmailTemplate(buildEmailTemplate()).testGet()
        assert template != null, 'Email template create failed'
        ids.add(template?.getId()?.value)
    }

    @Test
    void testGet() {
        def template = templateRepository.saveEmailTemplate(buildEmailTemplate()).testGet()
        assert template != null, 'Email template should not be null'
        ids.add(template?.getId()?.value)
        template = templateRepository.getEmailTemplateByName(template.name).testGet()
        assert template != null, 'Email template should not be null'
        def template2 = this.buildEmailTemplate()
        template2.setLocale('zh_CN')
        template2.setName('unit.test.zh_CN')
        templateRepository.saveEmailTemplate(template2).testGet()
        def list = templateRepository.getEmailTemplates(null, null).testGet()
        ids.add(template2?.getId()?.value)
        assert list.size() >= 2, 'Email template list get failed'

    }

    @Test
    void testUpdate() {
        def template = templateRepository.saveEmailTemplate(buildEmailTemplate()).testGet()
        template.setPlaceholderNames(['unit','test'])
        ids.add(template?.getId()?.value)
        def updated = templateRepository.updateEmailTemplate(template).testGet()
        assert updated != null, 'Email template update failed'
    }

    @Test
    void testDelete() {
        def template = templateRepository.saveEmailTemplate(buildEmailTemplate()).testGet()
        templateRepository.deleteEmailTemplate(template.id.value).testGet()
        template = templateRepository.getEmailTemplate(template.id.value).testGet()
        ids.add(template?.getId()?.value)
        assert template == null, 'Email template delete failed'
    }

    private EmailTemplate buildEmailTemplate() {
        def template = new EmailTemplate()
        template.setSource('unit')
        template.setAction('test')
        template.setLocale('en_US')
        template.setName('unit.test.en_US')
        template.setFromAddress('from@example.com')
        template.setFromName('ut')
        template.setProviderName('unittest')
        return template
    }

    @AfterClass
    private void cleanup() {
        if (ids != null && ids.size() != 0) {
            ids.each { String id ->
                if (id != null && id !='') {
                    templateRepository.deleteEmailTemplate(id).testGet()
                }
            }
        }
    }
}
