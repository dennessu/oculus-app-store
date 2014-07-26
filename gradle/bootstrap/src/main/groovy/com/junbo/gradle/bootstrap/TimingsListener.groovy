package com.junbo.gradle.bootstrap

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState
import org.gradle.util.Clock

/**
 * Log timings per task.
 * Created by kgu on 5/31/14.
 */
class TimingsListener implements TaskExecutionListener, BuildListener {
    private Clock clock
    private timings = []

    @Override
    void beforeExecute(Task task) {
        clock = new Clock()
    }

    @Override
    void afterExecute(Task task, TaskState taskState) {
        def ms = clock.timeInMs
        timings.add([ms, task.path])
        task.project.logger.info "${task.path} took ${ms}ms"
    }

    @Override
    void buildFinished(BuildResult result) {
        println "Task timings (Top 10):"

        def count = 0
        for (timing in timings.sort { -it[0] }) {
            printf "%7sms  %s\n", timing

            count++
            if (count >= 10) break;
        }
    }

    @Override
    void buildStarted(Gradle gradle) {}

    @Override
    void projectsEvaluated(Gradle gradle) {}

    @Override
    void projectsLoaded(Gradle gradle) {}

    @Override
    void settingsEvaluated(Settings settings) {}
}
