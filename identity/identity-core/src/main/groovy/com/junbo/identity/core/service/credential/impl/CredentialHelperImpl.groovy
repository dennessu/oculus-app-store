package com.junbo.identity.core.service.credential.impl

import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.common.util.Constants
import com.junbo.identity.core.service.credential.CredentialHash
import com.junbo.identity.core.service.credential.CredentialHashFactory
import com.junbo.identity.core.service.credential.CredentialHelper
import com.junbo.identity.service.UserPasswordService
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.slf4j.LoggerFactory
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Created by xiali_000 on 2014/12/18.
 */
@CompileStatic
class CredentialHelperImpl implements CredentialHelper {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CredentialHelperImpl)

    private static Map htmlEncode = new HashMap<String, String>()

    static {
        htmlEncode.put('<','&lt;')
        htmlEncode.put('>', '&gt;')
        htmlEncode.put('&', '&amp;')
    }

    private UserPasswordService userPasswordService

    private Integer currentCredentialVersion

    private CredentialHashFactory credentialHashFactory

    // Any data that will use this data should be data issue, we may need to fix this.
    private Integer maximumFetchSize

    private Boolean updateHtml

    // The logic is as follows:
    // First check whether current password version is the required version
    @Override
    Promise<Boolean> isValidPassword(UserId userId, String password) {
        if (userId == null || StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException('userId or password is null')
        }

        return userPasswordService.searchByUserIdAndActiveStatus(userId, true, maximumFetchSize, 0).then {
            Results<UserPassword> userPasswordList ->
                if (userPasswordList == null || CollectionUtils.isEmpty(userPasswordList.items) || userPasswordList.items.size() > 1) {
                    return Promise.pure(false)
                }

                String[] split = userPasswordList.items.get(0).passwordHash.split(Constants.COLON)
                if (split.length <= 1) {
                    return Promise.pure(false)
                }
                Integer passwordVersion = null
                try {
                    passwordVersion = Integer.parseInt(split[0])
                } catch (Exception e) {
                    LOGGER.error('current password has no version info', e)
                    return Promise.pure(false)
                }

                List<CredentialHash> credentialHashList = credentialHashFactory.getAllCredentialHash()
                CredentialHash matched = credentialHashList.find { CredentialHash hash ->
                    return hash.handles(passwordVersion)
                }

                if (matched == null) {
                    throw new IllegalStateException('current password version is not supported')
                }

                if (!updateHtml || currentCredentialVersion == passwordVersion) {
                    // don't need to do any html change
                    return Promise.pure(matched.matches(password, userPasswordList.items.get(0).passwordHash))
                } else {
                    // Need to convert data to html and update the password
                    String converted = toConvertedHtml(password)
                    if (matched.matches(converted, userPasswordList.items.get(0).passwordHash)) {
                        // need to update this to the lastest version
                        UserPassword userPassword = new UserPassword()
                        userPassword.expiresBy = userPasswordList.items.get(0).expiresBy
                        userPassword.changeAtNextLogin = userPasswordList.items.get(0).changeAtNextLogin
                        userPassword.userId = userPasswordList.items.get(0).userId
                        userPassword.active = true
                        userPassword.strength = userPasswordList.items.get(0).strength

                        CredentialHash currentCredentialMatched = credentialHashList.find { CredentialHash hash ->
                            return hash.handles(currentCredentialVersion)
                        }

                        if (currentCredentialMatched == null) {
                            throw new IllegalStateException('current password version is not supported')
                        }
                        userPassword.passwordHash = currentCredentialMatched.hash(password)
                        return userPasswordService.create(userPassword).then {
                            userPasswordList.items.get(0).setActive(false)
                            return userPasswordService.update(userPasswordList.items.get(0), userPasswordList.items.get(0)).then {
                                return Promise.pure(true)
                            }
                        }
                    }

                    return Promise.pure(false)
                }
        }
    }

    public static String toConvertedHtml(String raw) {
        if (StringUtils.isEmpty(raw)) {
            return raw
        }

        String converted = raw
        Iterator it = htmlEncode.entrySet().iterator()
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next()
            String key = entry.key
            String value = entry.value

            converted = converted.replaceAll(key, value)
        }

        return converted
    }

    @Required
    void setUserPasswordService(UserPasswordService userPasswordService) {
        this.userPasswordService = userPasswordService
    }

    @Required
    void setCurrentCredentialVersion(Integer currentCredentialVersion) {
        this.currentCredentialVersion = currentCredentialVersion
    }

    @Required
    void setCredentialHashFactory(CredentialHashFactory credentialHashFactory) {
        this.credentialHashFactory = credentialHashFactory
    }

    @Required
    void setMaximumFetchSize(Integer maximumFetchSize) {
        this.maximumFetchSize = maximumFetchSize
    }

    @Required
    void setUpdateHtml(Boolean updateHtml) {
        this.updateHtml = updateHtml
    }
}
