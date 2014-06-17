package com.junbo.email.db.repo.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.email.db.repo.EmailScheduleRepository
import com.junbo.email.spec.model.Email
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Impl of EmailSchedule Repository(Cloudant).
 */
@CompileStatic
class EmailScheduleRepositoryCloudantImpl extends CloudantClient<Email> implements EmailScheduleRepository {

    public Promise<Email> getEmailSchedule(String id) {
        return super.cloudantGet(id)
    }

    public Promise<Email> saveEmailSchedule(Email email) {
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

    public Promise<Void> deleteEmailSchedule(String id) {
        return super.cloudantDelete(id)
    }
}
