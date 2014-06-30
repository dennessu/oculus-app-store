package com.junbo.token.core.service;

import com.junbo.common.id.UserId;
import com.junbo.crypto.spec.model.CryptoMessage;
import com.junbo.crypto.spec.resource.CryptoResource;
import com.junbo.langur.core.promise.Promise;

/**
 * Created by Administrator on 14-6-27.
 */
public class MockCryptoServiceImpl implements CryptoResource{
    @Override
    public Promise<CryptoMessage> encrypt(UserId userId, CryptoMessage rawMessage) {
        return null;
    }

    @Override
    public Promise<CryptoMessage> encrypt(CryptoMessage rawMessage) {
        return Promise.pure(rawMessage);
    }

    @Override
    public Promise<CryptoMessage> decrypt(UserId userId, CryptoMessage encryptMessage) {
        return null;
    }

    @Override
    public Promise<CryptoMessage> decrypt(CryptoMessage encryptMessage) {
        return Promise.pure(encryptMessage);
    }
}
