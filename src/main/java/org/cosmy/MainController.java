package org.cosmy;

import javafx.event.ActionEvent;
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
import org.kordamp.ikonli.javafx.FontIcon;

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

    private IObservableModelRegistry modelRegistry;
    private ItemsHandler itemsHandler = new ItemsHandler();

    @FXML
    private void initialize() {
        //Keep updating the divider for splitpane to adjsut to chnaging window size
        vBox.widthProperty().addListener((observableValue, number, t1) -> {
            splitPane.setDividerPosition(0, 0.25);
        });

        modelRegistry = ObservableModelRegistryImpl.getInstance();
        FontIcon icon = new FontIcon("mdomz-supervisor_account");

        TreeItem<String> accountRoot = new TreeItem<>("Accounts");
        accountRoot.setGraphic(icon);
        accountRoot.getGraphic().setVisible(true);

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
            cell.setOnKeyReleased(keyEvent -> {
                //TODO investigate enter event on items cell
                System.out.println(keyEvent);
            });
            cell.setOnKeyPressed(keyEvent -> {
                //TODO investigate enter event on items cell
                System.out.println(keyEvent);
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

    public void preferencesDialog(ActionEvent event) throws IOException {
        Stage dialog = new Stage();
        dialog.setScene(new Scene(App.loadFXML("preferencesDialog")));
        dialog.initOwner(App.mainStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }
}
