package com.junbo.idea.codenarc.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.junbo.idea.codenarc.checker.CheckerFactory;

/**
 * Clear the Checker cache, forcing a reload of rules files.
 */
public class ClearCheckerCache extends BaseAction {

    @Override
    public void actionPerformed(final AnActionEvent event) {
        ServiceManager.getService(CheckerFactory.class).invalidateCache();
    }
}
