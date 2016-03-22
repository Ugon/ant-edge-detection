import controllers.ContentController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Wojciech Pachuta.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RootContent.fxml"));
        Parent root = fxmlLoader.load();
        ContentController contentController = fxmlLoader.getController();

        String imageName = getParameters().getRaw().get(0);
        contentController.begin(imageName);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.show();
    }

    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("Usage: App <IMAGE FULL PATH>");
        }
        else {
            launch(args);
        }
    }
}
