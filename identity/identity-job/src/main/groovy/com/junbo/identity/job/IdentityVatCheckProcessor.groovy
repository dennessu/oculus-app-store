package com.junbo.identity.job

import com.junbo.billing.spec.model.VatIdValidationResponse
import com.junbo.billing.spec.resource.VatResource
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserVAT
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 7/2/14.
 */
@CompileStatic
class IdentityVatCheckProcessor implements IdentityProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityVatCheckProcessor)

    private UserRepository userRepository

    private VatResource vatResource

    @Override
    Promise<IdentityProcessorResult> process(User user) {
        if (user.vat == null || user.vat.isEmpty()) {
            return Promise.pure(new IdentityProcessorResult(
                    success: true
            ))
        }

        IdentityProcessorResult result = new IdentityProcessorResult(
                success: true
        )
        return validateVat(user).recover { Throwable e ->
            LOGGER.error('vat update error')
            result.success = false
            return Promise.pure(null)
        }.then { User updated ->
            if (!result.success) {
                return Promise.pure(result)
            }
            return userRepository.update(updated).recover { Throwable e ->
                LOGGER.error('update user error')
                result.success = false
            }.then {
                return Promise.pure(result)
            }
        }
    }

    Promise<User> validateVat(User user) {
        if (user.vat == null || user.vat.isEmpty()) {
            return Promise.pure(null)
        }

        List<String> vatToRemove = new ArrayList<>()
        List<String> vatToUpdate = new ArrayList<>()

        return Promise.each(user.vat.entrySet()) { Map.Entry<String, UserVAT> entry ->
            UserVAT vat = entry.value
            return vatResource.validateVatId(vat.vatNumber).recover { Throwable e ->
                LOGGER.error('vatResource call error')
                vatToRemove.clear()
                vatToUpdate.clear()
                return Promise.pure(Promise.BREAK)
            }.then { VatIdValidationResponse response ->
                if (response.status == 'VALID') {
                    vatToUpdate.add(entry.key)
                } else if (response.status == 'INVALID') {
                    vatToRemove.add(entry.key)
                } else if (response.status == 'SERVICE_UNAVAILABLE' || response.status == 'UNKNOWN') {
                    // do nothing here.
                }

                return Promise.pure(null)
            }
        }.then {
            vatToRemove.each { String toRemove ->
                user.vat.remove(toRemove)
            }
            vatToUpdate.each { String toUpdate ->
                UserVAT userVAT = user.vat.get(toUpdate)
                if (userVAT != null) {
                    userVAT.lastValidateTime = new Date()
                }
            }

            return Promise.pure(user)
        }
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setVatResource(VatResource vatResource) {
        this.vatResource = vatResource
    }
}
