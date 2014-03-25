package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.model.users.User
import groovy.transform.CompileStatic
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
