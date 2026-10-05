import arena.Arena;

import com.googlecode.lanterna.TerminalSize;
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
        this.arena = new Arena(levelFiles.get(currentLevelIndex));
    }

    private void nextLevel() {
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
    private void draw () throws IOException{
        this.screen.clear();
        arena.draw(screen.newTextGraphics());

        if (gameWon){
            var g = screen.newTextGraphics();
            g.setBackgroundColor(com.googlecode.lanterna.TextColor.Factory.fromString("#008800"));
            g.setForegroundColor(com.googlecode.lanterna.TextColor.Factory.fromString("#FFFFFF"));
            g.enableModifiers(com.googlecode.lanterna.SGR.BOLD);
            String winMsg = "YOU WON THE GAME! Press Q to quit";
            int startX = Math.max(1, (arena.getWidth() - winMsg.length()) / 2);
            g.putString(new com.googlecode.lanterna.TerminalPosition(startX, arena.getHeight() / 2), winMsg);
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