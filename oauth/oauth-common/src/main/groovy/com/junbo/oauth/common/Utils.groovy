package com.junbo.oauth.common

import java.util.regex.Pattern

/**
 * Created by haomin on 14-7-17.
 */
class Utils {
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$', Pattern.CASE_INSENSITIVE);

    static String maskEmail(String email) {
        if (!isValidEmail(email)) {
            return email
        }

        int index = email.indexOf('@')
        String prefix = email.substring(0, index)
        String suffix = email.substring(index)

        int length = prefix.length()
        int cut = length/2 > 3 ? 3 : length/2
        cut = cut == 0 ? 1 : cut

        return email.substring(0, cut) + '******' + suffix
    }

    static boolean isValidEmail(String email) {
        if (email == null) {
            return false
        }

        return VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()
    }
}
