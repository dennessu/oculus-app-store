/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.validators;

import java.util.Locale;
import java.util.MissingResourceException;

/**
 * Validation Utils.
 */
public class ValidationUtils {
    public static boolean isValidLocale(String locale) {
        if (locale == null) {
            return false;
        }

        locale = locale.replace('-', '_');

        String[] parts = locale.split("_");
        switch (parts.length) {
            case 2:
                return isValidLocale(new Locale(parts[0], parts[1]));
            case 1:
                return isValidLocale(new Locale(parts[0]));
            default:
                return false;
        }
    }

    private static boolean isValidLocale(Locale locale) {
        try {
            return locale.getISO3Language() != null && locale.getISO3Country() != null;
        } catch (MissingResourceException e) {
            return false;
        }
    }
}
