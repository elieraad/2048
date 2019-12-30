package sample;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashMap;
import java.util.Random;

public class Tile extends Label {

    private int value;

    private int x;
    private int y;

    private Boolean merged;
    private ColorChooser colorChooser = new ColorChooser();

    public Tile(int value, int x, int y) {
        this.value = value;
        this.merged = false;
        this.x = x;
        this.y = y;

        //Set size
        int squareSize = Board.CELL_SIZE - Board.BORDER_WIDTH;
        setMinSize(squareSize, squareSize);
        setMaxSize(squareSize, squareSize);
        setPrefSize(squareSize, squareSize);

        //Set Alignment
        setAlignment(Pos.CENTER);

        //Set Background Color
        setBackground(new Background(new BackgroundFill(colorChooser.pick.get(value), null, null)));

        //Set Text
        String label = String.valueOf(value);
        setLabel(label);
    }

    public Tile(int value) {
        this(value, 0, 0);
    }

    public static Tile randomTile() {
        return new Tile(new Random().nextDouble() < 0.75 ? 2 : 4);
    }


    public void merge(Tile tile) {
        this.value += tile.getValue();
        updateTextFill(value);
        setText(String.valueOf(value));
        merged = true;
        setBackground(new Background(new BackgroundFill(colorChooser.pick.get(value), null, null)));
    }

    public int getValue() {
        return value;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPosY() {
        return (y * Board.CELL_SIZE) + Board.CELL_SIZE / 2;
    }

    public int getPosX() {
        return (x * Board.CELL_SIZE) + Board.CELL_SIZE / 2;
    }

    public boolean isMerged() {
        return merged;
    }

    public void clearMerge() {
        merged = false;
    }

    public boolean equal(Tile tile) {
        if (tile == null) {
            return false;
        }
        return tile.getValue() == getValue();
    }

    private void setLabel(String label) {
        int size = 130 / label.length() > 60 ? 45 : 130 / label.length();
        Font font = Font.font("Helvetica", FontWeight.BOLD, size);
        setFont(font);
        setText(label);
        updateTextFill(value);
    }

    private void updateTextFill(int value) {
        switch (value) {
            case 2:
            case 4:
                setTextFill(Color.rgb(119, 110, 101));
                break;
            default:
                setTextFill(Color.rgb(249, 246, 242));
                break;
        }
    }

    class ColorChooser {
        HashMap<Integer, Color> pick = new HashMap<>();

        public ColorChooser() {
            pick.put(2, Color.rgb(238, 228, 218));
            pick.put(4, Color.rgb(237, 224, 200));
            pick.put(8, Color.rgb(242, 177, 121));
            pick.put(16, Color.rgb(245, 149, 99));
            pick.put(32, Color.rgb(246, 124, 95));
            pick.put(64, Color.rgb(246, 94, 59));
            pick.put(128, Color.rgb(237, 207, 114));
            pick.put(256, Color.rgb(237, 204, 97));
            pick.put(512, Color.rgb(237, 200, 80));
            pick.put(1024, Color.rgb(237, 197, 63));
            pick.put(2048, Color.rgb(237, 194, 46));
        }
    }

}
