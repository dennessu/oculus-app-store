/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.param

import groovy.transform.CompileStatic

/**
 * OAuthParameters.
 */
@CompileStatic
class OAuthParameters {
    public static final String CLIENT_ID = 'client_id'
    public static final String CLIENT_SECRET = 'client_secret'
    public static final String REDIRECT_URI = 'redirect_uri'
    public static final String SCOPE = 'scope'
    public static final String RESPONSE_TYPE = 'response_type'
    public static final String STATE = 'state'
    public static final String NONCE = 'nonce'
    public static final String DISPLAY = 'display'
    public static final String PROMPT = 'prompt'
    public static final String ID_TOKEN_HINT = 'id_token_hint'
    public static final String LOGIN_HINT = 'login_hint'
    public static final String MAX_AGE = 'max_age'
    public static final String REMEMBER_ME = 'remember_me'
    public static final String CODE = 'code'
    public static final String GRANT_TYPE = 'grant_type'
    public static final String SALT = 'salt'
    public static final String USERNAME = 'username'
    public static final String LOGIN = 'login'
    public static final String PASSWORD = 'password'
    public static final String ACCESS_TOKEN = 'access_token'
    public static final String TOKEN_TYPE = 'token_type'
    public static final String ID_TOKEN = 'id_token'
    public static final String REFRESH_TOKEN = 'refresh_token'
    public static final String AUTHORIZATION = 'Authorization'
    public static final String CONVERSATION_ID = 'cid'
    public static final String EVENT = 'event'
    public static final String POST_LOGOUT_REDIRECT_URI = 'post_logout_redirect_uri'
    public static final String USER_ID = 'user_id'
    public static final String ID_TOKEN_USER_ID = 'id_token_user_id'
    public static final String SESSION_STATE = 'session_state'
    public static final String LOCALE = 'locale'
    public static final String EXTRA_PREFIX = 'extra_'
    public static final String EMAIL_VERIFY_CODE = 'evc'
    public static final String RESET_PASSWORD_CODE = 'rpc'
    public static final String IP_RESTRICTION = "ip_restriction"

    public static final String ERROR = 'error'
    public static final String NICK_NAME = 'nickname'
    public static final String FIRST_NAME = 'first_name'
    public static final String LAST_NAME = 'last_name'
    public static final String DOB = 'dob'
    public static final String GENDER = 'gender'
    public static final String EMAIL = 'email'
    public static final String PIN = 'pin'
    public static final String TOS = 'tos'
    public static final String USER_EMAIL = 'user_email'
    public static final String COMMUNICATION = 'communication'

    public static final String ADDRESS1 = 'address1'
    public static final String ADDRESS2 = 'address2'
    public static final String CITY = 'city'
    public static final String SUB_COUNTRY = 'sub_country'
    public static final String COUNTRY = 'country'
    public static final String ZIP_CODE = 'zip_code'

    public static final String CARD_NUMBER = 'card_number'
    public static final String NAME_ON_CARD = 'name_on_card'
    public static final String EXPIRATION_DATE = 'expiration_date'
    public static final String CVV = 'cvv'

    public static final String TFA_CODE = 'tfa'

    public static final String COOKIE_LOGIN_STATE = 'ls'
    public static final String COOKIE_SESSION_STATE = 'ss'
    public static final String COOKIE_REMEMBER_ME = 'me'
    public static final String FACEBOOK_AUTH = 'facebookAuth'
    public static final String GOOGLE_AUTH = 'googleAuth'
}

