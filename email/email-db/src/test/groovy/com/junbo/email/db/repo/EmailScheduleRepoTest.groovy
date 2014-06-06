package com.junbo.email.db.repo

import com.junbo.common.id.EmailId
import com.junbo.common.id.EmailTemplateId
import com.junbo.email.db.BaseTest
import com.junbo.email.spec.model.Email
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

/**
 * Created by Wei on 5/15/2014.
 */
class EmailScheduleRepoTest extends BaseTest {
    @Autowired
    private EmailScheduleRepository emailScheduleRepository

    private Email email

    @BeforeMethod
    private void buildEmail() {
        def email = new Email()
        email.setId(new EmailId(generateLong()))
        email.setTemplateId(new EmailTemplateId(generateLong()))
        email.setPriority(0)
        email.setRecipients(['test@example.com'])
        email.setStatus('PENDING')
        email.setRecipients(['unit','test'])
        email.setScheduleTime(new Date(2014,8,8,8,0,0))
        this.email = email
    }

    @Test
    void testCreate() {
        def email = emailScheduleRepository.saveEmailSchedule(email)
        assert email != null, 'Email schedule create failed'
    }

    @Test
    void testGet() {
        def email = emailScheduleRepository.saveEmailSchedule(email)
        def schedule = emailScheduleRepository.getEmailSchedule(email.id.value)
        assert  schedule != null, 'Email schedule should not be null'
    }

    @Test
    void testUpdate() {
        def email = emailScheduleRepository.saveEmailSchedule(email)
        email.setPriority(2)
        def schedule = emailScheduleRepository.updateEmailSchedule(email)
        assert schedule != null, 'Email schedule should not be null'
        assert schedule.priority == 2, 'Email schedule update failed'
    }

    @Test
    void testDelete() {
        def email = emailScheduleRepository.saveEmailSchedule(email)
        emailScheduleRepository.deleteEmailScheduleById(email.id.value)
        def schedule = emailScheduleRepository.getEmailSchedule(email.id.value)
        assert schedule == null, 'Email schedule delete failed'
    }

}
