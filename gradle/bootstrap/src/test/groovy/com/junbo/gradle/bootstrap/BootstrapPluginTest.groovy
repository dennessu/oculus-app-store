package com.junbo.gradle.bootstrap

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.testng.annotations.Test
/**
 * Created by kg on 1/21/14.
 */
class BootstrapPluginTest {

    @Test
    public void testBootstrap() {
        Project project = ProjectBuilder.builder().build()

        project.ext.set("project_group", "com.junbo.sample")
        project.ext.set("test-version", "0.0.1-SNAPSHOT")

        project.ext.set("artifactory_contextUrl", "https://arti.silkcloud.info:8444/artifactory")
        project.ext.set("artifactory_user", "username")
        project.ext.set("artifactory_password", "password")

        def subproj1 = ProjectBuilder.builder().withParent(project).build()
        project.subprojects.add(subproj1)

        def subproj2 = ProjectBuilder.builder().withParent(project).build()
        project.subprojects.add(subproj2)

        project.apply plugin: 'bootstrap'

        subproj1.apply plugin: 'java'
        subproj2.apply plugin: 'groovy'
    }
}
