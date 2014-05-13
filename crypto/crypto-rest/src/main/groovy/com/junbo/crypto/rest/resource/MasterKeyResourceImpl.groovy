package com.junbo.crypto.rest.resource

import com.junbo.crypto.core.validator.MasterKeyValidator
import com.junbo.crypto.spec.model.MasterKey
import com.junbo.crypto.spec.resource.MasterKeyResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 5/13/14.
 */
@CompileStatic
@Transactional
@SuppressWarnings('UnnecessaryGetter')
class MasterKeyResourceImpl extends CommonResourceImpl implements MasterKeyResource  {

    private MasterKeyValidator masterKeyValidator

    @Override
    Promise<Void> create(MasterKey masterKey) {
        return masterKeyValidator.validateMasterKeyCreate(masterKey).then {

            return getCurrentMasterKey().then { Integer keyVersion ->
                masterKey.encryptValue = asymmetricEncryptMasterKey(masterKey.value)
                masterKey.keyVersion = keyVersion + 1

                return masterKeyRepo.create(masterKey).then {
                    return Promise.pure(null)
                }
            }
        }
    }

    @Required
    void setMasterKeyValidator(MasterKeyValidator masterKeyValidator) {
        this.masterKeyValidator = masterKeyValidator
    }
}
