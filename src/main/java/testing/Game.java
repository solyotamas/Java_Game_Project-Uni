package testing;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Game extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Game.class.getResource("/hihihaha/game.fxml"));
        Parent root;
        if (loader.getLocation() == null) {
            throw new IOException("FXML file not found: /hihihaha/game.fxml");
        }
        else{
            root = loader.load();
        }


        //stage, scene coming from fxml
        primaryStage.setTitle("Safari");
        primaryStage.setScene(new Scene(root));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();

        System.out.println(Tile.class.getResource("/images/road.png"));
        System.out.println(getClass().getResource("/hihihaha/game.fxml"));
    }
}
