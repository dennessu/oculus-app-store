package com.junbo.identity.core.service.filter.pii.impl

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserSSN
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
class SSNAdvanceFilter extends HBIPIIAdvanceFilter {
    @Override
    boolean handles(UserPersonalInfo userPersonalInfo) {
        return userPersonalInfo.type == UserPersonalInfoType.SSN.toString()
    }

    @Override
    UserPersonalInfo getFilter(UserPersonalInfo userPersonalInfo) {
        if (userPersonalInfo.value == null || AuthorizeContext.hasRights('pii.read')) {
            return userPersonalInfo
        }

        UserSSN userSSN = (UserSSN)JsonHelper.jsonNodeToObj(userPersonalInfo.value, UserSSN)
        if (StringUtils.isEmpty(userSSN.info)) {
            return userPersonalInfo
        }

        userSSN.info = mask(userSSN.info)
        userPersonalInfo.value = ObjectMapperProvider.instance().valueToTree(userSSN)
        return userPersonalInfo
    }

    private String mask(String info) {
        if (info.length() <= 2) {
            return info + '****' + info
        } else {
            String prefix = info.substring(0, 2)
            String postfix = info.substring(info.length()-2, info.length())
            return prefix + '****' + postfix
        }
    }
}
