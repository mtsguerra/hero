import arena.Arena;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.List;

public class Game {
    private Screen screen;
    private Arena arena;
    private final List<String> levelFiles = List.of("levels/level1.txt", "levels/level2.txt");
    private int currentLevelIndex = 0;
    private boolean gameWon = false;
    private int heroHealth = 100;

    //scoring fields
    private int accumulatedScore = 0;
    private long levelStartTimeMs;
    private static final int INITIAL_TIME_BONUS = 1000;
    private static final int TIME_PENALTY_PER_SEC = 10;

    /**
     * Initializes the game.
     */
    public Game() {
        try {
            loadCurrentLevel();
            TerminalSize terminalSize = new TerminalSize(arena.getWidth(), arena.getHeight());
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory()
                    .setInitialTerminalSize(terminalSize);
            Terminal terminal = terminalFactory.createTerminal();
            this.screen = new TerminalScreen(terminal);

            this.screen.setCursorPosition(null);
            this.screen.startScreen();
            this.screen.doResizeIfNecessary();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCurrentLevel() {
        this.arena = new Arena(levelFiles.get(currentLevelIndex), this.heroHealth);
        this.levelStartTimeMs = System.currentTimeMillis();
    }

    private void restartGame() {
        this.currentLevelIndex = 0;
        this.accumulatedScore = 0;
        this.heroHealth = 100;
        this.gameWon = false;
        loadCurrentLevel();
    }

    private int getCurrentTimeBonus() {
        if (gameWon || arena.verifyGameOver()) {
            return 0;
        }
        long elapsedSeconds = (System.currentTimeMillis() - levelStartTimeMs) / 1000;
        return (int) Math.max(0, INITIAL_TIME_BONUS - (elapsedSeconds * TIME_PENALTY_PER_SEC));
    }
    private int getTotalCurrentScore() {
        if (gameWon) {
            return accumulatedScore;
        }
        return accumulatedScore
                + (arena.getCoinsCollected() * 100)
                + (arena.getMonstersKilled() * 150)
                + getCurrentTimeBonus();
    }

    private void nextLevel() {
        // Bank the current level's score plus bonuses into the persistent bank
        accumulatedScore += (arena.getCoinsCollected() * 100)
                + (arena.getMonstersKilled() * 150)
                + getCurrentTimeBonus()
                + 500
                + (arena.getHero().getHealth() * 5);

        heroHealth = Math.min(arena.getHero().getHealth() + 10, 100);
        currentLevelIndex++;
        if (currentLevelIndex < levelFiles.size()) {
            loadCurrentLevel();
        } else {
            this.gameWon = true;
        }
    }

    /**
     * Draws the game state.
     * @throws IOException If an I/O error occurs.
     */
    private void draw() throws IOException {
        this.screen.clear();
        arena.draw(screen.newTextGraphics(), getTotalCurrentScore(), getCurrentTimeBonus());

        if (gameWon) {
            TextGraphics g = screen.newTextGraphics();
            g.setBackgroundColor(TextColor.Factory.fromString("#008800"));
            g.setForegroundColor(TextColor.Factory.fromString("#FFFFFF"));
            g.enableModifiers(SGR.BOLD);

            String winMsg = "VICTORY! Final Score: " + accumulatedScore + " (R: Restart | Q: Quit)";
            int startX = Math.max(1, (arena.getWidth() - winMsg.length()) / 2);
            g.putString(new TerminalPosition(startX, arena.getHeight() / 2), winMsg);
            g.clearModifiers();
        }

        this.screen.refresh();
    }

    /**
     * Processes a key input.
     * @param key The key input to be processed.
     */
    private void processKey(KeyStroke key) {
        arena.processKey(key);
    }

    /**
     * Runs the game.
     */
    public void run() {
        try {
            while (true) {
                draw();
                KeyStroke key = screen.readInput();
                if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'q'){
                    screen.close();
                    break;
                }
                if (key.getKeyType() == KeyType.EOF) break;

                if (arena.verifyGameOver() || gameWon) {
                    if (key.getKeyType() == KeyType.Character && (key.getCharacter() == 'r' || key.getCharacter() == 'R')) {
                        restartGame();
                    }
                    continue;
                }

                processKey(key);

                if (arena.isLevelCompleted()) {
                    nextLevel();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}