package com.junbo.apphost.core.logging

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.StopWatch

import static com.junbo.apphost.core.logging.AnsiElement.*


/**
 * Created by kg on 4/21/2014.
 */
@CompileStatic
class Banner {

    private static final String BANNER = """
 \$\$\$\$\$\$\\  \$\$\\ \$\$\\ \$\$\\        \$\$\$\$\$\$\\  \$\$\\                                     \$\$\\
\$\$  __\$\$\\ \\__|\$\$ |\$\$ |      \$\$  __\$\$\\ \$\$ |                                    \$\$ |
\$\$ /  \\__|\$\$\\ \$\$ |\$\$ |  \$\$\\ \$\$ /  \\__|\$\$ | \$\$\$\$\$\$\\  \$\$\\   \$\$\\ \$\$\$\$\$\$\$\\   \$\$\$\$\$\$\$ |
\\\$\$\$\$\$\$\\  \$\$ |\$\$ |\$\$ | \$\$  |\$\$ |      \$\$ |\$\$  __\$\$\\ \$\$ |  \$\$ |\$\$  __\$\$\\ \$\$  __\$\$ |
 \\____\$\$\\ \$\$ |\$\$ |\$\$\$\$\$\$  / \$\$ |      \$\$ |\$\$ /  \$\$ |\$\$ |  \$\$ |\$\$ |  \$\$ |\$\$ /  \$\$ |
\$\$\\   \$\$ |\$\$ |\$\$ |\$\$  _\$\$<  \$\$ |  \$\$\\ \$\$ |\$\$ |  \$\$ |\$\$ |  \$\$ |\$\$ |  \$\$ |\$\$ |  \$\$ |
\\\$\$\$\$\$\$  |\$\$ |\$\$ |\$\$ | \\\$\$\\ \\\$\$\$\$\$\$  |\$\$ |\\\$\$\$\$\$\$  |\\\$\$\$\$\$\$  |\$\$ |  \$\$ |\\\$\$\$\$\$\$\$ |
 \\______/ \\__|\\__|\\__|  \\__| \\______/ \\__| \\______/  \\______/ \\__|  \\__| \\_______|
"""

    private static final String HEADER = " :: Junbo Application Host :: ";

    public static void write(PrintStream printStream) {

        printStream.println(BANNER)

        int width = 0
        BANNER.eachLine { String line -> width = Math.max(width, line.length()) }

        String version = Banner.class.package.implementationVersion

        version = (version == null ? "(vUndefined)" : " (v" + version + ")")

        String padding = "";
        while (padding.length() < width - (version.length() + HEADER.length())) {
            padding += " ";
        }

        printStream.println(AnsiOutput.toString(GREEN, HEADER, DEFAULT, padding, YELLOW, version))
        printStream.println()
    }

    private Banner() {
    }

}
