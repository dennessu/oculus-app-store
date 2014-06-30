package com.junbo.identity.core.service.filter.pii.impl

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserGovernmentID
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
class GovernmentIdAdvanceFilter extends HBIPIIAdvanceFilter {
    @Override
    boolean handles(UserPersonalInfo userPersonalInfo) {
        return userPersonalInfo.type == UserPersonalInfoType.GOVERNMENT_ID.toString()
    }

    @Override
    UserPersonalInfo getFilter(UserPersonalInfo userPersonalInfo) {
        if (userPersonalInfo.value == null || AuthorizeContext.hasRights('pii.read')) {
            return userPersonalInfo
        }

        UserGovernmentID userGovernmentID = (UserGovernmentID)JsonHelper.jsonNodeToObj(userPersonalInfo.value, UserGovernmentID)
        if (StringUtils.isEmpty(userGovernmentID.info)) {
            return userPersonalInfo
        }
        userGovernmentID.info = mask(userGovernmentID.info)
        userPersonalInfo.value = ObjectMapperProvider.instance().valueToTree(userGovernmentID)
        return userPersonalInfo
    }

    String mask(String governmentId) {
        if (governmentId.length() <= 2) {
            return governmentId + '****' + governmentId
        } else {
            String prefix = governmentId.substring(0, 2)
            String postfix = governmentId.substring(governmentId.length()-2, governmentId.length())
            return prefix + '****' + postfix
        }
    }
}
