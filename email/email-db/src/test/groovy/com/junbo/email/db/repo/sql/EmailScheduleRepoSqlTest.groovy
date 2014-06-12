package com.junbo.email.db.repo.sql

import com.junbo.common.id.EmailId
import com.junbo.common.id.EmailTemplateId
import com.junbo.email.db.BaseTest
import com.junbo.email.db.repo.EmailScheduleRepository
import com.junbo.email.spec.model.Email
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * EmailScheduleRepoSqlTest Class.
 */
class EmailScheduleRepoSqlTest extends BaseTest {
    @Autowired
    @Qualifier('emailScheduleSqlRepo')
    private EmailScheduleRepository emailScheduleRepository

    private Email email

    @BeforeClass
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
        def email = emailScheduleRepository.saveEmailSchedule(email).get()
        assert email != null, 'Email schedule create failed'
    }

    @Test
    void testGet() {
        def email = emailScheduleRepository.saveEmailSchedule(email).get()
        def schedule = emailScheduleRepository.getEmailSchedule(email.id.value).get()
        assert  schedule != null, 'Email schedule should not be null'
    }

    @Test
    void testUpdate() {
        def email = emailScheduleRepository.saveEmailSchedule(email).get()
        email.setPriority(2)
        def schedule = emailScheduleRepository.updateEmailSchedule(email).get()
        assert schedule != null, 'Email schedule should not be null'
        assert schedule.priority == 2, 'Email schedule update failed'
    }

    @Test
    void testDelete() {
        def email = emailScheduleRepository.saveEmailSchedule(email).get()
        emailScheduleRepository.deleteEmailSchedule(email.id.value)
        def schedule = emailScheduleRepository.getEmailSchedule(email.id.value).get()
        assert schedule == null, 'Email schedule delete failed'
    }

}
