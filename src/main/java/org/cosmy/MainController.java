package org.cosmy;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.cosmy.model.ObservableModelKey;

import java.io.IOException;

import static org.cosmy.model.ObservableModelKey.ACCOUNTS;

public class MainController {

    @FXML
    private TreeView dbAccounts;

    @FXML
    private TabPane tabs;

    @FXML
    private SplitPane splitPane;

    @FXML
    private VBox vBox;

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("layout");
    }

    private IObservableModelRegistry modelRegistry;
    private ItemsHandler itemsHandler = new ItemsHandler();

    @FXML
    private void initialize() {
        //Keep updating the divider for splitpane to adjsut to chnaging window size
        vBox.widthProperty().addListener((observableValue, number, t1) -> {
            splitPane.setDividerPosition(0, 0.25);
        });

        modelRegistry = ObservableModelRegistryImpl.getInstance();
        TreeItem<String> accountRoot = new TreeItem<>("Accounts");
        modelRegistry.register(ACCOUNTS, accountRoot.getChildren());
        dbAccounts.setCellFactory(treeView -> {
            TreeCell<String> cell = new TreeCell<>() {
                @Override
                protected void updateItem(String s, boolean b) {
                    super.updateItem(s, b);
                    if (b) {
                        setText(null);
                    } else {
                        setText(s);
                    }
                }
            };
            cell.setOnMouseClicked(mouseEvent -> {
                Node interactedNode = mouseEvent.getPickResult().getIntersectedNode();
                if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED && mouseEvent.getClickCount() == 2 && interactedNode instanceof Text) {
                    Text node = (Text) interactedNode;
                    String nodeName = node.getText();
                    if (nodeName.equalsIgnoreCase("Items")) {
                        if (node.getParent() instanceof TreeCell) {
                            TreeItem<String> treeItem = ((TreeCell<String>) node.getParent()).getTreeItem();
                            TreeItem<String> parent1 = treeItem.getParent();
                            TreeItem<String> parent2 = parent1.getParent();
                            TreeItem<String> parent3 = parent2.getParent();
                            itemsHandler.handle(parent1.getValue(), parent2.getValue(), parent3.getValue());
                        }
                    }
                }
            });
            return cell;
        });

        dbAccounts.setRoot(accountRoot);
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
}
