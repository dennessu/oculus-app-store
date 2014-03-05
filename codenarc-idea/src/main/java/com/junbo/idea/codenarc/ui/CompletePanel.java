package com.junbo.idea.codenarc.ui;

import com.junbo.idea.codenarc.CodeNarcConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ResourceBundle;

public class CompletePanel extends JPanel {

    public CompletePanel() {
        super(new GridBagLayout());

        initialise();
    }

    private void initialise() {
        final ResourceBundle resources = ResourceBundle.getBundle(CodeNarcConstants.RESOURCE_BUNDLE);
        final JLabel infoLabel = new JLabel(resources.getString("config.file.complete.text"));

        setBorder(new EmptyBorder(4, 4, 4, 4));

        add(infoLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE, new Insets(8, 8, 8, 8), 0, 0));
    }
}
