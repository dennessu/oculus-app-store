/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.ui;

import com.junbo.test.common.libs.LogHelper;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Created by weiyu_000 on 8/11/14.
 */
public abstract class BaseWorkflow {
    public boolean needInit = true;
    public boolean needDoPreVerification = true;
    public boolean needDoAction = true;
    public boolean needDoVerification = true;
    public boolean needMoveNext = true;
    protected LogHelper logger = new LogHelper(BaseWorkflow.class);

    public String getDescription() {
        return "base work flow - description not override";
    }

    public WorkflowArgument argument;

    protected WebDriver driver = null;

    public BaseWorkflow(WebDriver driver) {
        this.driver = driver;
    }

    public void execute() {
        logger.logInfo("====== Action description: " + getDescription() + " ======");

        if (needInit) {
            init();
        }
        if (needDoPreVerification) {
            doPreVerification();
        }
        if (needDoAction) {
            doAction();
        }
        if (needDoVerification) {
            doVerification();
        }
        if (needMoveNext) {
            moveNext();
        }
    }

    public void init() {
    }

    public void doPreVerification() {
    }

    public void doAction() {
    }

    public void doVerification() {

    }

    public void moveNext() {

    }

    protected void navigate(String url) {
        logger.logInfo(String.format("Navigate to %s", url));
        driver.navigate().to(url);
    }

    protected void navigateWithoutWait(String url) {
        logger.logInfo(String.format("Navigate to %s", url));
        ((JavascriptExecutor) driver).executeScript("window.location='" + url + "'");
    }
}
