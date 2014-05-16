package com.junbo.email.db.dao

import com.junbo.email.db.BaseTest
import com.junbo.email.db.entity.EmailTemplateEntity
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

/**
 * Created by Wei on 5/15/2014.
 */
class EmailTemplateDaoTest extends BaseTest {
    @Autowired
    private EmailTemplateDao emailTemplateDao

    private EmailTemplateEntity entity

    @BeforeMethod
    private void buildEmailTemplate() {
        entity = this.buildTemplate()
    }

    @Test
    void testCreate() {
        def id = emailTemplateDao.save(entity)
        assert id == entity.id, 'Email template create failed'
    }

    @Test
    void testGet() {
        def id = emailTemplateDao.save(entity)
        def entity = emailTemplateDao.get(id)
        assert entity != null, 'Email template get failed by id'
        entity = emailTemplateDao.getEmailTemplateByName(entity.name)
        assert entity != null, 'Email template get failed by name'
        def entity2 = this.buildTemplate()
        entity2.setLocale('zh_CN')
        entity2.setName('unit.test.zh_CN')
        emailTemplateDao.save(entity2)
        def list = emailTemplateDao.getEmailTemplatesByQuery(null,null)
        assert list.size() >= 2, 'Email template list get failed'
    }

    @Test
    void testUpdate() {
        emailTemplateDao.save(entity)
        entity.setPlaceholderNames('unittest')
        def id = emailTemplateDao.update(entity)
        assert  id == entity.id, 'Email template update failed'
        def entity = emailTemplateDao.get(id)
        assert entity != null, 'Email template should not be null'
        assert entity.placeholderNames == 'unittest', 'Email template update failed'
    }

    @Test
    void testDelete() {
        def id = emailTemplateDao.save(entity)
        emailTemplateDao.delete(entity)
        def entity = emailTemplateDao.get(id)
        assert entity == null, 'Email template delete failed'
    }

    private EmailTemplateEntity buildTemplate()
    {
        def entity = new EmailTemplateEntity()
        entity.setId(generateLong())
        entity.setSource('unit')
        entity.setAction('test')
        entity.setLocale('en_US')
        entity.setName('unit.test.en_US')
        entity.setPlaceholderNames('ut')
        entity.setProviderName('unittest')
        entity.setSubject('unit test template')
        entity.setCreatedBy('unit test')
        entity.setCreatedTime(new Date())
        return entity
    }
}
