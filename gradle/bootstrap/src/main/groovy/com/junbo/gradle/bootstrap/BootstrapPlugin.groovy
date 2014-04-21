package com.junbo.gradle.bootstrap
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CodeNarc
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
                    configProperties = ["headerFile" : "$buildDir/config/checkstyle/javaHeader.txt"]
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

                jar {
                    manifest {
                        attributes (
                                'Implementation-Title': "${subProject.name}",
                                'Implementation-Version': "${subProject.version}"
                        )
                    }
                }
            }

            plugins.withType(GroovyPlugin) {

                dependencies {
                    compile libraries.groovy
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

                subProject.apply plugin: 'codenarc'

                codenarc {
                    configFile = file("$buildDir/config/codenarc/codenarc.groovy")
                    toolVersion = '0.20'
                }

                codenarcTest {
                    exclude "**/**"
                }

                codenarcMain {
                    exclude "**/**/package-info.java"
                }

                task('unzipCodenarcConfigFile') {
                    outputs.upToDateWhen { codenarc.configFile.exists() }

                    doLast {
                        codenarc.configFile.parentFile.mkdirs()

                        def path = Paths.get(codenarc.configFile.canonicalPath)
                        def resource = BootstrapPlugin.getResourceAsStream('/config/codenarc/codenarc.groovy')
                        Files.copy(resource, path, StandardCopyOption.REPLACE_EXISTING)
                    }
                }

                tasks.withType(CodeNarc) {
                    it.dependsOn 'unzipCodenarcConfigFile'
                }
            }
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
