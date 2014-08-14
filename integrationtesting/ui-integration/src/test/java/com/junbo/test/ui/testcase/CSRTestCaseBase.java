package com.junbo.test.ui.testcase;

import com.junbo.test.ui.TestCaseBase;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * Created by weiyu_000 on 8/12/14.
 */

public class CSRTestCaseBase extends TestCaseBase {

    @BeforeMethod
    protected void beforeMethod(Method method) {
        super.beforeMethod(method);

    }

    @Override
    protected void initTestEnvironment() {

    }

}


