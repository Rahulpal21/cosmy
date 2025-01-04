package org.cosmy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.cosmy.context.ConnectionsContainer;

import java.io.IOException;
import java.util.Objects;

/**
 * JavaFX App
 */
/// @author Rahul Pal
public class App extends Application {

    public static Scene scene;
    public static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("main2"));
        scene.getStylesheets().add("json-format.css");
        mainStage = stage;
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/icons/telescope.png"))));
        stage.setIconified(true);
        stage.setTitle("Cosmy");

        stage.setOnCloseRequest(windowEvent -> {
            try {
                ConnectionsContainer.getInstance().persist();
            } catch (Exception e) {
                System.out.println(e);
            }
        });
        stage.show();
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

}