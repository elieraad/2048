package sample;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

class StateManager {

    private Menu menu;
    private Game game;

    public AnimationTimer getTimer() {
        return timer;
    }

    private AnimationTimer timer;
    StateManager(FlowPane root) {
        menu = new Menu();
        game = new Game(this);

        root.getChildren().add(menu);

        menu.getAgentPlayButton().setOnMouseClicked(e -> {
            root.getChildren().clear();
            game.getBoard().startNewGame();
            root.getChildren().add(game);

            Agent agent = new Agent(game.getBoard(), game);
            timer = new AnimationTimer() {
/*                int fps = 1; //number of update per second.
                double tickPerSecond = 1_000_000_000.0 / fps;
                double delta = 0;
                long lastTime = System.nanoTime();*/

                @Override
                public void handle(long now) {
/*                    delta += (now - lastTime) / tickPerSecond;
                    lastTime = now;
                    //System.out.printf("Delta %.2f%n", delta);

                    if (delta >= 1) {*/
                    if (game.isFinishedTransitions())
                        agent.run(game.getGameGrid(), Game.score);
                    if (game.isFull() && !game.availableMerges()) {
                        System.out.print("Agent done!");
                        stop();
                    }
//                        delta = 0;
                }
//                }
            };

            timer.start();
        });
        menu.getClassicPlayButton().setOnMouseClicked(e -> {
            root.getChildren().clear();
            game.getBoard().startNewGame();
            Label newGame = game.getHeader().getNewButton();
            Label undo = game.getHeader().getUndoButton();

            Game.score = 0;
            game.getHeader().getScoreLabel().setText(("SCORE\n" + Game.score));

            try {
                if (!Menu.undoOn) {
                    game.getHeader().getChildren().remove(undo);
                    GridPane.setColumnSpan(newGame, 2);
                    newGame.setPrefWidth(Board.CELL_SIZE * 2.87);
                    newGame.setMaxWidth(Board.CELL_SIZE * 2.87);
                    newGame.setMinWidth(Board.CELL_SIZE * 2.87);
                } else {
                    game.getHeader().add(undo, 2, 1);
                    GridPane.setColumnSpan(newGame, 1);
                    newGame.setPrefWidth(Board.CELL_SIZE * 1.4);
                    newGame.setMaxWidth(Board.CELL_SIZE * 1.4);
                    newGame.setMinWidth(Board.CELL_SIZE * 1.4);
                }
            } catch (Exception ignored) {
            }

            root.getChildren().add(game);
            game.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (game.isFinishedTransitions()) {
                    switch (keyEvent.getCode()) {
                        case UP:
                        case W:
                            System.out.println("UP");
                            game.moveTiles(0, -1);
                            break;
                        case DOWN:
                        case S:
                            System.out.println("DOWN");
                            game.moveTiles(0, 1);
                            break;
                        case LEFT:
                        case A:
                            System.out.println("LEFT");
                            game.moveTiles(-1, 0);
                            break;
                        case RIGHT:
                        case D:
                            System.out.println("RIGHT");
                            game.moveTiles(1, 0);
                            break;
                        default:
                            System.out.println("You pressed a key");
                            break;
                    }
                }
            });

            game.requestFocus();
        });

        game.getHeader().getBackToMenu().setOnMouseClicked(e -> {
            root.getChildren().clear();
            root.getChildren().add(menu);
        });

        game.getHeader().getNewButton().setOnMouseClicked(e -> {
            Game.score = 0;
            game.getHeader().getScoreLabel().setText(("SCORE\n" + Game.score));
            game.getBoard().startNewGame();
        });

        game.getHeader().getUndoButton().setOnMouseClicked(e -> game.checkPrevState());

    }

}
