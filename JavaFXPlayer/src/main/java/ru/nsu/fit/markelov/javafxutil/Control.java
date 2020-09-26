package ru.nsu.fit.markelov.javafxutil;

import javafx.beans.property.SimpleStringProperty;

/**
 * Control class is used for holding the information about actions that can be performed by pressing
 * primary or secondary key.
 *
 * @author Oleg Markelov
 */
public class Control {

    private final SimpleStringProperty action = new SimpleStringProperty("");
    private final SimpleStringProperty primaryKey = new SimpleStringProperty("");
    private final SimpleStringProperty secondaryKey = new SimpleStringProperty("");

    /**
     * Creates new Control with empty values.
     */
    public Control() {
        this("", "", "");
    }

    /**
     * Creates new Control with specified values.
     *
     * @param action       action to be performed.
     * @param primaryKey   primary key.
     * @param secondaryKey secondary key.
     */
    public Control(String action, String primaryKey, String secondaryKey) {
        setAction(action);
        setPrimaryKey(primaryKey);
        setSecondaryKey(secondaryKey);
    }

    /**
     * Returns action.
     *
     * @return action.
     */
    public String getAction() {
        return action.get();
    }

    /**
     * Sets action.
     *
     * @param action action.
     */
    public void setAction(String action) {
        this.action.set(action);
    }

    /**
     * Returns primary key.
     *
     * @return primary key.
     */
    public String getPrimaryKey() {
        return primaryKey.get();
    }

    /**
     * Sets primary key.
     *
     * @param primaryKey primary key.
     */
    public void setPrimaryKey(String primaryKey) {
        this.primaryKey.set(primaryKey);
    }

    /**
     * Returns secondary key.
     *
     * @return secondary key.
     */
    public String getSecondaryKey() {
        return secondaryKey.get();
    }

    /**
     * Sets secondary key.
     *
     * @param secondaryKey secondary key.
     */
    public void setSecondaryKey(String secondaryKey) {
        this.secondaryKey.set(secondaryKey);
    }
}
