package org.cosmy;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import org.cosmy.model.ObservableModelKey;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    public static Scene scene;
    public static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("main"), 640, 480);
        mainStage = stage;
        stage.setScene(scene);
        stage.setOnCloseRequest(windowEvent -> {
            try {
                ConnectionsContainer.getInstance().persist();
            } catch (Exception e) {
                System.out.println(e);
            }
        });
        stage.show();
        restore(scene);
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("Starting cosmy application..");
    }

    public static void main(String[] args) {
        launch();
    }

    public void restore(Scene scene){
        ObservableList<TreeItem<String>> accounts = (ObservableList<TreeItem<String>>) ObservableModelRegistryImpl.getInstance().lookup(ObservableModelKey.ACCOUNTS);
        ConnectionsContainer.getInstance().iterateAccounts().forEachRemaining(cosmosAccount -> {
            TreeItem<String> item = AccountViewGenerators.generateEmptyCollapsedView(cosmosAccount);
            accounts.add(item);
        });
    }
}