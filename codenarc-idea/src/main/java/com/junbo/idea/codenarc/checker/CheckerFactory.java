package com.junbo.idea.codenarc.checker;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.junbo.idea.codenarc.exception.CodeNarcPluginException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codenarc.ruleregistry.RuleRegistryInitializer;
import org.codenarc.ruleset.PropertiesFileRuleSetConfigurer;
import org.codenarc.ruleset.RuleSet;
import org.codenarc.ruleset.RuleSetUtil;
import com.junbo.idea.codenarc.model.ConfigurationLocation;
import com.junbo.idea.codenarc.util.IDEAUtilities;
import com.junbo.idea.codenarc.util.ModuleClassPathBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A configuration factory and resolver for CodeNarc.
 */
public class CheckerFactory {

    private static final Log LOG = LogFactory.getLog(CheckerFactory.class);

    private final Map<ConfigurationLocation, CachedChecker> cache = new HashMap<ConfigurationLocation, CachedChecker>();
    private List<URL> thirdPartyClassPath = null;

    public RuleSet getChecker(final ConfigurationLocation location, final List<String> thirdPartyJars)
            throws IOException {
        thirdPartyClassPath = new ArrayList<URL>();
        for (String path : thirdPartyJars) {
            thirdPartyClassPath.add(new File(path).toURI().toURL());
        }
        return getChecker(location, null, null);
    }

    /**
     * Get a checker for a given configuration, with the default module classloader.
     *
     * @param location the location of the CodeNarc file.
     * @param module   the current module.
     * @return the checker for the module or null if it cannot be created.
     * @throws IOException         if the CodeNarc file cannot be resolved.
     */
    public RuleSet getChecker(@NotNull final ConfigurationLocation location,
                              @Nullable final Module module)
            throws IOException {
        return getChecker(location, module, null);
    }

    /**
     * Get a checker for a given configuration.
     *
     * @param location    the location of the CodeNarc file.
     * @param module      the current module.
     * @param classLoader class loader for CodeNarc use, or null to create a module class-loader if required.
     * @return the checker for the module or null if it cannot be created.
     * @throws IOException         if the CodeNarc file cannot be resolved.
     */
    public RuleSet getChecker(@NotNull final ConfigurationLocation location,
                              @Nullable final Module module,
                              @Nullable final ClassLoader classLoader)
            throws IOException {
        final CachedChecker cachedChecker = getOrCreateCachedChecker(location, module, classLoader);
        if (cachedChecker != null) {
            return cachedChecker.getChecker();
        }
        return null;
    }

    private CachedChecker getOrCreateCachedChecker(final ConfigurationLocation location,
                                                   final Module module,
                                                   final ClassLoader classLoader)
            throws IOException {
        synchronized (cache) {
            if (cache.containsKey(location)) {
                final CachedChecker cachedChecker = cache.get(location);
                if (cachedChecker != null && cachedChecker.isValid()) {
                    return cachedChecker;
                } else {
                    cache.remove(location);
                }
            }

            final CachedChecker checker = createChecker(location, module, moduleClassLoaderFrom(module, classLoader));
            if (checker != null) {
                cache.put(location, checker);
                return checker;
            }

            return null;
        }
    }

    private ClassLoader moduleClassLoaderFrom(final Module module, final ClassLoader classLoader)
            throws MalformedURLException {
        if (classLoader == null && module != null) {
            return moduleClassPathBuilder(module).build(module);
        } else if (classLoader == null && module == null && thirdPartyClassPath != null) {
            URL[] urls = new URL[thirdPartyClassPath.size()];
            return new URLClassLoader(thirdPartyClassPath.toArray(urls), this.getClass().getClassLoader());
        }
        return classLoader;
    }

    private ModuleClassPathBuilder moduleClassPathBuilder(@NotNull final Module module) {
        return ServiceManager.getService(module.getProject(), ModuleClassPathBuilder.class);
    }

    /**
     * Invalidate any cached checkers.
     */
    public void invalidateCache() {
        synchronized (cache) {
            cache.clear();
        }
    }

    /**
     * Load the CodeNarc configuration in a separate thread.
     *
     * @param location           The location of the CodeNarc configuration file.
     * @param module             the current module.
     * @param contextClassLoader the context class loader, or null for default.
     * @return loaded Configuration object
     */
    private CachedChecker createChecker(final ConfigurationLocation location,
                                        final Module module,
                                        final ClassLoader contextClassLoader) {

        if (LOG.isDebugEnabled()) {
            // debug information

            LOG.debug("Call to create new checker.");

            logClassLoaders(contextClassLoader);
        }

        final CheckerFactoryWorker worker = new CheckerFactoryWorker(
                location, module, contextClassLoader);

        // Begin reading the configuration
        worker.start();

        // Wait for configuration thread to complete
        while (worker.isAlive()) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                // Just be silent for now
            }
        }

        // Did the process of reading the configuration fail?
        if (worker.getResult() instanceof IOException) {
            LOG.info("CodeNarc configuration could not be loaded: " + location.getLocation(),
                    (IOException) worker.getResult());
            return blacklistAndShowMessage(location, module, "codenarc.file-not-found", "Not found: {0}", location.getLocation());

        } else if (worker.getResult() instanceof Throwable) {
            location.blacklist();
            throw new RuntimeException("Could not load configuration", (Throwable) worker.getResult());
        }

        return (CachedChecker) worker.getResult();
    }

    private CachedChecker blacklistAndShowMessage(final ConfigurationLocation location,
                                                  final Module module,
                                                  final String messageKey,
                                                  final String messageFallback,
                                                  final Object... messageArgs) {
        if (!location.isBlacklisted()) {
            location.blacklist();

            final MessageFormat messageFormat = new MessageFormat(IDEAUtilities.getResource(messageKey, messageFallback));
            if (module != null) {
                IDEAUtilities.showError(module.getProject(), messageFormat.format(messageArgs));
            } else {
                throw new CodeNarcPluginException(messageFormat.format(messageArgs));
            }
        }
        return null;
    }

    private void logClassLoaders(final ClassLoader contextClassLoader) {
        // Log classloaders, if known
        if (contextClassLoader != null) {
            ClassLoader currentLoader = contextClassLoader;
            while (currentLoader != null) {
                if (currentLoader instanceof URLClassLoader) {
                    LOG.debug("+ URLClassLoader: "
                            + currentLoader.getClass().getName());
                    final URLClassLoader urlLoader = (URLClassLoader)
                            currentLoader;
                    for (final URL url : urlLoader.getURLs()) {
                        LOG.debug(" + URL: " + url);
                    }
                } else {
                    LOG.debug("+ ClassLoader: " + currentLoader.getClass().getName());
                }

                currentLoader = currentLoader.getParent();
            }
        }
    }

    private class CheckerFactoryWorker extends Thread {

        private final Object[] threadReturn = new Object[1];

        private final ConfigurationLocation location;
        private final Module module;

        public CheckerFactoryWorker(final ConfigurationLocation location,
                                    final Module module,
                                    final ClassLoader contextClassLoader) {
            this.location = location;
            this.module = module;


            if (contextClassLoader != null) {
                setContextClassLoader(contextClassLoader);
            } else {
                final ClassLoader loader = CheckerFactory.this.getClass().getClassLoader();
                setContextClassLoader(loader);
            }
        }

        public Object getResult() {
            return threadReturn[0];
        }

        public void run() {
            try {

                new RuleRegistryInitializer().initializeRuleRegistry();

                RuleSet ruleSet = RuleSetUtil.loadRuleSetFile(location.resolve().toString());

                new PropertiesFileRuleSetConfigurer().configure(ruleSet);

                threadReturn[0] = new CachedChecker(ruleSet);

            } catch (Exception e) {
                threadReturn[0] = e;
            }
        }

    }
}
