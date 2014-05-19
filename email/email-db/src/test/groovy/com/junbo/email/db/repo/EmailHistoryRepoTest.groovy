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
class EmailHistoryRepoTest extends BaseTest {
    @Autowired
    private EmailHistoryRepository emailHistoryRepository

    private Email email

    @BeforeMethod
    private buildEmail() {
        def email = new Email()
        email.setId(new EmailId(generateLong()))
        email.setTemplateId(new EmailTemplateId(generateLong()))
        email.setReplacements(['test':'ut'])
        email.setRecipients(['test@example.com'])
        email.setStatus('PENDING')
        this.email = email
    }

    @Test
    void testCreate() {
        def id = emailHistoryRepository.createEmailHistory(email)
        assert id != null, 'Email create failed'
    }

    @Test
    void testGet() {
        def id = emailHistoryRepository.createEmailHistory(email)
        def email = emailHistoryRepository.getEmail(id)
        assert email != null, 'Email should not be null'
    }

    @Test
    void testUpdate() {
        def id = emailHistoryRepository.createEmailHistory(email)
        email.setId(new EmailId(id))
        email.setStatus('SUCCEED')
        def updateId = emailHistoryRepository.updateEmailHistory(email)
        def email = emailHistoryRepository.getEmail(updateId)
        assert email != null, 'Email should not be null'
        assert email.status == 'SUCCEED', 'Email update failed'
    }
}
