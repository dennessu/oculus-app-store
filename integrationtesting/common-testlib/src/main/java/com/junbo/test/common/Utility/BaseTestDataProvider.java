/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Utility;

import com.junbo.test.common.libs.DBHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Yunlong on 4/4/14.
 */
public abstract class BaseTestDataProvider {
    protected DBHelper dbHelper = new DBHelper();

    public BaseTestDataProvider() {
    }

    protected String readFileContent(String resourceLocation) throws Exception {

        InputStream inStream = ClassLoader.getSystemResourceAsStream(resourceLocation);
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));

        StringBuilder strItem = new StringBuilder();
        try {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                strItem.append(sCurrentLine + "\n");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null) {
                br.close();
            }
            if (inStream != null) {
                inStream.close();
            }
        }

        return strItem.toString();
    }

}
