package com.junbo.idea.codenarc.ui;

import com.junbo.idea.codenarc.CodeNarcConstants;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * A table model for editing CodeNarc properties.
 */
public class PropertiesTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -5666606037841678795L;

    protected static final int COLUMN_NAME = 0;
    protected static final int COLUMN_VALUE = 1;
    private static final int NUMBER_OF_COLUMNS = 2;

    private final List<String> orderedNames = new ArrayList<String>();
    private final Map<String, String> properties
            = new HashMap<String, String>();

    /**
     * Create a new empty properties table model.
     */
    public PropertiesTableModel() {
    }

    /**
     * Create a new properties table model.
     *
     * @param properties the map of property names to values.
     */
    public PropertiesTableModel(
            final Map<String, String> properties) {
        setProperties(properties);
    }

    /**
     * Set the current properties in the table.
     *
     * @param newProperties the map of property names to values.
     */
    public void setProperties(final Map<String, String> newProperties) {
        orderedNames.clear();
        properties.clear();

        if (newProperties != null) {
            properties.putAll(newProperties);
            orderedNames.addAll(newProperties.keySet());
            Collections.sort(orderedNames);
        }

        fireTableDataChanged();
    }

    /**
     * Clear all data from this table model.
     */
    public void clear() {
        orderedNames.clear();
        properties.clear();

        fireTableDataChanged();
    }

    /**
     * Get the properties from the table.
     *
     * @return the map of properties to values.
     */
    public Map<String, String> getProperties() {
        return new HashMap<String, String>(properties);
    }

    public int getColumnCount() {
        return NUMBER_OF_COLUMNS;
    }

    public Class<?> getColumnClass(final int columnIndex) {
        return String.class;
    }

    public String getColumnName(final int column) {
        final ResourceBundle resources = ResourceBundle.getBundle(
                CodeNarcConstants.RESOURCE_BUNDLE);

        return resources.getString("config.file.properties.table." + column);
    }

    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return columnIndex == COLUMN_VALUE;
    }

    public void setValueAt(final Object aValue, final int rowIndex,
                           final int columnIndex) {
        switch (columnIndex) {
            case COLUMN_VALUE:
                final String propertyName = orderedNames.get(rowIndex);
                properties.put(propertyName, aValue != null
                        ? aValue.toString() : null);
                break;

            default:
                throw new IllegalArgumentException("Invalid column: "
                        + columnIndex);
        }
    }

    public int getRowCount() {
        return orderedNames.size();
    }

    public Object getValueAt(final int rowIndex, final int columnIndex) {
        switch (columnIndex) {
            case COLUMN_NAME:
                return orderedNames.get(rowIndex);
            case COLUMN_VALUE:
                return properties.get(orderedNames.get(rowIndex));

            default:
                throw new IllegalArgumentException("Invalid column: "
                        + columnIndex);
        }
    }
}
