package org.cosmy.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.property.adapter.JavaBeanIntegerProperty;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.cosmy.model.Preferences;
import org.cosmy.state.FilePersistedStateManager;
import org.cosmy.state.IPersistedStateManager;

public class PreferencesController {
    @FXML
    private TextField pageLength;

    private Preferences preferencesBean;
    private JavaBeanIntegerProperty pageLengthBinding;
    private IPersistedStateManager stateManager;

    public PreferencesController() {
        stateManager = FilePersistedStateManager.getInstance();
    }

    @FXML
    private void initialize() {
        preferencesBean = Preferences.getInstance();
        /*try {
            preferencesBean = (Preferences) stateManager.load(Preferences.class);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            preferencesBean = new Preferences();
        }*/

        try {
            pageLengthBinding = JavaBeanIntegerPropertyBuilder.create().bean(preferencesBean).beanClass(Preferences.class).name("pageLength").build();
            Bindings.bindBidirectional(pageLength.textProperty(), pageLengthBinding, new StringConverter<Number>() {
                @Override
                public Number fromString(String arg0) {
                    return Integer.parseInt(arg0);
                }

                @Override
                public String toString(Number arg0) {
                    return String.valueOf(arg0);
                }
            });

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void savePreferences(ActionEvent event) {
        System.out.println(preferencesBean.getPageLength());
        stateManager.persist(preferencesBean);
        Node node = (Node) event.getTarget();
        node.getScene().getWindow().hide();
    }


    public void cancel(ActionEvent event) {
        Node node = (Node) event.getTarget();
        node.getScene().getWindow().hide();
    }
}
