package com.junbo.identity.service.impl

import com.junbo.common.id.UsernameMailBlockerId
import com.junbo.identity.data.repository.migration.UsernameEmailBlockerRepository
import com.junbo.identity.service.UsernameEmailBlockerService
import com.junbo.identity.spec.v1.model.migration.UsernameMailBlocker
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
class UsernameEmailBlockerServiceImpl implements UsernameEmailBlockerService {
    private UsernameEmailBlockerRepository usernameEmailBlockerRepository

    @Override
    Promise<UsernameMailBlocker> get(UsernameMailBlockerId id) {
        return usernameEmailBlockerRepository.get(id)
    }

    @Override
    Promise<UsernameMailBlocker> create(UsernameMailBlocker model) {
        return usernameEmailBlockerRepository.create(model)
    }

    @Override
    Promise<UsernameMailBlocker> update(UsernameMailBlocker model, UsernameMailBlocker oldModel) {
        return usernameEmailBlockerRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(UsernameMailBlockerId id) {
        return usernameEmailBlockerRepository.delete(id)
    }

    @Override
    Promise<List<UsernameMailBlocker>> searchByUsername(String username, Integer limit, Integer offset) {
        return usernameEmailBlockerRepository.searchByUsername(username, limit, offset)
    }

    @Override
    Promise<List<UsernameMailBlocker>> searchByEmail(String email, Integer limit, Integer offset) {
        return usernameEmailBlockerRepository.searchByEmail(email, limit, offset)
    }

    @Required
    void setUsernameEmailBlockerRepository(UsernameEmailBlockerRepository usernameEmailBlockerRepository) {
        this.usernameEmailBlockerRepository = usernameEmailBlockerRepository
    }
}
