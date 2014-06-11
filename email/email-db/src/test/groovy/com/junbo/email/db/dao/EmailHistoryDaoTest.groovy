package com.junbo.email.db.dao

import com.junbo.email.db.BaseTest
import com.junbo.email.db.entity.EmailHistoryEntity
import com.junbo.email.spec.model.EmailStatus
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

/**
 * EmailHistoryDaoTest Class.
 */
class EmailHistoryDaoTest extends BaseTest {
    @Autowired
    private EmailHistoryDao emailHistoryDao

    private EmailHistoryEntity entity

    @BeforeMethod
    private void buildEmail() {
        def entity = new EmailHistoryEntity()
        entity.setId(generateLong())
        entity.setTemplateId(generateLong())
        entity.setRecipients('test@example.com')
        entity.setStatus(EmailStatus.FAILED.id)
        entity.setPayload ('{"recipients":"test@example.com"}')
        entity.setCreatedBy('test')
        entity.setCreatedTime(new Date())

        this.entity = entity
    }

    @Test
    void testCreate() {
        def id = emailHistoryDao.save(entity)
        assert id != null, 'Email history create failed'
    }

    @Test
    void testGet() {
        def entity = emailHistoryDao.save(entity)
        assert entity != null, 'Email history should not be null'
    }

    @Test
    void testUpdate() {
        entity.setSentTime(new Date())
        entity.setStatus(EmailStatus.SUCCEED.id)
        def entity = emailHistoryDao.update(entity)
        assert entity != null, 'Email history update failed'
    }

    @Test
    void testUpdateStatus() {
        def entity = emailHistoryDao.save(entity)
        def updated = emailHistoryDao.updateStatus(entity.id, EmailStatus.SUCCEED.id)
        assert updated.status == EmailStatus.SUCCEED.id, 'Email history status update failed'
    }

    @Test
    void testDelete() {
        emailHistoryDao.save(entity)
        emailHistoryDao.delete(entity)
        def entity = emailHistoryDao.get(entity.id)
        assert entity == null, 'Email history delete failed'
    }
}
