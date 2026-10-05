import arena.Arena;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Game {
    private Screen screen;
    private Arena arena;

    /**
     * Initializes the game.
     */
    public Game() {
        try {
            arena = new Arena("levels/level1.txt");
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

    /**
     * Draws the game state.
     * @throws IOException If an I/O error occurs.
     */
    private void draw () throws IOException{
        this.screen.clear();
        arena.draw(screen.newTextGraphics());
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

                if (arena.verifyGameOver()) {
                    continue;
                }

                processKey(key);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}