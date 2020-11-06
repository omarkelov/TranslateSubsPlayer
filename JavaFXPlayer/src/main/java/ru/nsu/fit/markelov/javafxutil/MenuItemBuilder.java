package ru.nsu.fit.markelov.javafxutil;

import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;
import java.util.List;

public class MenuItemBuilder {

    private String text;
    private ToggleGroup toggleGroup;
    private boolean isSelected = false;
    private Object userData;
    private boolean isMnemonicParsing = false;
    private List<String> styleClassNames = new ArrayList<>();

    public MenuItemBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public MenuItemBuilder setToggleGroup(ToggleGroup toggleGroup) {
        this.toggleGroup = toggleGroup;
        return this;
    }

    public MenuItemBuilder setSelected(boolean selected) {
        isSelected = selected;
        return this;
    }

    public MenuItemBuilder setUserData(Object userData) {
        this.userData = userData;
        return this;
    }

    public MenuItemBuilder setMnemonicParsing(boolean mnemonicParsing) {
        this.isMnemonicParsing = mnemonicParsing;
        return this;
    }

    public MenuItemBuilder addStyleClass(String className) {
        styleClassNames.add(className);
        return this;
    }

    public MenuItem buildStandard() {
        MenuItem menuItem = new MenuItem();

        build(menuItem);

        return menuItem;
    }

    public RadioMenuItem buildRadio() {
        RadioMenuItem radioMenuItem = new RadioMenuItem();

        radioMenuItem.setToggleGroup(toggleGroup);
        radioMenuItem.setSelected(isSelected);

        build(radioMenuItem);

        return radioMenuItem;
    }

    private void build(MenuItem menuItem) {
        if (text != null) {
            menuItem.setText(text);
        }

        menuItem.setUserData(userData);
        menuItem.setMnemonicParsing(isMnemonicParsing);
        menuItem.getStyleClass().addAll(styleClassNames);
    }
}
