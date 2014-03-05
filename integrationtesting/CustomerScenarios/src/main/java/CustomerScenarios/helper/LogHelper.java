package CustomerScenarios.helper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Jason on 2/27/14.
 */
public class LogHelper {

    private Log logger;

    public LogHelper(String name) {
        logger = LogFactory.getLog(name);
    }

    public LogHelper(Class clazz) {
        logger = LogFactory.getLog(clazz);
    }

}
