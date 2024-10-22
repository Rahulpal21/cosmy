package org.cosmy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.cosmy.model.ObservableModelKey;
import org.cosmy.view.TreeViewPane;

import java.io.IOException;

public class MainController {

    @FXML
    public VBox accountPaneVBox;

    @FXML
    private TabPane tabs;

    @FXML
    private SplitPane splitPane;

    @FXML
    private VBox vBox;

    private IObservableModelRegistry modelRegistry;

    @FXML
    private void initialize() {
        //Keep updating the divider for splitpane to adjsut to chnaging window size
        vBox.widthProperty().addListener((observableValue, number, t1) -> {
            splitPane.setDividerPosition(0, 0.25);
        });

        modelRegistry = ObservableModelRegistryImpl.getInstance();

        TreeViewPane accountsPane = new TreeViewPane();
        accountsPane.initialize();
        accountPaneVBox.getChildren().add(accountsPane.getTreeView());
        modelRegistry.register(ObservableModelKey.TABS, tabs.getTabs());
    }

    @FXML
    public void createConnectioDialog(ActionEvent actionEvent) throws IOException {
        Stage dialog = new Stage();
        dialog.setScene(new Scene(App.loadFXML("createConnectionDialog")));
        dialog.initOwner(App.mainStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }

    public void bulkImportDialog(ActionEvent actionEvent) throws IOException {
        Stage dialog = new Stage();
        dialog.setScene(new Scene(App.loadFXML("bulkImportDialog")));
        dialog.initOwner(App.mainStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }

    public void preferencesDialog(ActionEvent event) throws IOException {
        Stage dialog = new Stage();
        dialog.setScene(new Scene(App.loadFXML("preferencesDialog")));
        dialog.initOwner(App.mainStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }
}
