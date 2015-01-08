package com.junbo.billing.clientproxy.impl.common

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by LinYi on 1/8/2015.
 */
@CompileStatic
@TypeChecked
class VatUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(VatUtil)

    private static final Map<String, String> VAT_FORMAT_MAP

    static {
        Map<String, String> vatFormatMap = new HashMap<String, String>()
        vatFormatMap.put('AU', '^((ABN)\\s)?\\d{2}\\s\\d{3}\\s\\d{3}\\s\\d{3}(\\s\\d{3})?$|^(ABN)?\\d{11}(\\d{3})?$')
        vatFormatMap.put('AT', '^ATU\\d{8}$|^AT U\\d{8}$|^EU')
        vatFormatMap.put('BE', '^BE\\d{10}$|^BE\\s0\\d{9}$|^EU')
        vatFormatMap.put('CZ', '^CZ\\d{8}$|^CZ\\d{9}$|^CZ\\d{10}$|^EU')
        vatFormatMap.put('DK', '^(DK)?\\d{8}$|^EU')
        vatFormatMap.put('FI', '^FI\\d{8}$|^EU')
        vatFormatMap.put('DE', '^DE\\d{9}$|^EU')
        vatFormatMap.put('IE', '^IE\\d{7}[A-Z]{1,2}$|^IE\\d[A-Z]\\d{5}[A-Z]$|^IE\\d\\W\\d{5}[A-Z]$|^EU')
        vatFormatMap.put('IT', '^(IT)?\\d{11}$|^EU')
        vatFormatMap.put('NL', '^NL\\d{9}B\\d{2}$|^EU')
        vatFormatMap.put('PL', '^(PL)?\\s?\\d{10}$|^(PL)?\\s?\\d{3}-\\d{2}-\\d{2}-\\d{3}$|^(PL)?\\s?\\d{3}-\\d{3}-\\d{2}-\\d{2}$|^EU')
        vatFormatMap.put('ES', '^(ES)?[A-Z0-9]\\d{7}[A-Z0-9]$|^EU')
        vatFormatMap.put('SE', '^SE\\d{12}$|^EU')
        vatFormatMap.put('GB', '^GB\\d{9}$|^GB\\d{12}$|^GB[A-Z]{2}\\d{3}$|^EU')
        VAT_FORMAT_MAP = Collections.unmodifiableMap(vatFormatMap)
    }

    static Boolean isValidFormat(String vatId, String country) {
        String REFormat = VAT_FORMAT_MAP.get(country)
        if (REFormat == null) {
            return true
        }
        Pattern p = Pattern.compile(REFormat)
        Matcher m = p.matcher(vatId)
        return m.matches()
    }

    static String splitVat(String vatId, String country) {
        if (vatId.startsWith(country) && country.length() == 2) {
            return vatId.substring(2)
        }
        else {
            return vatId
        }
    }
}
