package com.junbo.apphost.core.health

import groovy.transform.CompileStatic

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import java.util.jar.JarFile

/**
 * Created by kgu on 6/2/14.
 */
@Path("/bundles")
@Produces(["application/json"])
@CompileStatic
class BundlesEndpoint {

    @GET
    List<BundleInfo> getBundles() {

        ClassLoader classLoader = Thread.currentThread().contextClassLoader

        if (classLoader instanceof URLClassLoader) {
            def bundles = ((URLClassLoader) classLoader).URLs.collect { URL url ->

                def path = url.toString()
                String version = null

                if (path.endsWith('.jar')) {
                    try {
                        def jarFile = new JarFile(url.path)

                        if (jarFile.manifest != null) {
                            version = jarFile.manifest.mainAttributes.getValue('Implementation-Version')
                        }
                    } catch (IOException ignored) {
                    }
                }

                def gitProperties = new Properties()
                InputStream gitPropertiesStream = null

                try {
                    if (path.endsWith('.jar')) {
                        gitPropertiesStream = new URL("jar:$path!/git.properties").openStream()
                    } else {
                        gitPropertiesStream = new URL("$url/git.properties").openStream()
                    }
                } catch (IOException ignored) {
                }

                if (gitPropertiesStream != null) {
                    gitProperties.load(gitPropertiesStream)
                }

                return new BundleInfo(
                        path: path,
                        version: version ?: 'unknown',
                        gitProperties: new TreeMap(gitProperties)
                )
            }

            return bundles
        }

        return []
    }

    static class BundleInfo {

        String path

        String version

        Map gitProperties
    }
}
