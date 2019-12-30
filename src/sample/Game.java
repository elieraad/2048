package sample;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class Game extends GridPane {
    private StateManager manager;
    private Board board;
    private Header header;
    private Tile[][] gameGrid;
    private Tile[][] lastState;
    private Set<Tile> mergedTiles = new HashSet<>();
    private boolean finishedTransitions;

    private int movedTiles = 0;
    public static int highScore = 0;
    public static int totalScore = 0;
    public static int totalGames = 0;

    private AudioClip swipeSound;
    private AudioClip mergeSound;


    public int getScore() {
        return score;
    }

    public static int score;

    public Tile[][] getLastState() {
        return lastState;
    }

    public void setLastState(Tile[][] lastState) {
        this.lastState = lastState;
    }

    public Tile[][] getGameGrid() {
        return gameGrid;
    }

    public boolean isFinishedTransitions() {
        return finishedTransitions;
    }

    public Header getHeader() {
        return header;
    }

    public Board getBoard() {
        return board;
    }

    public Game(StateManager manager) {
        header = new Header();
        board = new Board();
        add(header, 0, 0);
        add(board, 0, 1);
        this.gameGrid = board.getGameGrid();
        lastState = board.getLastState();
        finishedTransitions = true;

        swipeSound = new AudioClip(Paths.get("src/sample/swipe.mp3").toUri().toString());
        mergeSound = new AudioClip(Paths.get("src/sample/pop.mp3").toUri().toString());

        this.manager = manager;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("stats.txt"));
            highScore = Integer.valueOf(reader.readLine());
            totalScore = Integer.valueOf(reader.readLine());
            totalGames = Integer.valueOf(reader.readLine());
            totalGames += 1;
            header.getHighScoreLabel().setText(("BEST\n" + highScore));
            reader.close();
            System.out.printf("High Score: %s%nTotal Score: %s%nGames Played: %s%n", highScore, totalScore, totalGames);
            BufferedWriter writer = new BufferedWriter(new FileWriter("stats.txt"));
            writer.write(String.format("%s%n", highScore));
            writer.write(String.format("%s%n", totalScore));
            writer.write(String.format("%s%n", totalGames));
            writer.close();

        } catch (Exception e) {
        }
    }


    public void moveTiles(int dirX, int dirY) {
        ParallelTransition parallelTransition = new ParallelTransition();

        for (int i = 0; i < gameGrid.length; i++)
            System.arraycopy(gameGrid[i], 0, lastState[i], 0, lastState.length);

        movedTiles = 0;
        mergedTiles.clear();

        board.getxLocations().sort(dirX == 1 ? Collections.reverseOrder() : Integer::compareTo);
        board.getyLocations().sort(dirY == 1 ? Collections.reverseOrder() : Integer::compareTo);

        for (int x : board.getxLocations()) {
            for (int y : board.getyLocations()) {
                Tile tile = gameGrid[y][x];

                int[] destination = getDestination(x, y, dirX, dirY);
                int destinationX = destination[0];
                int destinationY = destination[1];

                if (tile != null && (destinationX != x || destinationY != y)) {
                    parallelTransition.getChildren().add(slideTile(tile, destinationX, destinationY));
                    System.out.printf("(%d, %d) -> (%d, %d)%n", tile.getX(), tile.getY(), destinationX, destinationY);

                    gameGrid[destinationY][destinationX] = tile;
                    gameGrid[tile.getY()][tile.getX()] = null;

                    tile.setX(destinationX);
                    tile.setY(destinationY);

                    movedTiles++;

                    if (Menu.soundsOn)
                        swipeSound.play();
                }

                int nextLocationX = destinationX + dirX;
                int nextLocationY = destinationY + dirY;

                if (tile != null && isValidLocation(nextLocationX, nextLocationY) && gameGrid[nextLocationY][nextLocationX] != null) {
                    Tile nextTile = gameGrid[nextLocationY][nextLocationX];

                    if (nextTile.equal(tile) && !nextTile.isMerged()) {
                        parallelTransition.getChildren().add(slideTile(tile, nextLocationX, nextLocationY));
                        System.out.printf("(%d, %d) merged with (%d, %d)%n", tile.getX(), tile.getY(), nextLocationX, nextLocationX);

                        gameGrid[tile.getY()][tile.getX()] = null;

                        movedTiles++;

                        nextTile.merge(tile);
                        nextTile.toFront();

                        if(nextTile.getValue() == 2048) {
                            if(manager.getTimer() != null)
                                manager.getTimer().stop();
                            System.out.println("You win!");
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("You Win!");
                            alert.setHeaderText(null);
                            alert.setContentText("Congratulations! You have completed the game.\nYou earned " + score + " points.");

                            ButtonType playAgain = new ButtonType("Play Again");
                            ButtonType quit = new ButtonType("Quit");

                            alert.getButtonTypes().setAll(playAgain, quit);
                            Optional<ButtonType> result = alert.showAndWait();

                            if (result.get() == playAgain) {
                                score = 0;
                                header.getScoreLabel().setText(("SCORE\n" + Game.score));
                                board.startNewGame();
                            } else {
                                Platform.exit();
                            }
                        }

                        score += nextTile.getValue();
                        header.getScoreLabel().setText(("SCORE\n" + score));

                        if (score > highScore) {
                            highScore = score;
                            header.getHighScoreLabel().setText(("BEST\n" + highScore));
                        }


                        try {
                            BufferedWriter writer = new BufferedWriter(new FileWriter("stats.txt"));
                            writer.write(String.format("%s%n", highScore));
                            writer.write(String.format("%s%n", totalScore + score));
                            writer.write(String.format("%s%n", totalGames));
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        parallelTransition.getChildren().add(scaleTile(nextTile));
                        mergedTiles.add(tile);

                        if (Menu.soundsOn)
                            mergeSound.play();
                    }
                }

            }
        }

        if (parallelTransition.getChildren().size() > 0) {
            finishedTransitions = false;

            parallelTransition.setOnFinished(e -> {
                board.getChildren().removeAll(mergedTiles);
                finishedTransitions = true;
                // reset merged after each movement
                for (Tile[] gameRow : gameGrid) {
                    for (Tile tile : gameRow) {
                        if (tile != null) {
                            tile.clearMerge();
                        }
                    }
                }
            });

            List<Integer> randomLocation = randomLocation();

            if (randomLocation != null && movedTiles > 0)
                createTile(randomLocation).play();

            System.out.printf("isFull:%b, availableMerges:%b%n", isFull(), availableMerges());
            if (isFull() && !availableMerges()) {
                if(manager.getTimer() != null)
                    manager.getTimer().stop();
                System.out.println("GAME OVER");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Game Over");
                alert.setHeaderText(null);
                alert.setContentText("You earned " + score + " points.");

                ButtonType playAgain = new ButtonType("Play Again");
                ButtonType quit = new ButtonType("Quit");

                alert.getButtonTypes().setAll(playAgain, quit);
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == playAgain) {
                    score = 0;
                    header.getScoreLabel().setText(("SCORE\n" + Game.score));
                    board.startNewGame();
                } else {
                    Platform.exit();
                }
            }
            parallelTransition.play();
        }

    }


    public boolean isFull() {
        for (Tile[] row : gameGrid)
            for (Tile t : row)
                if (t == null)
                    return false;
        return true;
    }

    public int[] getDestination(int x, int y, int dirX, int dirY) {
        int destinationX, destinationY;

        do {
            destinationX = x;
            destinationY = y;
            x = destinationX + dirX;
            y = destinationY + dirY;
        } while (isValidLocation(x, y) && gameGrid[y][x] == null);

        return new int[]{destinationX, destinationY};
    }

    private Timeline slideTile(Tile tile, int newLocationX, int newLocationY) {
        int newLayoutX = (newLocationX * Board.CELL_SIZE) + Board.CELL_SIZE / 2;
        int newLayoutY = (newLocationY * Board.CELL_SIZE) + Board.CELL_SIZE / 2;

        KeyValue valueX = new KeyValue(tile.layoutXProperty(), newLayoutX - (tile.getMinHeight() / 2));
        KeyFrame frameX = new KeyFrame(Duration.millis(200), valueX);

        KeyValue valueY = new KeyValue(tile.layoutYProperty(), newLayoutY - (tile.getMinHeight() / 2));
        KeyFrame frameY = new KeyFrame(Duration.millis(200), valueY);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(frameX, frameY);

        return timeline;
    }

    private SequentialTransition scaleTile(Tile tile) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), tile);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.2);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), tile);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        return new SequentialTransition(scaleUp, scaleDown);
    }

    private List<Integer> randomLocation() {
        List<List<Integer>> availableLocations = new ArrayList<>();
        for (int x : board.getxLocations()) {
            for (int y : board.getyLocations()) {
                if (gameGrid[y][x] == null) {
                    List<Integer> location = new ArrayList<>();
                    location.add(x);
                    location.add(y);
                    availableLocations.add(location);
                }

            }
        }

        if (availableLocations.isEmpty()) {
            return null;
        }

        Random random = new Random();
        Collections.shuffle(availableLocations, random);

        return availableLocations.get(random.nextInt(availableLocations.size()));
    }

    public boolean availableMerges() {

        int dirX = 0, dirY = -1;

        board.getxLocations().sort(Integer::compareTo);
        board.getyLocations().sort(Integer::compareTo);

        for (int i = 0; i < 2; i++) {
            for (int x : board.getxLocations()) {
                for (int y : board.getyLocations()) {

                    Tile tile = gameGrid[y][x];
                    if (tile != null) {
                        int dx = x + dirX;
                        int dy = y + dirY;
                        if (isValidLocation(dx, dy)) {
                            Tile nextTile = gameGrid[y + dirY][x + dirX];
                            if (nextTile != null && tile.equal(nextTile))
                                return true;
                        }
                    }

                }
            }
            dirX = -1;
            dirY = 0;
        }


        return false;
    }

    private ScaleTransition createTile(List<Integer> randomLocation) {
        Tile tile = Tile.randomTile();

        tile.setX(randomLocation.get(0));
        tile.setY(randomLocation.get(1));

        double layoutX = tile.getPosX() - (tile.getMinWidth() / 2);
        double layoutY = tile.getPosY() - (tile.getMinHeight() / 2);

        tile.setLayoutX(layoutX);
        tile.setLayoutY(layoutY);

        tile.setScaleX(0.1);
        tile.setScaleY(0.1);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), tile);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);

        board.addTile(tile);
        gameGrid[tile.getY()][tile.getX()] = tile;

        return scaleTransition;
    }

    public boolean isValidLocation(int x, int y) {
        return x >= 0 && x < Board.GRID_SIZE && y >= 0 && y < Board.GRID_SIZE;
    }

    public void checkPrevState() {
        for (int i = 0; i < Board.GRID_SIZE; i++) {
            for (int j = 0; j < Board.GRID_SIZE; j++) {
                if (lastState[i][j] == null && gameGrid[i][j] == null)
                    continue;
                if (lastState[i][j] != null && gameGrid[i][j] != null) {
                    if (!lastState[i][j].equal(gameGrid[i][j])) {
                        System.out.println("You have a prev state");
                        restoreState();
                        return;
                    }
                } else {
                    System.out.println("You have a prev state");
                    restoreState();
                    return;
                }
            }
        }
        System.out.println("You don't have any prev state");
    }


    private void restoreState() {

        board.getxLocations().clear();
        board.getyLocations().clear();

        for (int i = 0; i < Board.GRID_SIZE; i++) {
            board.getxLocations().add(i);
            board.getyLocations().add(i);
        }

        System.out.println("Current State");
        for (Tile[] gameRow : gameGrid) {
            for (Tile tile : gameRow) {
                if (tile != null) {
                    System.out.printf("[%d]", tile.getValue());
                } else System.out.print("[ ]");
            }
            System.out.println();
        }

        System.out.println("Previous State");
        for (Tile[] gameRow : lastState) {
            for (Tile tile : gameRow) {
                if (tile != null) {
                    System.out.printf("[%d]", tile.getValue());
                } else System.out.print("[ ]");
            }
            System.out.println();
        }


        for (int i = 0; i < Board.GRID_SIZE; i++) {
            for (int j = 0; j < Board.GRID_SIZE; j++) {
                Tile currStateTile = gameGrid[i][j];
                if (currStateTile != null) {
                    board.getChildren().remove(currStateTile);
                    System.out.printf("Removing tile at %d, %d%n", j, i);
                }
            }
        }

        for (int i = 0; i < Board.GRID_SIZE; i++) {
            for (int j = 0; j < Board.GRID_SIZE; j++) {
                Tile prevStateTile = lastState[i][j];
                gameGrid[i][j] = prevStateTile;
                if (prevStateTile != null) {
                    prevStateTile.setX(j);
                    prevStateTile.setY(i);

                    int newLayoutX = (j * Board.CELL_SIZE) + Board.BORDER_WIDTH / 2;
                    int newLayoutY = (i * Board.CELL_SIZE) + Board.BORDER_WIDTH / 2;

                    prevStateTile.setLayoutX(newLayoutX);
                    prevStateTile.setLayoutY(newLayoutY);

                    prevStateTile.setScaleX(0.1);
                    prevStateTile.setScaleY(0.1);

                    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), prevStateTile);
                    scaleTransition.setToX(1.0);
                    scaleTransition.setToY(1.0);

                    board.getChildren().add(prevStateTile);
                    scaleTransition.play();
                    System.out.printf("Adding tile at %d, %d%n", j, i);
                }
            }
        }

    }

}
