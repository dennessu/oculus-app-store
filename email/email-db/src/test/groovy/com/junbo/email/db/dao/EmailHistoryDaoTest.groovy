package com.junbo.email.db.dao

import com.junbo.email.db.BaseTest
import com.junbo.email.db.entity.EmailHistoryEntity
import com.junbo.email.spec.model.EmailStatus
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

/**
 * Created by Wei on 5/15/2014.
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
        assert id == entity.id, 'Email history create failed'
    }

    @Test
    void testGet() {
        def id = emailHistoryDao.save(entity)
        def entity = emailHistoryDao.get(id)
        assert entity != null, 'Email history should not be null'
    }

    @Test
    void testUpdate() {
        entity.setSentTime(new Date())
        entity.setStatus(EmailStatus.SUCCEED.id)
        def id = emailHistoryDao.update(entity)
        assert id == entity.id, 'Email history update failed'
    }

    @Test
    void testUpdateStatus() {
        def id = emailHistoryDao.save(entity)
        def updateId = emailHistoryDao.updateStatus(id, EmailStatus.SUCCEED.id)
        def entity = emailHistoryDao.get(updateId)
        assert entity.status == EmailStatus.SUCCEED.id, 'Email history status update failed'
    }

    @Test
    void testDelete() {
        emailHistoryDao.save(entity)
        emailHistoryDao.delete(entity)
        def entity = emailHistoryDao.get(entity.id)
        assert entity == null, 'Email history delete failed'
    }
}
