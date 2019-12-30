package sample;

import javafx.animation.ScaleTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.*;

public class Board extends Pane {

    public static final int CELL_SIZE = 128;
    public static final int GRID_SIZE = 4;
    public static final int BORDER_WIDTH = 9;
    public static final int GRID_WIDTH = CELL_SIZE * GRID_SIZE + BORDER_WIDTH / 2;

    private final Tile[][] gameGrid = new Tile[GRID_SIZE][GRID_SIZE];
    private Tile[][] lastState = new Tile[GRID_SIZE][GRID_SIZE];

    private final List<Integer> xLocations = new ArrayList<>();
    private final List<Integer> yLocations = new ArrayList<>();

    public List<Integer> getxLocations() {
        return xLocations;
    }

    public List<Integer> getyLocations() {
        return yLocations;
    }

    public Tile[][] getGameGrid() {
        return gameGrid;
    }

    public Tile[][] getLastState() {
        return lastState;
    }

    public Board() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                getChildren().add(createCell(i, j));
            }
        }

        for (int i = 0; i < GRID_SIZE; i++) {
            xLocations.add(i);
            yLocations.add(i);
        }

    }

    private Rectangle createCell(int i, int j) {
        Rectangle cell = new Rectangle(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        cell.setFill(Color.rgb(200, 190, 180));
        cell.setStroke(Color.rgb(190, 170, 160));
        cell.setStrokeWidth(BORDER_WIDTH);
        return cell;
    }

    public void startNewGame() {
        for (Tile[] gameRow : gameGrid) {
            for (Tile tile : gameRow) {
                if (tile != null) {
                    this.getChildren().remove(tile);
                }
            }
        }

        for (Tile[] gameRow : gameGrid)
            Arrays.fill(gameRow, null);

        for (Tile[] gameRow : lastState)
            Arrays.fill(gameRow, null);

        Tile randomTile = Tile.randomTile();
        Random random = new Random();
        int rndX = random.nextInt(GRID_SIZE);
        int rndY = random.nextInt(GRID_SIZE);

        randomTile.setX(rndX);
        randomTile.setY(rndY);

        gameGrid[rndY][rndX] = randomTile;
        lastState[rndY][rndX] = randomTile;

        randomTile.setScaleX(0.1);
        randomTile.setScaleY(0.1);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), randomTile);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);


        for (Tile[] gameRow : gameGrid) {
            for (Tile tile : gameRow) {
                if (tile != null) {
                    addTile(tile);
                }
            }
        }

        scaleTransition.play();

    }

    public void addTile(Tile tile) {
        double layoutX = tile.getPosX() - (tile.getMinWidth() / 2);
        double layoutY = tile.getPosY() - (tile.getMinHeight() / 2);

        tile.setLayoutX(layoutX);
        tile.setLayoutY(layoutY);
        getChildren().add(tile);
    }

}
