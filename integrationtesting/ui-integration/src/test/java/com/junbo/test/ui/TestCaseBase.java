/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.ui;

import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.test.common.libs.LogHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * Created by weiyu_000 on 8/11/14.
 */
public abstract class TestCaseBase extends TestClass {
    protected WebDriver driver;
    protected String remoteURL;

    public LogHelper logger = new LogHelper(TestCaseBase.class);


    @BeforeMethod
    protected void beforeMethod(Method method) {
        logger.logInfo("***Start Test*** " + method.getDeclaringClass().getName() + "." + method.getName());
        initWebDriver();
    }

    @AfterMethod
    protected void afterMethod() throws IOException {
        if (ConfigHelper.getSetting("const.closeBrowserAfterTest").equalsIgnoreCase("true")) {
            closeWebDriver();
            Runtime.getRuntime().exec("taskkill /F /IM firefox.exe");
        }
    }

    private enum BrowserType {
        IE, FIREFOX, CHROME
    }


    protected void initWebDriver() {
        initWebDriver(BrowserType.valueOf(ConfigHelper.getSetting("browserType").toUpperCase()));
    }

    protected void closeWebDriver() {
        closeWebDriver(driver);
        driver = null;
    }

    protected void closeWebDriver(WebDriver webDriver) {
        if (webDriver != null) {
            webDriver.close();
            if (webDriver instanceof FirefoxDriver) {
                ((FirefoxDriver) webDriver).kill();
            }
        }
    }

    protected void initWebDriver(BrowserType browser) {

        switch (browser) {
            case IE:
                if (getRemoteURL() != null) {
                    DesiredCapabilities dc = DesiredCapabilities.internetExplorer();
                    driver = new RemoteWebDriver(getRemoteURL(), dc);
                    break;
                }
                driver = new InternetExplorerDriver();
                break;
            case FIREFOX:
                if (getRemoteURL() != null) {
                    DesiredCapabilities dc = DesiredCapabilities.firefox();
                    driver = new RemoteWebDriver(getRemoteURL(), dc);
                    break;
                }
                FirefoxProfile profile = new FirefoxProfile();
                profile.setPreference("browser.cache.disk.enable", false);
                driver = new FirefoxDriver(profile);
                break;
            case CHROME:
                if (getRemoteURL() != null) {
                    DesiredCapabilities dc = DesiredCapabilities.chrome();
                    driver = new RemoteWebDriver(getRemoteURL(), dc);
                    break;
                }
                //System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
                driver = new ChromeDriver();
                break;
            default:
                throw new UnsupportedOperationException();
        }

        driver.manage().window().maximize();
    }

    protected URL getRemoteURL() {
        if (remoteURL != null) {
            try {
                return new URL(remoteURL);
            } catch (Exception ex) {
            }
        }
        return null;
    }

    protected void initTestEnvironment() {}

}
