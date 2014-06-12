package com.junbo.email.db.repo.cloudant

import com.junbo.common.id.EmailId
import com.junbo.common.id.EmailTemplateId
import com.junbo.email.db.repo.EmailHistoryRepository
import com.junbo.email.spec.model.Email
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

/**
 * EmailHistoryRepoCloudantTest Class.
 */
class EmailHistoryRepoCloudantTest extends BaseCloudantTest {
    @Autowired
    @Qualifier('emailHistoryCloudantRepo')
    private EmailHistoryRepository emailHistoryRepository

    private Email email

    @BeforeClass
    private buildEmail() {
        def email = new Email()
        email.setId(new EmailId(idGenerator.nextId()))
        email.setTemplateId(new EmailTemplateId(idGenerator.nextId()))
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
}
