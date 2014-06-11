package com.junbo.email.db.dao

import com.junbo.email.db.BaseTest
import com.junbo.email.db.entity.EmailTemplateEntity
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

/**
 * EmailTemplateDaoTest Class.
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
        def entity = emailTemplateDao.save(entity)
        assert entity != null, 'Email template create failed'
    }

    @Test
    void testGet() {
        def entity = emailTemplateDao.save(entity)
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
        def entity = emailTemplateDao.update(entity)
        assert  entity != null, 'Email template update failed'
        assert entity.placeholderNames == 'unittest', 'Email template update failed'
    }

    @Test
    void testDelete() {
        emailTemplateDao.save(entity)
        emailTemplateDao.delete(entity)
        def entity = emailTemplateDao.get(entity.id)
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
