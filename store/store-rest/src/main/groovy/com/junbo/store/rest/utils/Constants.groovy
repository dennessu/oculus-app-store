package com.junbo.store.rest.utils

import groovy.transform.CompileStatic

@CompileStatic
class Constants {

    public static class PersonalInfoType {
        public static final String EMAIL = 'EMAIL'
        public static final String PHONE = 'PHONE'
        public static final String NAME = 'NAME'
        public static final String ADDRESS = 'ADDRESS'
    }

    public static class UserStatus {
        public static final String ACTIVE = 'ACTIVE'
        public static final String SUSPEND = 'SUSPEND'
        public static final String BANNED = 'BANNED'
        public static final String DELETED = 'DELETED'
    }

    public static class ChallengeType {
        public static final String PASSWORD = 'PASSWORD'
        public static final String PIN = 'PIN'
        public static final String EMAIL_VERIFICATION = 'EMAIL_VERIFICATION'
    }
}
