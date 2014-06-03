package com.junbo.gradle.bootstrap

import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.StoredConfig
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.text.SimpleDateFormat

/**
 * Created by kgu on 5/31/14.
 */
class GetGitPropertiesTask extends DefaultTask {

    @TaskAction
    void getGitProperties() {
        def repoDir = findRepoDir()

        if (repoDir == null) {
            project.logger.warn("Could not locate a '${Constants.DOT_GIT}' directory. Task skipped.")
            return
        }

        FileRepositoryBuilder builder = new FileRepositoryBuilder()

        Repository repo = builder.setGitDir(repoDir).readEnvironment().build()

        def headRef = repo.getRef(Constants.HEAD)

        RevWalk revWalk = new RevWalk(repo)
        RevCommit commit = revWalk.parseCommit(headRef.objectId)
        revWalk.markStart(commit)

        try {
            StoredConfig conf = repo.config
            int abbrev = conf.getInt('core', 'abbrev', 7);

            def commitTime = new Date(commit.commitTime * 1000L)
            def ident = commit.authorIdent

            Properties properties = new Properties()

            def sdf = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss.SSS Z')

            properties.put('build.time', sdf.format(new Date()))
            properties.put('build.user.name', conf.getString('user', null, 'name') ?: '')
            properties.put('build.user.email', conf.getString('user', null, 'email') ?: '')

            properties.put('branch', repo.branch)
            properties.put('commit.id', commit.name)
            properties.put('commit.id.abbrev', commit.name.substring(0, abbrev))
            properties.put('commit.time', sdf.format(commitTime))
            properties.put('commit.user.name', ident.name)
            properties.put('commit.user.email', ident.emailAddress)
            properties.put('commit.message', commit.fullMessage)
            properties.put('commit.message.short', commit.shortMessage)

            def extension = project.getExtensions().getByType(GitPropertiesExtension)
            extension.gitProperties = properties
        } finally {
            revWalk.dispose()
        }
    }

    private File findRepoDir() {
        File baseDir = project.projectDir
        while (baseDir != null) {
            def repoDir = new File(baseDir, Constants.DOT_GIT)

            project.logger.info("Searching '$baseDir' for a '${Constants.DOT_GIT}' directory.")
            if (repoDir.exists()) return repoDir

            baseDir = baseDir.parentFile
        }

        project.logger.warn("Could not locate a '${Constants.DOT_GIT}' directory.")
        return null
    }
}
