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
            def bundles = new ArrayList<BundleInfo>()

            for (URL url : ((URLClassLoader) classLoader).URLs) {
                bundles.addAll(getBundlesByURL(url))
            }

            return bundles
        }

        return []
    }

    private List<BundleInfo> getBundlesByURL(URL url) {
        def bundles = new ArrayList<BundleInfo>()

        def path = url.toString()
        String version = null
        String classPath = null

        if (path.endsWith('.jar')) {
            try {
                def jarFile = new JarFile(url.path)

                if (jarFile.manifest != null) {
                    version = jarFile.manifest.mainAttributes.getValue('Implementation-Version')
                    classPath = jarFile.manifest.mainAttributes.getValue('Class-Path')
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

        bundles.add(new BundleInfo(
                path: path,
                version: version ?: 'unknown',
                gitProperties: new TreeMap(gitProperties)
        ))

        if (classPath != null) {
            def classPathArray = classPath.split(' ')
            for (String item : classPathArray) {
                def newFile = new File(new File(url.path).parentFile, item)
                def newUrl = new URL(url.protocol, url.host, url.port, newFile.path)

                bundles.addAll(getBundlesByURL(newUrl))
            }
        }

        return bundles
    }

    static class BundleInfo {

        String path

        String version

        Map gitProperties
    }
}
