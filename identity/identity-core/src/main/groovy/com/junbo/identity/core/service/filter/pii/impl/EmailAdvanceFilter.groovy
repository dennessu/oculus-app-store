package com.junbo.identity.core.service.filter.pii.impl

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
class EmailAdvanceFilter extends HBIPIIAdvanceFilter {
    public static final String MAIL_SEPARATOR = '@'

    @Override
    boolean handles(UserPersonalInfo userPersonalInfo) {
        return userPersonalInfo.type == UserPersonalInfoType.EMAIL.toString()
    }

    @Override
    UserPersonalInfo getFilter(UserPersonalInfo userPersonalInfo) {
        if (userPersonalInfo.value == null || AuthorizeContext.hasRights('pii.read')) {
            return userPersonalInfo
        }

        Email email = (Email)JsonHelper.jsonNodeToObj(userPersonalInfo.value, Email)
        email.info = mask(email.info)
        userPersonalInfo.value = ObjectMapperProvider.instance().valueToTree(email)
        return userPersonalInfo
    }

    private String mask(String info) {
        String[] infos = info.split(MAIL_SEPARATOR)
        String mail = infos[0]
        String domain = infos[1]

        if (mail.length() <= 2) {
            return mail + '****' + MAIL_SEPARATOR + domain
        } else {
            return mail.substring(0, 2) + '****' + MAIL_SEPARATOR + domain
        }
    }
}
