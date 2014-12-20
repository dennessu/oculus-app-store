package com.junbo.token.core.service;

import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.option.list.UserListOptions;
import com.junbo.identity.spec.v1.option.model.UserGetOptions;
import com.junbo.identity.spec.v1.resource.UserResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.BeanParam;
import javax.ws.rs.core.Response;

/**
 * Created by Administrator on 14-7-4.
 */
public class MockUserResource implements UserResource {
    @Override
    public Promise<User> create(User user) {
        return null;
    }

    @Override
    public Promise<User> put(UserId userId, User user) {
        return null;
    }

    @Override
    public Promise<User> get(UserId userId, @BeanParam UserGetOptions getOptions) {
        User user = new User(){
            {
                setStatus("ACTIVE");
            }
        };
        return Promise.pure(user);
    }

    @Override
    public Promise<Results<User>> list(@BeanParam UserListOptions listOptions) {
        return null;
    }

    @Override
    public Promise<Response> delete(UserId userId) {
        return null;
    }

    @Override
    public Promise<Void> checkUsername(String username) {
        return Promise.pure(null);
    }

    @Override
    public Promise<Void> checkEmail(String email) {
        return Promise.pure(null);
    }

    @Override
    public Promise<User> silentPut(UserId userId, User user) {
        return Promise.pure(null);
    }

    @Override
    public Promise<Boolean> checkUsernameEmailBlocker(String username, String email) {
        return null;
    }

    @Override
    public Promise<String> getUsernameEmailOccupyState(String username, String email) {
        return null;
    }
}
