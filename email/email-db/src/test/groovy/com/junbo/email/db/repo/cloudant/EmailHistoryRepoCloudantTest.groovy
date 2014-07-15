package com.junbo.email.db.repo.cloudant

import com.junbo.common.id.EmailTemplateId
import com.junbo.email.db.repo.EmailHistoryRepository
import com.junbo.email.spec.model.Email
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testng.annotations.AfterClass
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

    private List<String> ids = new ArrayList<>()

    @BeforeClass
    private buildEmail() {
        def email = new Email()
        email.setTemplateId(new EmailTemplateId(idGenerator.nextId().toString()))
        email.setReplacements(['test':'ut'])
        email.setRecipients(['test@example.com'])
        email.setStatus('PENDING')
        this.email = email
    }

    @Test
    void testCreate() {
        def email = emailHistoryRepository.createEmailHistory(email).testGet()
        assert email != null, 'Email create failed'
        ids.add(email.getId().value)
    }

    @AfterClass
    private void cleanup() {
        if(ids != null && ids.size() != 0) {
            ids.each {String id ->
                if (id != null && id !='') {
                    emailHistoryRepository.deleteEmailHistory(id).testGet()
                }
            }
        }
    }
}
