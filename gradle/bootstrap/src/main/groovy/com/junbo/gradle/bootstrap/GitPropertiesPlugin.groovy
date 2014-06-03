package com.junbo.gradle.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSetContainer

/**
 * Created by kgu on 5/31/14.
 */
class GitPropertiesPlugin implements Plugin<Project> {

    @Override
    void apply(Project rootProject) {

        rootProject.extensions.create('gitProperties', GitPropertiesExtension)

        def getGitPropertiesTask = rootProject.task('getGitProperties', type: GetGitPropertiesTask)
        rootProject.subprojects { Project subProject ->

            subProject.plugins.withType(JavaPlugin) {
                def writeGitPropertiesTask = subProject.task('writeGitProperties', type: WriteGitPropertiesTask)
                writeGitPropertiesTask.dependsOn(getGitPropertiesTask)

                subProject.tasks['processResources'].dependsOn(writeGitPropertiesTask)
            }
        }
    }
}
