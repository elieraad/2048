package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.*;

public class Menu extends VBox {

    private Text title;
    private Font font;
    public static boolean soundsOn = true;
    public static boolean undoOn = true;

    private Label classicPlayButton;
    private Label agentPlayButton;

    private Label soundsButton;
    private Label undoButton;

    public Label getClassicPlayButton() {
        return classicPlayButton;
    }

    public Label getAgentPlayButton() {
        return agentPlayButton;
    }

    public Menu() {
        title = new Text("2048");
        font = Font.font("Helvetica", FontWeight.BOLD, 48);
        title.setFont(font);
        title.setFill(Color.rgb(119, 110, 101));
        setAlignment(Pos.CENTER);
        setPadding(new Insets(40));
        setSpacing(40);
        getChildren().add(title);
        VBox vBox1 = new VBox(8);
        VBox vBox2 = new VBox(8);

        classicPlayButton = createButton("Classic play", Color.rgb(237, 194, 46));
        agentPlayButton = createButton("Agent Play", Color.rgb(246, 94, 59));
        vBox1.getChildren().addAll(classicPlayButton, agentPlayButton);

        Label statisticsButton = createButton("Statistics", Color.rgb(24, 89, 163));
        soundsButton = createButton("Sounds", Color.rgb(24, 89, 163));
        undoButton = createButton("Undo", Color.rgb(24, 89, 163));

        statisticsButton.setOnMouseClicked(e -> {
            int highScore = 0;
            int totalScore = 0;
            int totalGames = 0;

            try {
                BufferedReader reader = new BufferedReader(new FileReader("stats.txt"));
                highScore = Integer.valueOf(reader.readLine());
                totalScore = Integer.valueOf(reader.readLine());
                totalGames = Integer.valueOf(reader.readLine());
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Statistics");
                alert.setHeaderText(null);
                alert.setContentText("High Score: " + highScore
                        + "\nTotal Score: " + totalScore
                        + "\nGames Played: " + totalGames);
                alert.showAndWait();
            }
        });

        soundsButton.setOnMouseClicked(e -> {
            soundsOn = !soundsOn;
            updateSettings();
        });

        undoButton.setOnMouseClicked(e -> {
            undoOn = !undoOn;
            updateSettings();
        });

        readSettings();

        vBox2.getChildren().addAll(statisticsButton, soundsButton, undoButton);

        getChildren().addAll(vBox1, vBox2);

    }

    private void readSettings() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("settings.txt"));
            soundsOn = Boolean.valueOf(reader.readLine());
            undoOn = Boolean.valueOf(reader.readLine());
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            updateSettings();
        }
    }

    private void updateSettings() {
        if (soundsOn)
            soundsButton.setText("Sounds ON");
        else soundsButton.setText("Sounds OFF");

        if (undoOn)
            undoButton.setText("Undo ON");
        else undoButton.setText("Undo OFF");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("settings.txt"));
            writer.write(String.format("%b%n%b", soundsOn, undoOn));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public Label createButton(String label, Color color) {
        Label button = new Label();
        int width = 256;
        int height = 40;

        button.setMinSize(width, height);
        button.setMaxSize(width, height);
        button.setPrefSize(width, height);

        //Set Alignment
        button.setAlignment(Pos.CENTER);

        //Set Background Color
        button.setBackground(new Background(new BackgroundFill(color, null, null)));

        button.setText(label);
        button.setFont(Font.font("Helvetica", 24));
        button.setTextFill(Color.WHITE);

        return button;
    }


}
