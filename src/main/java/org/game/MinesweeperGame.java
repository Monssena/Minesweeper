package org.game;

import org.game.engine.Color;
import org.game.engine.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private final static String MINE = "\uD83D\uDCA3";
    private final static String FLAG = "\uD83D\uDEA9";
    private boolean isGameStopped = false;
    private final static int SIDE = 9;
    private final GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField = 0;
    private int countClosedTiles = SIDE * SIDE;
    private int countFlags;
    private int score = 0;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                setCellValue(j, i, "");
            }
        }

        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                boolean isMine = getRandomNumber(10) == 1;
                if (isMine) countMinesOnField++;
                //Почему i и j наоборот
                gameField[j][i] = new GameObject(i, j, isMine);
                setCellColor(i, j, Color.AQUA);
            }
        }
        countFlags = countMinesOnField;
        countMineNeighbors();
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.NONE, "You win", Color.GREEN, 75);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.NONE, "Game over", Color.DARKRED, 75);
    }

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        setScore(score);
        countMinesOnField = 0;
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
        } else openTile(x, y);
    }

    @Override
    public void onMouseRightClick(int x, int y) { markTile(x, y); }

    private void markTile(int x, int y) {
        if (isGameStopped || gameField[y][x].isOpen || (countFlags < 1 && !gameField[y][x].isFlag)) return;
        if (gameField[y][x].isFlag) {
            gameField[y][x].isFlag = false;
            countFlags++;
            setCellValueEx(x, y, Color.AQUA, "");
        } else {
            gameField[y][x].isFlag = true;
            countFlags--;
            setCellValueEx(x, y, Color.LIGHTSKYBLUE, FLAG);
        }
    }

    private void openTile(int x, int y) {
        if (isGameStopped || gameField[y][x].isOpen || gameField[y][x].isFlag) return;
        gameField[y][x].isOpen = true;
        countClosedTiles--;
        setCellColor(x, y, Color.BLUE);

        if (gameField[y][x].isMine) {
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
        } else {
            score = score + 5;
            setScore(score);
            if (countClosedTiles == countMinesOnField) win();
            int countMine = gameField[y][x].countMineNeighbors;
            if (countMine == 0) {
                setCellValue(x, y, "");
                for (GameObject gameObject : getNeighbors(gameField[y][x])) {
                    if (!gameObject.isOpen) openTile(gameObject.x, gameObject.y);
                }
            } else setCellNumber(x, y, countMine);
        }
    }

    private void countMineNeighbors() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (!gameField[j][i].isMine) {
                    for (GameObject gameObject : getNeighbors(gameField[j][i])) {
                        if (gameObject.isMine) gameField[j][i].countMineNeighbors++;
                    }
                }
            }
        }
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
}