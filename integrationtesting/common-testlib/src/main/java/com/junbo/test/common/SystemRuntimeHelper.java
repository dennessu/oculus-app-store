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

/**
 * Created by weiyu_000 on 6/16/14.
 */
public class SystemRuntimeHelper {

      public static void systemRuntime(Date date)
      {
          String osName = System.getProperty("os.name");
          String cmd;
          try {
              if (osName.matches("^(?i)Windows.*$")) {
                  cmd = "  cmd /c time 00:05:00";
                  Runtime.getRuntime().exec(cmd);
                  //2006-4-16
                  String dateFormatted = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
                  cmd = " cmd /c date " + dateFormatted;
                  Runtime.getRuntime().exec(cmd);
              } else {
                  //20060416
                  DateFormat format = new SimpleDateFormat("yyyyMMdd");
                  String dateFormatted = format.format(date);
                  cmd = "  date -s 20090326";
                  Runtime.getRuntime().exec(cmd);
                  cmd = "  date -s 00:05:00";
                  Runtime.getRuntime().exec(cmd);
              }
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
}
