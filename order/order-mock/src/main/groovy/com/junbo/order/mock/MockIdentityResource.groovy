package com.junbo.order.mock

import com.junbo.identity.spec.model.user.User
import com.junbo.identity.spec.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam

/**
 * Created by LinYi on 14-2-25.
 */

@Component('mockIdentityResource')
@CompileStatic
@Scope('prototype')
class MockIdentityResource implements UserResource {
    @Override
    Promise<User> postUser(User user) {
        return generateUser()
    }

    @Override
    Promise<List<User>> getUsers(
            @QueryParam('userName') String userName,
            @QueryParam('userNamePrefix') String userNamePrefix,
            @QueryParam('start') Integer start, @QueryParam('count') Integer count) {
        return null
    }

    @Override
    Promise<User> getUser(@PathParam('key') Long id) {
        return generateUser()
    }

    @Override
    Promise<User> putUser(@PathParam('key') Long id, User user) {
        return null
    }

    @Override
    Promise<User> authenticateUser(@QueryParam('userName') String userName, @QueryParam('password') String password) {
        return null
    }

    @Override
    Promise<User> updatePassword(
            @PathParam('key') Long id,
            @QueryParam('oldPassword') String oldPassword, @QueryParam('newPassword') String newPassword) {
        return null
    }

    @Override
    Promise<User> restPassword(@PathParam('key') Long id, @QueryParam('newPassword') String newPassword) {
        return null
    }

    Promise<User> generateUser() {
        User user = new User()
        user.setUserName('fake_user')
        user.setPassword('fake_password')
        user.setStatus('ACTIVE')
        return Promise.pure(user)
    }
}
