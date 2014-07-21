package com.junbo.email.db.repo.sql

import com.junbo.common.id.EmailId
import com.junbo.common.id.EmailTemplateId
import com.junbo.email.db.BaseTest
import com.junbo.email.db.repo.EmailHistoryRepository
import com.junbo.email.spec.model.Email
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * EmailHistoryRepoSqlTest Class.
 */
class EmailHistoryRepoSqlTest extends BaseTest {
    @Autowired
    @Qualifier('emailHistorySqlRepo')
    private EmailHistoryRepository emailHistoryRepository

    private Email email

    @BeforeClass
    private buildEmail() {
        def email = new Email()
        email.setId(new EmailId(generateLong()))
        email.setTemplateId(new EmailTemplateId(generateLong()))
        email.setReplacements(['test':'ut'])
        email.setRecipients(['test@example.com'])
        email.setStatus('PENDING')
        this.email = email
    }

    @Test(enabled = false)
    void testCreate() {
        def email = emailHistoryRepository.createEmailHistory(email).get()
        assert email != null, 'Email create failed'
    }

    @Test(enabled = false)
    void testGet() {
        def email = emailHistoryRepository.createEmailHistory(email).get()
        assert email != null, 'Email should not be null'
    }

    @Test(enabled = false)
    void testUpdate() {
        def email = emailHistoryRepository.createEmailHistory(email).get()
        email.setStatus('SUCCEED')
        assert email.getTemplateId() != null, "email id value: ${email.id?.value}"
        def updated = emailHistoryRepository.updateEmailHistory(email, email).get()
        assert updated != null, 'Email should not be null'
        assert updated.status == 'SUCCEED', 'Email update failed'
    }
}
