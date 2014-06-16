package com.junbo.email.core;

import com.junbo.common.model.Results;
import com.junbo.email.spec.model.EmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * EmailTemplateServiceTest Class.
 */
public class EmailTemplateServiceTest extends BaseTest {
    @Autowired
    @Qualifier("templateService")
    private EmailTemplateService templateService;

    private EmailTemplate template;

    @BeforeClass
    private void buildTemplate() {
        this.template = this.buildEmailTemplate();
    }

    @Test(enabled = false)
    public void testCreate() throws Exception {
        EmailTemplate emailTemplate = templateService.postEmailTemplate(template).get();
        Assert.assertNotNull(emailTemplate,"Email template should not be null");
    }

    @Test(enabled = false)
    public void testGet() throws Exception {
        EmailTemplate saveTemplate = templateService.postEmailTemplate(template).get();
        EmailTemplate emailTemplate = templateService.getEmailTemplate(saveTemplate.getId().getValue()).get();
        Assert.assertNotNull(emailTemplate,"Email template get failed");
        EmailTemplate template2 = this.buildEmailTemplate();
        template2.setLocale("zh_CN");
        EmailTemplate saveTemplate2 = templateService.postEmailTemplate(template2).get();
        Results<EmailTemplate> list = templateService.getEmailTemplates(null).get();
        Assert.assertEquals(list.getItems().size() >= 2 , true, "Email template list get failed" );
    }

    @Test(enabled = false)
    public void testUpdate() throws Exception {
        EmailTemplate emailTemplate = templateService.postEmailTemplate(template).get();
        String id = emailTemplate.getId().getValue();
        emailTemplate.setId(null);
        emailTemplate.setProviderName("unittest");
        EmailTemplate updateTemplate = templateService.putEmailTemplate(id, emailTemplate).get();
        Assert.assertNotNull(updateTemplate,"Email template should not be null");
        Assert.assertEquals(updateTemplate.getProviderName(), "unittest", "Email template update failed");
    }

    @Test(enabled = false)
    public void testDelete() throws Exception {

        EmailTemplate emailTemplate = templateService.postEmailTemplate(template).get();
        templateService.deleteEmailTemplate(emailTemplate.getId().getValue()).get();
        EmailTemplate deleteTemplate = templateService.getEmailTemplate(emailTemplate.getId().getValue())
                .get();
        Assert.assertNull(deleteTemplate, "Email template delete failed");
    }

    private EmailTemplate buildEmailTemplate() {
        EmailTemplate template = new EmailTemplate();
        template.setSource("unit");
        template.setAction("test");
        template.setLocale("en_US");
        template.setProviderName("ut");
        List<String> placeholderNames = new ArrayList<>();
        placeholderNames.add("unittest");
        template.setPlaceholderNames(placeholderNames);
        return template;
    }
}
