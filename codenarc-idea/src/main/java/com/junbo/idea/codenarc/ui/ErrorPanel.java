package com.junbo.idea.codenarc.ui;

import com.intellij.ui.components.JBScrollPane;
import com.junbo.idea.codenarc.CodeNarcConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ResourceBundle;

public class ErrorPanel extends JPanel {
    private final JTextArea errorField = new JTextArea();

    public ErrorPanel() {
        super(new BorderLayout());

        initialise();

        setError(new RuntimeException());
    }

    private void initialise() {
        setBorder(new EmptyBorder(8, 8, 8, 8));

        final ResourceBundle resources = ResourceBundle.getBundle(CodeNarcConstants.RESOURCE_BUNDLE);
        final JLabel infoLabel = new JLabel(resources.getString("config.file.error.caption"));
        infoLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        add(infoLabel, BorderLayout.NORTH);

        errorField.setEditable(false);
        errorField.setTabSize(2);

        final JScrollPane errorScrollPane = new JBScrollPane(errorField);
        add(errorScrollPane, BorderLayout.CENTER);
    }

    public void setError(final Throwable t) {
        final StringWriter errorWriter = new StringWriter();
        causeOf(t).printStackTrace(new PrintWriter(errorWriter));

        errorField.setText(errorWriter.getBuffer().toString());
        errorField.setCaretPosition(0);
        invalidate();
    }

    private Throwable causeOf(final Throwable t) {
        if (t.getCause() != null && t.getCause() != t) {
            return causeOf(t.getCause());
        }
        return t;
    }
}
