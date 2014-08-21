package com.junbo.order.mock
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.BeanParam
import javax.ws.rs.PathParam
/**
 * Created by LinYi on 14-2-25.
 */

@Component('mockIdentityResource')
@CompileStatic
@TypeChecked
class MockIdentityResource implements UserResource {

    static Promise<User> generateUser() {
        User user = new User()
        user.username = new UserPersonalInfoId(0L)
        return Promise.pure(user)
    }

    @Override
    Promise<User> create(User user) {
        return generateUser()
    }

    @Override
    Promise<User> put(@PathParam('userId') UserId userId, User user) {
        return null
    }

    @Override
    Promise<User> patch(@PathParam('userId') UserId userId, User user) {
        return null
    }

    @Override
    Promise<User> get(@PathParam('userId') UserId userId, @BeanParam UserGetOptions getOptions) {
        return generateUser()
    }

    @Override
    Promise<Results<User>> list(@BeanParam UserListOptions listOptions) {
        return null
    }

    @Override
    Promise<Void> delete(@PathParam("userId") UserId userId) {
        return null
    }

    @Override
    Promise<Void> checkUsername(String username) {
        return Promise.pure(null)
    }

    @Override
    Promise<Void> checkEmail(String email) {
        return Promise.pure(null)
    }

    @Override
    Promise<User> silentPut(UserId userId, User user) {
        return Promise.pure(null)
    }
}
