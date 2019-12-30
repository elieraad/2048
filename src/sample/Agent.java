package sample;

import java.util.Collections;

public class Agent {

    private final Board board;
    private final Game game;

    public Tile[][] backgroundBoard = new Tile[Board.GRID_SIZE][Board.GRID_SIZE];

    int score;

    public Agent(Board board, Game game) {
        this.board = board;
        this.game = game;
    }

    public void run(Tile[][] board, int score) {
        for (int i = 0; i < board.length; i++)
            System.arraycopy(board[i], 0, backgroundBoard[i], 0, backgroundBoard.length);

        this.score = score;
        Tile bestMove = minimax(backgroundBoard);
        for (Tile[] row : backgroundBoard) {
            for (Tile tile : row) {
                if (tile != null)
                    System.out.printf("[%d]", tile.getValue());
                else System.out.print("[ ]");

            }
            System.out.println();
        }
        System.out.printf("Best move: %d, %d%n", bestMove.getX(), bestMove.getY());
        if (bestMove.getX() != 0 || bestMove.getY() != 0) {
            game.moveTiles(bestMove.getX(), bestMove.getY());
        } else {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    //skip no movement
                    if ((x == 0 && y == 0) || ((Math.abs(x) + Math.abs(y)) == 2))
                        continue;

                    if (availableMerges(backgroundBoard, x, y)) {
                        game.moveTiles(x, y);
                        return;
                    }

                }
            }
        }
    }

    private Tile minimax(Tile[][] backgroundBoard) {
        int maxEval = Integer.MIN_VALUE;
        Tile bestMove = new Tile(0);
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                //skip no movement
                if ((x == 0 && y == 0) || ((Math.abs(x) + Math.abs(y)) == 2))
                    continue;

                System.out.printf("Available merges in (%d, %d) direction %b%n", x, y, availableMerges(backgroundBoard, x, y));
                if (!availableMerges(backgroundBoard, x, y)) {
                    boolean available = false;
                    System.out.println("TEST");

                    for (Tile[] row : backgroundBoard) {
                        for (Tile tile : row) {
                            if (tile != null) {
                                int[] dt = game.getDestination(tile.getX(), tile.getY(), x, y);
                                if (dt[0] != tile.getX() || dt[1] != tile.getY()) {
                                    available = true;
                                    break;
                                }

                            }
                        }
                        if (available)
                            break;
                    }

                    if (!available)
                        continue;
                }

                //save the state
                Tile[][] gameState = new Tile[Board.GRID_SIZE][Board.GRID_SIZE];
                for (int i = 0; i < backgroundBoard.length; i++)
                    System.arraycopy(backgroundBoard[i], 0, gameState[i], 0, gameState.length);

                //Make the move
                move(x, y);

                int eval = max(backgroundBoard, 4);

                //undo the move
                for (int i = 0; i < backgroundBoard.length; i++)
                    System.arraycopy(gameState[i], 0, backgroundBoard[i], 0, backgroundBoard.length);

                if (maxEval < eval) {
                    maxEval = eval;
                    bestMove.setX(x);
                    bestMove.setY(y);
                }

            }
        }
        return bestMove;
    }

    private int max(Tile[][] backgroundBoard, int depth) {
        if (depth == 0 || game.isFull() && !game.availableMerges())
            return score /*+ (game.availableMerges() ? score : -score)*/;

        int maxEval = Integer.MIN_VALUE;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                //skip no movement
                if ((x == 0 && y == 0) || ((Math.abs(x) + Math.abs(y)) == 2))
                    continue;

                if (!availableMerges(backgroundBoard, x, y)) {
                    boolean available = false;
                    System.out.println("TEST");
                    for (Tile[] row : backgroundBoard) {
                        for (Tile tile : row) {
                            if (tile != null) {
                                int[] dt = game.getDestination(tile.getX(), tile.getY(), x, y);
                                if (dt[0] != tile.getX() || dt[1] != tile.getY()) {
                                    available = true;
                                    break;
                                }

                            }
                        }
                        if (available)
                            break;
                    }

                    if (!available)
                        continue;
                }
                //save the state
                Tile[][] gameState = new Tile[Board.GRID_SIZE][Board.GRID_SIZE];
                for (int i = 0; i < backgroundBoard.length; i++)
                    System.arraycopy(backgroundBoard[i], 0, gameState[i], 0, gameState.length);

                //Make the move
                move(x, y);

                int eval = max(backgroundBoard, depth - 1);

                //undo the move
                for (int i = 0; i < backgroundBoard.length; i++)
                    System.arraycopy(gameState[i], 0, backgroundBoard[i], 0, backgroundBoard.length);

                if (maxEval < eval) {
                    maxEval = eval;
                }

            }
        }

        return maxEval;
    }

/*    private int min(Tile[][] backgroundBoard, int score, int depth) {
        if (depth == 0 || game.isFull() && !game.availableMerges())
            return score;

        int minEval = Integer.MAX_VALUE;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                //skip no movement
                if (x == 0 && y == 0)
                    continue;

                //save the state
                Tile[][] gameState = new Tile[Board.GRID_SIZE][Board.GRID_SIZE];
                for (int i = 0; i < backgroundBoard.length; i++)
                    System.arraycopy(backgroundBoard[i], 0, gameState[i], 0, gameState.length);

                //Make the move
                move(x, y);

                int eval = max(backgroundBoard, depth - 1);

                //undo the move
                for (int i = 0; i < backgroundBoard.length; i++)
                    System.arraycopy(gameState[i], 0, backgroundBoard[i], 0, backgroundBoard.length);

                if (minEval > eval) {
                    minEval = eval;
                }

            }
        }

        return minEval;
    }*/

    public void move(int dirX, int dirY) {

        board.getxLocations().sort(dirX == 1 ? Collections.reverseOrder() : Integer::compareTo);
        board.getyLocations().sort(dirY == 1 ? Collections.reverseOrder() : Integer::compareTo);

        for (int x : board.getxLocations()) {
            for (int y : board.getyLocations()) {
                Tile tile = backgroundBoard[y][x];

                int[] destination = game.getDestination(x, y, dirX, dirY);
                int destinationX = destination[0];
                int destinationY = destination[1];

                if (tile != null && (destinationX != x || destinationY != y)) {
                    backgroundBoard[destinationY][destinationX] = tile;
                    backgroundBoard[tile.getY()][tile.getX()] = null;
                }

                int nextLocationX = destinationX + dirX;
                int nextLocationY = destinationY + dirY;

                if (tile != null && game.isValidLocation(nextLocationX, nextLocationY) && backgroundBoard[nextLocationY][nextLocationX] != null) {
                    Tile nextTile = backgroundBoard[nextLocationY][nextLocationX];

                    if (nextTile.equal(tile) && !nextTile.isMerged()) {
                        backgroundBoard[nextTile.getY()][nextTile.getX()] =
                                new Tile(nextTile.getValue() + tile.getValue(), nextTile.getX(), nextTile.getY());
                        backgroundBoard[tile.getY()][tile.getX()] = null;

                        score += nextTile.getValue();

                    }
                }

            }
        }


        // reset merged after each movement
        for (Tile[] gameRow : backgroundBoard) {
            for (Tile tile : gameRow) {
                if (tile != null) {
                    tile.clearMerge();
                }
            }
        }

    }

    public boolean availableMerges(Tile[][] gameGrid, int dirX, int dirY) {
        board.getxLocations().sort(dirX == 1 ? Collections.reverseOrder() : Integer::compareTo);
        board.getyLocations().sort(dirY == 1 ? Collections.reverseOrder() : Integer::compareTo);

        for (int x : board.getxLocations()) {
            for (int y : board.getyLocations()) {

                Tile tile = gameGrid[y][x];
                if (tile != null) {
                    int dx = x + dirX;
                    int dy = y + dirY;
                    if (game.isValidLocation(dx, dy)) {
                        Tile nextTile = gameGrid[y + dirY][x + dirX];
                        if (nextTile != null && tile.equal(nextTile))
                            return true;
                    }
                }

            }
        }


        return false;
    }
}
