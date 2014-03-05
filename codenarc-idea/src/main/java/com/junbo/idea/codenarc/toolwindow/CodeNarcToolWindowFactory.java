package com.junbo.idea.codenarc.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.ui.content.Content;
import com.junbo.idea.codenarc.util.IDEAUtilities;

public class CodeNarcToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(final Project project, final ToolWindow toolWindow) {
        final Content toolContent = toolWindow.getContentManager().getFactory().createContent(
                new CodeNarcToolWindowPanel(toolWindow, project),
                IDEAUtilities.getResource("plugin.toolwindow.action", "Scan"),
                false);
        toolWindow.getContentManager().addContent(toolContent);

        toolWindow.setTitle(IDEAUtilities.getResource("plugin.toolwindow.name", "Scan"));
        toolWindow.setType(ToolWindowType.DOCKED, null);
    }

}
