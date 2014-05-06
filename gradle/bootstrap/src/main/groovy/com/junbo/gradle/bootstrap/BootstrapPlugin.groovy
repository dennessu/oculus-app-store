package com.junbo.gradle.bootstrap

import org.apache.tools.ant.util.TeeOutputStream
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Upload
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.wrapper.Wrapper

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
/**
 * Created by kg on 1/21/14.
 */
class BootstrapPlugin implements Plugin<Project> {

    @Override
    void apply(Project rootProject) {
        rootProject.task('createWrapper', type: Wrapper) {
            gradleVersion = '1.11'
        }

        rootProject.apply plugin: 'idea'

        // unzip libraries.gradle on rootProject (identity, billing, etc.)
        rootProject.file("$rootProject.projectDir/build").mkdirs()
        def libPath = Paths.get("$rootProject.projectDir/build/libraries.gradle")
        def libResource = BootstrapPlugin.getResourceAsStream("/config/gradle/libraries.gradle")
        Files.copy(libResource, libPath, StandardCopyOption.REPLACE_EXISTING)

        rootProject.apply from: "$rootProject.projectDir/build/libraries.gradle"
        rootProject.idea {
            project {
                jdkName = 1.7
                languageLevel = 1.7
            }
        }

        rootProject.defaultTasks 'clean', 'install'

        rootProject.subprojects {
            def subProject = it

            group = "${project_group}"
            version = property("${project.name}-version")

            apply plugin: 'idea'

            idea {
                module {
                    excludeDirs -= file("$buildDir")
                    sourceDirs += file("$buildDir/generated-src")

                    sourceDirs += file('src/main/java')
                    sourceDirs += file('src/main/groovy')
                    sourceDirs += file('src/main/resources')
                    sourceDirs += file('src/main/webapp')
                }
            }

            apply plugin: 'maven'

            repositories {
                mavenLocal()
                mavenCentral()
                jcenter()

                maven {
                    credentials {
                        username "${artifactory_user}"
                        password "${artifactory_password}"
                    }

                    url "${artifactory_contextUrl}/repo"
                }

            }

            uploadArchives {
                repositories {
                    mavenDeployer {
                        repository(url: "${artifactory_contextUrl}/libs-release-local") {
                            authentication(userName: "${artifactory_user}", password: "${artifactory_password}")
                        }

                        snapshotRepository(url: "${artifactory_contextUrl}/libs-snapshot-local") {
                            authentication(userName: "${artifactory_user}", password: "${artifactory_password}")
                        }
                    }
                }
            }

            configurations {
                processor
            }

            ext {
                processorClass = null
            }

            plugins.withType(JavaPlugin) {
                sourceCompatibility = 1.7
                targetCompatibility = 1.7

                dependencies {
                    testCompile libraries.testng
                }

                compileJava.doFirst {
                    def generated = file "$buildDir/generated-src"
                    generated.mkdirs()

                    compileJava.options.compilerArgs.addAll '-s', generated.path

                    if (!configurations.processor.empty) {
                        compileJava.options.compilerArgs.addAll '-processorpath', configurations.processor.asPath
                    }

                    if (processorClass) {
                        compileJava.options.compilerArgs.addAll '-processor', processorClass
                    }
                }

                task('sourcesJar', type: Jar) {
                    classifier = 'sources'
                    from sourceSets.main.allSource, "$buildDir/generated-src"
                }

                artifacts {
                    archives sourcesJar
                }

                test {
                    useTestNG()
                }

                subProject.apply plugin: 'checkstyle'

                checkstyle {
                    configFile = file("$buildDir/config/checkstyle/checkstyle.xml")
                    configProperties = ["headerFile": "$buildDir/config/checkstyle/javaHeader.txt"]
                }

                checkstyleTest {
                    exclude "**/**"
                }

                checkstyleMain {
                    exclude "**/**/package-info.java"
                }

                task('unzipCheckstyleConfigFile') {
                    outputs.upToDateWhen { checkstyle.configFile.exists() }

                    doLast {
                        checkstyle.configFile.parentFile.mkdirs()

                        def path = Paths.get(checkstyle.configFile.canonicalPath)
                        def resource = BootstrapPlugin.getResourceAsStream("/config/checkstyle/checkstyle.xml")
                        Files.copy(resource, path, StandardCopyOption.REPLACE_EXISTING)

                        path = Paths.get("$buildDir/config/checkstyle/javaHeader.txt")
                        resource = BootstrapPlugin.getResourceAsStream("/config/checkstyle/javaHeader.txt")
                        Files.copy(resource, path, StandardCopyOption.REPLACE_EXISTING)
                    }
                }

                tasks.withType(Checkstyle) {
                    it.dependsOn 'unzipCheckstyleConfigFile'
                }

                tasks.withType(Upload) {
                    it.dependsOn 'build'
                }

                jar {
                    manifest {
                        attributes(
                                'Implementation-Title': "${subProject.name}",
                                'Implementation-Version': "${subProject.version}"
                        )
                    }
                }
            }

            plugins.withType(GroovyPlugin) {
                configurations {
                    codenarc
                }

                dependencies {
                    compile libraries.groovy

                    codenarc "org.codenarc:CodeNarc:0.21-J-SNAPSHOT"
                    codenarc "com.junbo.gradle:bootstrap:${junboVersion}"
                    codenarc libraries.log4j
                    codenarc libraries.groovy
                }

                compileGroovy.doFirst {
                    def generated = file "$buildDir/generated-src"
                    generated.mkdirs()

                    compileGroovy.options.compilerArgs.addAll '-s', generated.path

                    if (!configurations.processor.empty) {
                        compileGroovy.options.compilerArgs.addAll '-processorpath', configurations.processor.asPath
                    }

                    if (processorClass) {
                        compileGroovy.options.compilerArgs.addAll '-processor', processorClass
                    }
                }

                // hardcoded source folder because pattern matching is not working in windows
                def codenarcSourceFolder = file("$projectDir/src/main/groovy")
                if (codenarcSourceFolder.exists()) {
                    task('codenarc', dependsOn: 'jar', type: JavaExec) {
                        def reportFile = "$buildDir/CodeNarcReport.html"
                        def outputStream = new ByteArrayOutputStream()

                        classpath configurations.codenarc + tasks['jar'].outputs.files + configurations.compile
                        main = 'org.codenarc.CodeNarc'
                        standardOutput = new TeeOutputStream(standardOutput, outputStream)

                        // only include main. test is not included
                        args = ["-basedir=$codenarcSourceFolder",
                                // disable, pattern matching is not working in windows somehow
                                // "-includes=" + sourceSets.main.groovy.srcDirs.collect { "$it\\**.groovy" }.join(","),
                                "-rulesetfiles=config/codenarc/codenarc.groovy",
                                "-report=html:$reportFile"
                        ].toList()

                        doLast {
                            def outputAsString = outputStream.toString()
                            def compileErrorMatcher = outputAsString =~ /Compilation failed for /
                            def resultMatcher = outputAsString =~ /CodeNarc completed: \(p1=(\d+); p2=(\d+); p3=(\d+)\)/

                            def p1Count = resultMatcher[0][1].toInteger()
                            def p2Count = resultMatcher[0][2].toInteger()
                            def p3Count = resultMatcher[0][3].toInteger()

                            println "CodeNarc report is available at: $reportFile"

                            println args.join(" ")

                            if (compileErrorMatcher.find()) {
                                throw new GradleException("Compilation failures in CodeNarc run.")
                            }

                            if (p1Count + p2Count + p3Count > 0) {
                                throw new GradleException("CodeNarc found violations.")
                            }
                        }
                    }

                    tasks.build.dependsOn 'codenarc'
                }
            }

            plugins.withType(ApplicationPlugin) {
                jar {
                    doFirst {
                        manifest {
                            attributes "Class-Path": configurations.runtime.files*.name.join(" ")
                        }
                    }
                }

                startScripts {
                    // clear up the classpath because the launcher jar has it.
                    classpath = files(jar.archivePath)
                }
            }

            defaultTasks 'clean', 'install'
        }

        rootProject.apply plugin: "sonar-runner"
        rootProject.sonarRunner {
            sonarProperties {
                property "sonar.host.url", "http://sonar.wansan.internal:9000"
                property "sonar.jdbc.url", "jdbc:mysql://sonar.wansan.internal:3306/sonar"
                property "sonar.jdbc.driverClassName", "com.mysql.jdbc.Driver"
                property "sonar.jdbc.username", "root"
                property "sonar.jdbc.password", ""
            }
        }

        rootProject.task('cleanupReport')  {
            rootProject.delete "build/xml-report"
        }

        rootProject.task('collectReport')  {
            doLast{
                rootProject.copy {
                    from rootProject.subprojects*.testResultsDir
                    into 'build/xml-report'
                    include '**/*.xml'
                }
            }
        }
    }
}
