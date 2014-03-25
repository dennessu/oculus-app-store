package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.model.users.User
import com.junbo.oom.core.MappingContext
import com.junbo.oom.core.filter.PropertiesToIncludeFilter
import com.junbo.oom.core.filter.PropertyMappingFilterList
import com.junbo.oom.core.filter.SkipNullPropertyFilter
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by kg on 3/19/2014.
 */
@Component
@CompileStatic
class UserFilter implements ResourceFilter<User> {

    @Override
    User filterForCreate(User user) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        return user
    }

    @Override
    User filterForPut(User user, User oldUser) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        return user
    }

    @Override
    User filterForPatch(User user, User oldUser) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        return user
    }

    @Override
    User filterForGet(User user, List<String> propertiesToInclude) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        return user
    }
}
