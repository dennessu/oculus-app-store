package com.junbo.email.db.dao

import com.junbo.email.db.BaseTest
import com.junbo.email.db.entity.EmailScheduleEntity
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

/**
 * Created by Wei on 5/15/2014.
 */
class EmailScheduleDaoTest extends BaseTest {
    @Autowired
    private EmailScheduleDao emailScheduleDao

    private EmailScheduleEntity entity

    @BeforeMethod
    private void buildEmailSchedule() {
        def entity = new EmailScheduleEntity()
        entity.setId(generateLong())
        entity.setTemplateId(generateLong())
        entity.setRecipients('test@example.com')
        entity.setScheduleTime(new Date(2014,8,8,8,0,0))
        entity.setPayload ('{"date":61368278400000}')
        entity.setCreatedBy('test')
        entity.setCreatedTime(new Date())

        this.entity = entity
    }

    @Test
    void testCreate() {
        def id = emailScheduleDao.save(entity)
        assert id == entity.id, 'Email schedule create failed'
    }

    @Test
    void testGet() {
        def id = emailScheduleDao.save(entity)
        def entity = emailScheduleDao.get(id)
        assert entity != null, 'Email schedule get failed'
    }

    @Test
    void testUpdate() {
        entity.setScheduleTime(new Date(2018,8,8,8,0,0))
        def id = emailScheduleDao.update(entity)
        def entity = emailScheduleDao.get(id)
        assert entity != null, 'Email schedule should not be null'
        assert entity.scheduleTime == new Date(2018,8,8,8,0,0), 'Email schedule update failed'
    }

    @Test
    void testDelete() {
        def id = emailScheduleDao.save(entity)
        emailScheduleDao.deleteEmailScheduleById(id)
        def entity = emailScheduleDao.get(id)
        assert entity == null, 'Email schedule delete failed'
    }
}
