package com.junbo.identity.core.service.filter.pii.impl

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserPassport
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
class PassportAdvanceFilter extends HBIPIIAdvanceFilter {
    @Override
    boolean handles(UserPersonalInfo userPersonalInfo) {
        return userPersonalInfo.type == UserPersonalInfoType.PASSPORT.toString()
    }

    @Override
    UserPersonalInfo getFilter(UserPersonalInfo userPersonalInfo) {
        if (userPersonalInfo.value == null || AuthorizeContext.hasRights('pii.read')) {
            return userPersonalInfo
        }

        UserPassport passport = (UserPassport)JsonHelper.jsonNodeToObj(userPersonalInfo.value, UserPassport)
        if (StringUtils.isEmpty(passport.info)) {
            return userPersonalInfo
        }
        passport.info = mask(passport.info)
        userPersonalInfo.value = ObjectMapperProvider.instance().valueToTree(passport)
        return userPersonalInfo
    }

    String mask(String passport) {
        if (passport.length() <= 2) {
            return passport + '****' + passport
        } else {
            String prefix = passport.substring(0, 2)
            String postfix = passport.substring(passport.length()-2, passport.length())
            return prefix + '****' + postfix
        }
    }
}
