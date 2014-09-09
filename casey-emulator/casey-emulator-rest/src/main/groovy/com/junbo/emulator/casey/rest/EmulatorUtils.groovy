package com.junbo.emulator.casey.rest

import com.junbo.langur.core.context.JunboHttpContext
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component

/**
 * The EmulatorUtils class.
 */
@CompileStatic
@Component('caseyEmulatorUtils')
public class EmulatorUtils {

    void emulateLatency() {
        String latency = JunboHttpContext.getData().getRequestHeaders().getFirst(EmulatorHeaders.X_EMULATE_LATENCY.name())
        if (!StringUtils.isEmpty(latency)) {
            try {
                long val = Long.parseLong(latency)
                Thread.sleep(val)
            } catch (NumberFormatException ex) {
            }
        }
    }
}
