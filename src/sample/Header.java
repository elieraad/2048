package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Header extends GridPane {
    private Tile backToMenu = new Tile(2048);
    private Label newButton = createButton("NEW", 24, Color.rgb(246, 94, 59));
    private Label undoButton = createButton("UNDO", 24, Color.rgb(246, 94, 59));

    private Label scoreLabel = createButton("SCORE\n0", 18, Color.BLACK);
    private Label highScoreLabel = createButton("BEST\n0", 18, Color.BLACK);

    public Tile getBackToMenu() {
        return backToMenu;
    }

    public Label getNewButton() {
        return newButton;
    }

    public Label getUndoButton() {
        return undoButton;
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }

    public Label getHighScoreLabel() {
        return highScoreLabel;
    }

    public Header() {
        add(backToMenu, 0, 0);
        add(scoreLabel, 1, 0);
        add(highScoreLabel, 2, 0);
        add(newButton, 1, 1);
        GridPane.setRowSpan(backToMenu, 2);
        setHgap(8);
        setVgap(8);
        setPadding(new Insets(8, 0, 20, 8));
    }

    public Label createButton(String label, int size, Color color) {
        Label button = new Label();
        double width = Board.CELL_SIZE * 1.4;
        double height = Board.CELL_SIZE / 2 - 8;

        button.setMinSize(width, height);
        button.setMaxSize(width, height);
        button.setPrefSize(width, height);

        //Set Alignment
        button.setAlignment(Pos.CENTER);
        button.setTextAlignment(TextAlignment.CENTER);

        //Set Background Color
        button.setBackground(new Background(new BackgroundFill(color, null, null)));

        button.setText(label);
        button.setFont(Font.font("Helvetica", FontWeight.BOLD, size));
        button.setTextFill(Color.WHITE);

        button.setPadding(new Insets(8));
        return button;
    }
}
