package org.cosmy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import static org.cosmy.model.ObservableModelKey.ACCOUNTS;

public class MainController {

    @FXML
    private TreeView dbAccounts;

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("layout");
    }

    private IObservableModelRegistry modelRegistry;

    @FXML
    private void initialize(){
        modelRegistry = ObservableModelRegistryImpl.getInstance();
        System.out.println("initializing controller..");
        TreeItem<String> accountRoot = new TreeItem<>("Accounts");
        modelRegistry.register(ACCOUNTS, accountRoot.getChildren());
        dbAccounts.setRoot(accountRoot);
    }

    @FXML
    public void createConnectioDialog(ActionEvent actionEvent) throws IOException {
        System.out.println("crete new connection ..");
        Stage dialog = new Stage();
        dialog.setScene(new Scene(App.loadFXML("createConnectionDialog")));
        dialog.initOwner(App.mainStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }
}
