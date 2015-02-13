package com.junbo.payment.core.mock;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.UserInfoFacade;
import com.junbo.payment.spec.model.UserInfo;

/**
 * Created by Administrator on 14-5-28.
 */
public class MockUserResource implements UserInfoFacade {
    @Override
    public Promise<UserInfo> getUserInfo(Long userId) {
        UserInfo user = new UserInfo();
        user.setUserId(userId);
        user.setFirstName("utFirstName");
        user.setLastName("utLastName");
        user.setEmail("test@123.com");
        return Promise.pure(user);
    }

    @Override
    public Promise<Void> updateDefaultPI(Long userId, Long existingPiId,  Long piId) {
        return Promise.pure();
    }
}
