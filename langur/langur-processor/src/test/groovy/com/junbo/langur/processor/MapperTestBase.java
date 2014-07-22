/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Java cod for MapperTestBase.
 */
public abstract class MapperTestBase {

    private String sourceDir;
    private String classOutputDir;
    private String sourceOutputDir;

    private List<File> classPath;

    @BeforeClass
    public void setup() throws Exception {
        String basePath = getBasePath();

        sourceDir = basePath + "/src/test/groovy";

        classOutputDir = basePath + "/target/classes";
        sourceOutputDir = basePath + "/target/generated";

        classPath = new ArrayList<>();
        String classpath = System.getProperty("java.class.path");
        for (String entry : classpath.split(File.pathSeparator)) {
            classPath.add(new File(entry));
        }

        createOutputDirs();

        Thread.currentThread().setContextClassLoader(
                new URLClassLoader(
                        new URL[]{new File(classOutputDir).toURI().toURL()},
                        Thread.currentThread().getContextClassLoader()
                )
        );
    }

    @BeforeMethod
    public void generateMapperImplementation(Method testMethod) {
        List<File> sourceFiles = getSourceFiles(getTestClasses(testMethod));

        boolean compilationSuccessful = compile(sourceFiles);
        Assert.assertTrue(compilationSuccessful, "compilation failed");

    }

    private void createOutputDirs() {
        File directory = new File(classOutputDir);
        deleteDirectory(directory);
        directory.mkdirs();

        directory = new File(sourceOutputDir);
        deleteDirectory(directory);
        directory.mkdirs();
    }

    private void deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        path.delete();
    }


    private List<File> getSourceFiles(List<Class<?>> classes) {
        List<File> sourceFiles = new ArrayList<File>(classes.size());

        for (Class<?> clazz : classes) {
            sourceFiles.add(new File(sourceDir + File.separator +
                    clazz.getName().replace(".", File.separator) +
                    ".java"));
        }

        return sourceFiles;
    }


    private List<Class<?>> getTestClasses(Method testMethod) {
        WithClasses withClasses = testMethod.getAnnotation(WithClasses.class);

        if (withClasses == null) {
            withClasses = this.getClass().getAnnotation(WithClasses.class);
        }

        if (withClasses == null || withClasses.value().length == 0) {
            throw new IllegalStateException(
                    "The classes to be compiled during the test must be specified via @WithClasses."
            );
        }

        return Arrays.asList(withClasses.value());
    }


    private boolean compile(Iterable<File> sourceFiles) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFiles);

        try {
            fileManager.setLocation(StandardLocation.CLASS_PATH, classPath);
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File(classOutputDir)));
            fileManager.setLocation(StandardLocation.SOURCE_OUTPUT, Arrays.asList(new File(sourceOutputDir)));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                fileManager,
                null,
                null,
                null,
                compilationUnits
        );

        task.setProcessors(Arrays.asList(
                new RestAdapterProcessor(),
                new ClientProxyProcessor()
        ));

        return task.call();
    }


    private String getBasePath() {
        try {
            return new File(".").getCanonicalPath();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
