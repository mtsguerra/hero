package arena;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import model.*;

import java.util.List;

public class Arena {

    private final int width;
    private final int height;
    private final Hero hero;
    private final List<Wall> walls;
    private final boolean[][] wallGrid;
    private final List<Coin> coins;
    private final Door door;
    private final List<Monster> monsters;

    private boolean levelCompleted;
    private int coinsCollected = 0;
    private int monstersKilled = 0;

    /**
     * Initializes an arena from a map resource file.
     * @param mapResourcePath The path to the map resource file.
     */
    public Arena(String mapResourcePath) {
        try {
            ArenaLoader loader = new ArenaLoader(mapResourcePath);
            this.width = loader.getWidth();
            this.height = loader.getHeight();
            this.hero = loader.getHero();
            this.walls = loader.getWalls();
            this.wallGrid = loader.getWallGrid();
            this.coins = loader.getCoins();
            this.door = loader.getDoor();
            this.monsters = loader.getMonsters();
        } catch (Exception e) {
            throw new RuntimeException("Error loading map: " + mapResourcePath, e);
        }
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public Hero getHero() {
        return hero;
    }
    public boolean isLevelCompleted() {
        return levelCompleted;
    }
    public int getCoinsCollected() {
        return coinsCollected;
    }
    public int getMonstersKilled() {
        return monstersKilled;
    }
    public void incrementMonstersKilled() {
        this.monstersKilled++;
    }

    /**
     * Removes a coin from the arena if it is at the same position as the hero.
     */
    private void retrieveCoins(){
        for (int i=0; i<coins.size(); i++){
            if (coins.get(i).getPosition().equals(hero.getPosition())){
                coins.remove(i);
                coinsCollected++;
                break;
            }
        }
        if (coinsCollected >= 5 && door != null) {
            door.setOpen(true);
        }
    }

    private void verifyDoorCollision() {
        if (door != null && door.isOpen() &&
                door.getPosition().equals(hero.getPosition())) {
            this.levelCompleted = true;
        }
    }

    /**
     * Verifies if the hero collides with any monsters in the arena.
     * Iterates through the list of monsters to check if any monster's position matches
     * the hero's current position. If a match is found, the hero's health is decreased
     * by 10, and the hero is moved to a random adjacent position not occupied
     * by a monster or a wall.
     */
    public void verifyMonsterCollisions(){
        for (Monster monster : monsters){
            if (monster.getPosition().equals(hero.getPosition())){
                System.out.println("Ouch!");
                hero.decreaseHealth(10);

                Position[] adjacentPositions = new Position[]{
                        new Position(monster.getPosition().getX() + 1, monster.getPosition().getY()),
                        new Position(monster.getPosition().getX() - 1, monster.getPosition().getY()),
                        new Position(monster.getPosition().getX(), monster.getPosition().getY() + 1),
                        new Position(monster.getPosition().getX(), monster.getPosition().getY() - 1)
                };

                for (Position adj : adjacentPositions) {
                    if (canMoveElement(adj) && !adj.equals(hero.getPosition())) {
                        monster.setPosition(adj);
                        break;
                    }
                }

                verifyGameOver();
                return;
            }
        }
    }

    public boolean verifyGameOver(){
        return hero.isDead();
    }

    /**
     * Checks if an element can move to a given position.
     * Checks if the position is within the arena boundaries and if it is not occupied by a wall.
     * @param position The position to check.
     * @return True if the element can move to the position, false otherwise.
     */
    private boolean canMoveElement(Position position){
        int x = position.getX();
        int y = position.getY();
        if (x < 0 || x >= width || y < 0 || y >= height){
            return false;
        }
        return !wallGrid[x][y];
    }

    /**
     * Moves the monsters in the arena.
     * Handles deterministic bouncing for patrol monsters and random attempts for others.
     */
    public void moveMonsters() {
        for (Monster monster : monsters) {
            if (monster instanceof PatrolMonster patrolMonster) {
                Position next = patrolMonster.move();
                if (canMoveElement(next)) {
                    patrolMonster.setPosition(next);
                } else {
                    // Wall hit: invert direction and take a step back
                    patrolMonster.reverseDirection();
                    Position bounce = patrolMonster.move();
                    if (canMoveElement(bounce)) {
                        patrolMonster.setPosition(bounce);
                    }
                }
            } else {
                int tries = 0;
                while (tries++ < 11) {
                    Position newPosition = monster.move();
                    if (canMoveElement(newPosition)) {
                        monster.setPosition(newPosition);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Moves the hero in the arena.
     * Checks if the hero can move to the given position. If the hero can move,
     * updates the hero's position, retrieves any coins at the new position,
     * moves the monsters, and verifies if the hero collides with any monsters.
     * @param position The position to move the hero to.
     */
    public void moveHero(Position position){
        if(canMoveElement(position)){
            hero.setPosition(position);
            retrieveCoins();
            verifyDoorCollision();
            moveMonsters();
            verifyMonsterCollisions();
        }
    }

    /**
     * Processes a keystroke and moves the hero accordingly.
     * Checks the type of the keystroke and moves the hero in the corresponding direction.
     * @param key The keystroke to the process.
     */
    public void processKey(KeyStroke key){
        System.out.println(key);

        switch (key.getKeyType()){
            case ArrowUp -> moveHero(hero.moveUp());
            case ArrowDown -> moveHero(hero.moveDown());
            case ArrowLeft -> moveHero(hero.moveLeft());
            case ArrowRight -> moveHero(hero.moveRight());
            default -> {}
        }
    }

    /**
     * Draws the arena elements on the terminal.
     * Fills the background color, draws the walls, coins, monsters, and hero.
     * Draws the health points and coins left on the top wall.
     * @param graphics The text graphics object to draw on.
     */
    public void draw(TextGraphics graphics, int totalScore, int timeBonus){
        // background
        graphics.setBackgroundColor(TextColor.Factory.fromString("#96e072"));
        graphics.fillRectangle(new TerminalPosition(0,0), new TerminalSize(width,height), ' ');
        // arena elements
        for (Wall wall : walls) wall.draw(graphics);
        if (door != null) door.draw(graphics);
        for (Coin coin : coins) coin.draw(graphics);
        for (Monster monster : monsters) monster.draw(graphics);
        hero.draw(graphics);
        // hud on top wall
        graphics.setBackgroundColor(TextColor.Factory.fromString("#333333"));
        graphics.setForegroundColor(TextColor.Factory.fromString("#FFFFFF"));
        graphics.enableModifiers(SGR.BOLD);

        String hpText = " HP:" + hero.getHealth() + " ";
        String coinText = door != null && door.isOpen() ? " [DOOR OPEN] " : " Coins:" + coinsCollected + "/5 ";
        String scoreText = " Score:" + totalScore + " (TimeBonus: " + timeBonus + ") ";

        graphics.putString(new TerminalPosition(1, 0), hpText);
        graphics.putString(new TerminalPosition(hpText.length() + 2, 0), coinText);
        graphics.putString(new TerminalPosition(Math.max(1, width - scoreText.length() - 1), 0), scoreText);
        graphics.clearModifiers();

        //end game messages
        if (verifyGameOver()) {
            drawCenterBanner(graphics, "GAME OVER! Score: " + totalScore + " (R: Restart | Q: Quit)", "#AA0000", "#FFFFFF");
        }
    }

    /**
     * Draws a centered banner on the terminal.
     * Sets the background color, foreground color, and bold modifier for the banner.
     * Draws the message at the specified position and clears the modifiers.
     * @param graphics The text graphics object to draw on.
     * @param message The message to display in the banner.
     * @param bgColor The background color of the banner.
     * @param fgColor The foreground color of the banner.
     */
    private void drawCenterBanner(TextGraphics graphics, String message, String bgColor, String fgColor) {
        int startX = Math.max(1, (width - message.length()) / 2);
        int startY = height / 2;
        graphics.setBackgroundColor(TextColor.Factory.fromString(bgColor));
        graphics.setForegroundColor(TextColor.Factory.fromString(fgColor));
        graphics.enableModifiers(SGR.BOLD);
        graphics.putString(new TerminalPosition(startX, startY), message);
        graphics.clearModifiers();
    }

}