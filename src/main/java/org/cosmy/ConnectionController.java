package org.cosmy;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import org.cosmy.model.CosmosAccount;
import org.cosmy.model.ObservableModelKey;

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

        ObservableList<TreeItem<String>> accountsTree = (ObservableList) ObservableModelRegistryImpl.getInstance().lookup(ObservableModelKey.ACCOUNTS);
        accountsTree.add(AccountViewGenerators.generateEmptyCollapsedView(account));

        Node node = (Node) actionEvent.getTarget();
        node.getScene().getWindow().hide();
    }


}
