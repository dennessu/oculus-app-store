package com.junbo.email.db.repo.impl.cloudant

import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.EmailId
import com.junbo.email.db.repo.EmailScheduleRepository
import com.junbo.email.spec.model.Email
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Impl of EmailSchedule Repository(Cloudant).
 */
@CompileStatic
class EmailScheduleRepositoryCloudantImpl extends EmailBaseRepository<Email> implements EmailScheduleRepository {
    private IdGenerator idGenerator

    @Required
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    public Promise<Email> getEmailSchedule(Long id) {
        return super.cloudantGet(id.toString())
    }

    public Promise<Email> saveEmailSchedule(Email email) {
        email.setId(new EmailId(this.getId(email?.userId?.value)))

        return super.cloudantPost(email)
    }

    public Promise<Email> updateEmailSchedule(Email email) {
        return super.cloudantGet(email.getId().value.toString()).then {Email savedEmail ->
            if (email.priority !=null ) {
                savedEmail.setPriority(email.priority)
            }
            if (email.recipients !=null ) {
                savedEmail.setRecipients(email.recipients)
            }
            savedEmail.setUserId(email.userId)
            savedEmail.setReplacements(email.replacements)
            savedEmail.setTemplateId(email.templateId)
            savedEmail.setScheduleTime(email.scheduleTime)
            return super.cloudantPut(savedEmail)
        }
    }

    public void deleteEmailSchedule(Long id) {
        super.cloudantDelete(id.toString()).get()
    }

    private Long getId(Long userId) {
        if(userId != null) {
            return idGenerator.nextId(userId)
        }

        return idGenerator.nextId()
    }

    private CloudantViews cloudantViews = new CloudantViews() {{
        Map<String, CloudantViews.CloudantView> viewMap = new HashMap<>()

        def view = new CloudantViews.CloudantView()
        view.setMap('function(doc) {emit(doc.id, doc._id)}')
        view.setResultClass(String.class)
        viewMap.put('by_emailId', view)

        setViews(viewMap)
    }}

    @Override
    protected CloudantViews getCloudantViews() {
        return cloudantViews
    }
}
