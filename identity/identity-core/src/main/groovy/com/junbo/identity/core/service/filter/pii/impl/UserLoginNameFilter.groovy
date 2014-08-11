package com.junbo.identity.core.service.filter.pii.impl

import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserLoginName
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 8/6/14.
 */
@CompileStatic
class UserLoginNameFilter extends LBIPIIAdvanceFilter {
    @Override
    boolean handles(UserPersonalInfo userPersonalInfo) {
        return userPersonalInfo.type == UserPersonalInfoType.USERNAME.toString()
    }

    @Override
    UserPersonalInfo getFilter(UserPersonalInfo userPersonalInfo) {
        if (userPersonalInfo.value == null) {
            return userPersonalInfo
        }

        UserLoginName userLoginName = (UserLoginName)JsonHelper.jsonNodeToObj(userPersonalInfo.value, UserLoginName)
        userLoginName.canonicalUsername = null
        userPersonalInfo.value = ObjectMapperProvider.instance().valueToTree(userLoginName)
        return userPersonalInfo
    }
}
