/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.test;

import com.junbo.configuration.ConfigContext;
import com.junbo.configuration.ConfigService;
import com.junbo.configuration.reloadable.IntegerConfig;
import com.junbo.configuration.reloadable.StringConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**.
 * Java doc for configuration Test.
 */
public class ConfigurationTest {
    private static Logger logger = Logger.getLogger(ConfigurationTest.class);

    /**.
     * Java doc
     */
    public static final class ConfigBag {
        private IntegerConfig integerConfig;
        private StringConfig stringConfig;
        private String rawStringConfig;

        public IntegerConfig getIntegerConfig() {
            return integerConfig;
        }

        public void setIntegerConfig(IntegerConfig integerConfig) {
            this.integerConfig = integerConfig;
        }

        public StringConfig getStringConfig() {
            return stringConfig;
        }

        public void setStringConfig(StringConfig stringConfig) {
            this.stringConfig = stringConfig;
        }

        public String getRawStringConfig() {
            return rawStringConfig;
        }

        public void setRawStringConfig(String rawStringConfig) {
            this.rawStringConfig = rawStringConfig;
        }
    }

    /**.
     * Java doc
     */
    public static final class JarConfigBag {
        private StringConfig oneboxCatalog;
        private StringConfig oneboxBilling;
        private StringConfig oneboxDefault;
        private StringConfig oneboxIdentity;
        private StringConfig defaultDefault;
        private StringConfig defaultEntitlement;
        private StringConfig defaultBilling;

        public void setOneboxCatalog(StringConfig oneboxCatalog) {
            this.oneboxCatalog = oneboxCatalog;
        }

        public void setOneboxBilling(StringConfig oneboxBilling) {
            this.oneboxBilling = oneboxBilling;
        }

        public void setOneboxDefault(StringConfig oneboxDefault) {
            this.oneboxDefault = oneboxDefault;
        }

        public void setOneboxIdentity(StringConfig oneboxIdentity) {
            this.oneboxIdentity = oneboxIdentity;
        }

        public void setDefaultDefault(StringConfig defaultDefault) {
            this.defaultDefault = defaultDefault;
        }

        public void setDefaultEntitlement(StringConfig defaultEntitlement) {
            this.defaultEntitlement = defaultEntitlement;
        }

        public void setDefaultBilling(StringConfig defaultBilling) {
            this.defaultBilling = defaultBilling;
        }

        public StringConfig getOneboxCatalog() {
            return oneboxCatalog;
        }

        public StringConfig getOneboxBilling() {
            return oneboxBilling;
        }

        public StringConfig getOneboxDefault() {
            return oneboxDefault;
        }

        public StringConfig getOneboxIdentity() {
            return oneboxIdentity;
        }

        public StringConfig getDefaultDefault() {
            return defaultDefault;
        }

        public StringConfig getDefaultEntitlement() {
            return defaultEntitlement;
        }

        public StringConfig getDefaultBilling() {
            return defaultBilling;
        }
    }

    private File tmpConfigDir;

    @BeforeSuite
    public void setup() {
        tmpConfigDir = createTempDir();
        try {
            File envFilePath = new File(tmpConfigDir, "configuration.properties");
            try (FileWriter writer = new FileWriter(envFilePath)) {
                StringBuffer envFileContent = new StringBuffer();
                envFileContent.append("test.integerConfig=1234\n");
                envFileContent.append("test.stringConfig=testStr\n");
                writer.write(envFileContent.toString());
            }

            try {
                Set<PosixFilePermission> permissions = new HashSet<>();
                permissions.add(PosixFilePermission.OWNER_READ);
                permissions.add(PosixFilePermission.OWNER_WRITE);
                Files.setPosixFilePermissions(envFilePath.toPath(), permissions);
            } catch (UnsupportedOperationException ex) {
                // ignore, windows doesn't support posix
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
/*
            URI uri = getClass().getClassLoader().getResource("conf/onebox/identity/db.properties").toURI();
            File file1 = new File(uri.getPath());
            InputStream in = new FileInputStream(file1);

            OutputStream out = new FileOutputStream(tmpConfigDir + File.separator + "configuration.jar");

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0){
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
*/
            System.setProperty("configDir", tmpConfigDir.getAbsolutePath());
            System.setProperty("environment", "onebox.dc1");
        }
        catch (Exception ex) {
            throw new RuntimeException("Error in setup test configDir.", ex);
        }
    }

    @AfterSuite
    public void teardown() {
        deleteDirectory(tmpConfigDir);
    }

    @Test
    public void testLoadConfig() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");

        TestBean testBean = (TestBean) context.getBean("testBean");
        Assert.assertNotNull(testBean);
    }

    @Test
    public void testConfigurations() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");

        ConfigBag configBag = (ConfigBag) context.getBean("configurationTestBean");
        Assert.assertEquals((int) configBag.getIntegerConfig().get(), 1234);
        Assert.assertEquals(configBag.getStringConfig().get(), "testStr");
        Assert.assertEquals(configBag.getRawStringConfig(), "testStr");

        ConfigService configService = (ConfigService) context.getBean("configService");
        Assert.assertEquals(configService.getConfigValue("test.integerConfig"), "1234");
        Assert.assertEquals(configService.getConfigValue("test.stringConfig"), "testStr");

        ConfigContext configContext = configService.getConfigContext();
        Assert.assertEquals(configContext.getEnvironment(), "onebox.dc1");
        Assert.assertEquals(configContext.getBaseEnvironment(), "onebox");
    }

    // @Test
    // seems Thread.sleep(1000) can't switch thread context on mac, disable this unit test temporarily
    public  void testReloadableConfigurations() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");

        ConfigBag configBag = (ConfigBag) context.getBean("configurationTestBean");
        Assert.assertEquals((int) configBag.getIntegerConfig().get(), 1234);
        Assert.assertEquals(configBag.getStringConfig().get(), "testStr");
        Assert.assertEquals(configBag.getRawStringConfig(), "testStr");

        ConfigService configService = (ConfigService) context.getBean("configService");
        Assert.assertEquals(configService.getConfigValue("test.integerConfig"), "1234");
        Assert.assertEquals(configService.getConfigValue("test.stringConfig"), "testStr");

        File envFilePath = new File(tmpConfigDir, "configuration.properties");
        try (FileWriter writer = new FileWriter(envFilePath)) {
            StringBuffer envFileContent = new StringBuffer();
            envFileContent.append("test.integerConfig=2345\n");
            envFileContent.append("test.stringConfig=changedStr\n");
            writer.write(envFileContent.toString());
        }
        catch (IOException ex) {
            throw new RuntimeException("error when changing configuration.properties content");
        }

        try {
            // switch thread to have config auto reload
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        configService = (ConfigService) context.getBean("configService");
        Assert.assertEquals(configService.getConfigValue("test.integerConfig"), "2345");
        Assert.assertEquals(configService.getConfigValue("test.stringConfig"), "changedStr");
    }

    @Test(enabled = false)
    public void testJarConfigurations() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/*.xml");

        JarConfigBag configBag = (JarConfigBag) context.getBean("jarConfigurationTestBean");

        Assert.assertEquals(configBag.getOneboxCatalog().get(), "onebox.catalog.dc1.value");
        Assert.assertEquals(configBag.getOneboxBilling().get(), "onebox.billing.value");
        Assert.assertEquals(configBag.getOneboxDefault().get(), "onebox.default.value");
        Assert.assertEquals(configBag.getOneboxIdentity().get(), "onebox.identity.defaultvalue");

        Assert.assertEquals(configBag.getDefaultDefault().get(), "default");
        Assert.assertEquals(configBag.getDefaultEntitlement().get(), "entitlementvalue");
        Assert.assertEquals(configBag.getDefaultBilling().get(), "defaultbilling");

        ConfigService configService = (ConfigService) context.getBean("configService");
        ConfigContext configContext = configService.getConfigContext();
        Assert.assertEquals(configContext.getEnvironment(), "conf/_default/onebox");
    }

    //region directory helpers

    private static File createTempDir() {
        try {
            File temp = File.createTempFile("tempConfigDir", Long.toString(System.nanoTime()));
            if (!temp.delete()) {
                throw new RuntimeException("Could not delete temp file: " + temp.getAbsolutePath());
            }
            temp = new File(temp.getAbsolutePath() + ".d");
            if (!temp.mkdir()) {
                throw new RuntimeException("Could not create temp directory: " + temp.getAbsolutePath());
            }

            return temp;
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to create temp file.");
        }
    }

    private static void deleteDirectory(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                deleteDirectory(c);
            }
        }
        if (!f.delete()) {
            logger.warn("Failed to delete file: " + f);
        }
    }

    private static List<Integer> range(int from, int to) {
        List<Integer> list = new ArrayList<>();
        for (int i = from; i <= to; ++i) {
            list.add(i);
        }

        return list;
    }

    @SafeVarargs
    private static List<Integer> concat(List<Integer>... lists) {
        List<Integer> result = new ArrayList<>();
        for (List<Integer> list : lists) {
            result.addAll(list);
        }
        return result;
    }

    private static int[] toArray(List<Integer> list) {
        int[] result = new int[list.size()];
        int resultCount = 0;

        for (Integer i : list) {
            result[resultCount++] = i;
        }
        return result;
    }

    //endregion
}
