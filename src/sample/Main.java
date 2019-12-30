package sample;

import javafx.application.Application;;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        FlowPane pane = new FlowPane();
        pane.setOrientation(Orientation.VERTICAL);
        pane.setAlignment(Pos.CENTER);
        new StateManager(pane);
        primaryStage.setScene(new Scene(pane, 700, 700));
//        primaryStage.setFullScreen(true);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Play 2048!");
        primaryStage.show();


    }


    public static void main(String[] args) {
        launch(args);
    }
}
