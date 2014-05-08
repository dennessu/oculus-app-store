package com.junbo.gradle.bootstrap

import org.gradle.GradleLauncher
import org.gradle.api.Project
import org.gradle.api.internal.TaskInternal
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

        def subproj1 = ProjectBuilder.builder().withParent(project).build()
        project.subprojects.add(subproj1)

        def subproj2 = ProjectBuilder.builder().withParent(project).build()
        project.subprojects.add(subproj2)

        project.apply plugin: 'bootstrap'

        subproj1.apply plugin: 'java'
        subproj2.apply plugin: 'groovy'
    }
}
