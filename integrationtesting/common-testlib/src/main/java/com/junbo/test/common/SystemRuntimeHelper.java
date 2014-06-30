/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by weiyu_000 on 6/16/14.
 */
public class SystemRuntimeHelper {

    public static void systemRuntime(Date date) {
        String osName = System.getProperty("os.name");
        String cmd;
        try {
            if (osName.matches("^(?i)Windows.*$")) {
                cmd = "  cmd /c time 00:05:00";
                Runtime.getRuntime().exec(cmd);
                //2006-4-16
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yy");
                //String dateFormatted = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US).format(date);

                String dateFormatted = simpleDateFormat.format(date);
                cmd = " cmd /c date " + dateFormatted;
                Runtime.getRuntime().exec(cmd);
            } else {
                //20060416
                DateFormat format = new SimpleDateFormat("yyyyMMdd");
                String dateFormatted = format.format(date);
                cmd = "  date -s " + dateFormatted;
                Runtime.getRuntime().exec(cmd);
                //cmd = "  date -s 00:05:00";
                //Runtime.getRuntime().exec(cmd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
