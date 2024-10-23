package org.cosmy.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.cosmy.context.AppContext;
import org.cosmy.context.ConnectionsContainer;
import org.cosmy.model.CosmosAccount;

public class ConnectionController {
    @FXML
    private TextField name;

    @FXML
    private TextField accountHost;

    @FXML
    private TextField accountKey;

    @FXML
    public void createConnection(ActionEvent actionEvent) {
        CosmosAccount account = new CosmosAccount(name.getText(), accountHost.getText(), accountKey.getText());
        try {
            ConnectionsContainer.getInstance().addConnection(account);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        AppContext.getInstance().getAccountsPane().acceptNewAccount(account);

        Node node = (Node) actionEvent.getTarget();
        node.getScene().getWindow().hide();
    }


}
