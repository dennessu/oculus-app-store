package com.junbo.email.core;

import com.junbo.common.id.EmailId;
import com.junbo.common.id.EmailTemplateId;
import com.junbo.email.spec.model.Email;
import com.junbo.email.spec.model.EmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

/**
 * EmailService Test.
 */
public class EmailServiceTest extends BaseTest {
    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailTemplateService templateService;

    private Email email;
    private EmailTemplate template;

    @BeforeMethod
    private void buildEmailAndTemplate() throws Exception {
        EmailTemplate template = new EmailTemplate();
        template.setSource("unit");
        template.setAction("test");
        template.setLocale("en_US");
        template.setProviderName("ut");
        List<String> placeholderNames = new ArrayList<>();
        placeholderNames.add("unittest");
        template.setPlaceholderNames(placeholderNames);
        this.template = templateService.postEmailTemplate(template).wrapped().get();

        Email email = new Email();
        email.setTemplateId(this.template.getId());
        List<String> recipients = new ArrayList<>();
        recipients.add("test@example.com");
        email.setRecipients(recipients);
        Map<String, String> replacements = new HashMap<>();
        replacements.put("unittest","unittest");
        email.setReplacements(replacements);
        Date schedule = new Date(System.currentTimeMillis() + 1000000);
        email.setScheduleTime(schedule);
        this.email = email;
    }

    @Test
    public void testPostEmail() throws Exception {
        Email result = emailService.postEmail(email).wrapped().get();
        Assert.assertNotNull(result, "Failed to post email");
        Assert.assertEquals(result.getStatus(),"PENDING", "Email post failed");
    }

    @Test
    public void testGetEmail() throws Exception {
        Email result = emailService.postEmail(email).wrapped().get();
        Email getEmail = emailService.getEmail(result.getId().getValue()).wrapped().get();
        Assert.assertNotNull(getEmail, "Email get failed");
    }

    @Test
    public void testUpdateEmail() throws Exception {
        Email result = emailService.postEmail(email).wrapped().get();
        Date scheduleTime = new Date(System.currentTimeMillis() + 5000000);
        Long id = result.getId().getValue();
        result.setId(null);
        result.setStatus(null);
        result.setIsResend(null);
        result.setCreatedBy(null);
        result.setCreatedTime(null);
        result.setScheduleTime(scheduleTime);
        Email update = emailService.updateEmail(id, result).wrapped().get();
        Assert.assertNotNull(update, "Email should not be null");
        Assert.assertEquals(update.getScheduleTime(), scheduleTime, "Email update failed");
    }

    @Test
    public void testDeleteEmail() throws Exception {
        Email result = emailService.postEmail(email).wrapped().get();
        emailService.deleteEmail(result.getId().getValue());
        Email deleteEmail = emailService.getEmail(result.getId().getValue()).wrapped().get();
        Assert.assertNull(deleteEmail, "Email delete failed");
    }
}
