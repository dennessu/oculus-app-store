/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.ui.page;

/**
 * Created by weiyu_000 on 8/11/14.
 */
import com.google.common.base.Predicate;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.LogHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public abstract class BaseWebPage {
    protected WebDriver driver = null;
    protected static LogHelper logger;

    public BaseWebPage(WebDriver Driver) {
        this.driver = Driver;
    }

    public void waitForPagePresent() {
        new WebDriverWait(driver, Long.parseLong(ConfigHelper.getSetting("const.pageload.timeout")))
                .until(new Predicate<WebDriver>() {
                    public boolean apply(WebDriver arg0) {
                        return IsPagePresent();
                    }
                });
    }

    // This used to verify if page is actually loaded.
    public abstract boolean IsPagePresent();

    public void initControls() {

    }

    public void initPage() throws TestException {
        waitForPagePresent();

        if (IsPagePresent()) {
            initControls();
        } else {
            throw new TestException("Page is not present.");
        }
    }

    public WebElement getWebElement(By by) {
        return getWebElement(by, false);
    }

    public WebElement getWebElement(By by, boolean logWhenNotFound) {
        return getWebElement(driver, by, logWhenNotFound);
    }

    public static WebElement getWebElement(WebDriver driver, By by, boolean logWhenNotFound) {
        try {
            return driver.findElement(by);
        } catch (NoSuchElementException ex) {
            if (logWhenNotFound) {
                logger.logWarn("Element not found. " + by.toString());
            }
            return null;
        }
    }

    public static WebElement getWebElement(WebElement targetWebElement, By by) {
        return getWebElement(targetWebElement, by, false);
    }

    public static WebElement getWebElement(WebElement targetWebElement, By by, boolean logWhenNotFound) {
        try {
            return targetWebElement.findElement(by);
        } catch (NoSuchElementException ex) {
            if (logWhenNotFound) {
                logger.logWarn("Element not found. " + by.toString());
            }
            return null;
        }
    }

    public static List<WebElement> getWebElements(WebElement targetWebElement, By by) {
        return getWebElements(targetWebElement, by, false);
    }

    public static List<WebElement> getWebElements(WebElement targetWebElement, By by, boolean logWhenNotFound) {
        try {
            return targetWebElement.findElements(by);
        } catch (NoSuchElementException ex) {
            if (logWhenNotFound) {
                logger.logWarn("Element not found. " + by.toString());
            }
            return null;
        }
    }

    protected void selectItemFromUL(WebElement ul, String textToSelect) {
        List<WebElement> lis = ul.findElements(By.tagName("li"));

        for (WebElement li : lis) {
            if (li.getText().equalsIgnoreCase(textToSelect)) {
                li.click();
                break;
            }
        }
    }

    protected void selectItemFromUL(WebElement ul, int index) {
        List<WebElement> lis = ul.findElements(By.tagName("li"));
        lis.get(index).click();
    }

    public void clickButton(WebElement button) {
        if (button.isDisplayed() && button.isEnabled()) {
            button.click();
        }
    }

    public void checkCheckBox(WebElement checkBox) {
        if (checkBox.isDisplayed() && checkBox.isEnabled() && checkBox.getAttribute("checked") == null) {
            checkBox.click();
        }
    }

    public void uncheckCheckBox(WebElement checkBox) {
        if (checkBox.isDisplayed() && checkBox.isEnabled() && checkBox.getAttribute("checked") != null) {
            checkBox.click();
        }
    }

    public boolean isButtonEnabled(WebElement button) {
        if (button.getAttribute("class").contains("disabled")) {
            return false;
        }
        return true;
    }

    public boolean isCheckBoxChecked(WebElement checkBox) {
        if (checkBox.isEnabled() && checkBox.getAttribute("checked") != null) {
            return true;
        }
        return false;
    }

    public String getLinkTarget(WebElement link) {
        if (link.getAttribute("target").isEmpty()) {
            return null;
        } else {
            return link.getAttribute("target");
        }
    }

    public String getLinkUrl(WebElement link) {
        return link.getAttribute("href");
    }

    public void waitForWebElementPresent(By by) {
        long timeout = Long.parseLong(ConfigHelper.getSetting("const.pageload.timeout"));
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public void waitForWebElementNotPresent(By by) {
        long timeout = Long.parseLong(ConfigHelper.getSetting("const.pageload.timeout"));
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
    }
}
