package arena;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Arena {

    private int width;
    private int height;
    private Hero hero;
    private List<Wall> walls;
    private boolean[][] wallGrid;
    private List<Coin> coins;
    private List<Monster> monsters;

    /**
     * Initializes an arena with the specified width and height.
     * @param width The width of the arena.
     * @param height The height of the arena.
     */
    public Arena(int width, int height){
        this.width = width;
        this.height = height;
        this.hero = new Hero(10,10);
        this.walls = createWalls();
        this.coins = createCoins();
        this.monsters = createMonsters();
    }

    /**
     * Creates the walls for the arena, using a grid-based approach.
     * @return A list of walls.
     */
    private List<Wall> createWalls() {
        this.wallGrid = new boolean[width][height];
        List<Wall> walls = new ArrayList<>();

        for (int c = 0; c < width; c++) {
            walls.add(new Wall(c, 0));
            wallGrid[c][0] = true;
            walls.add(new Wall(c, height - 1));
            wallGrid[c][height - 1] = true;
        }

        for (int r = 1; r < height - 1; r++) {
            walls.add(new Wall(0, r));
            wallGrid[0][r] = true;
            walls.add(new Wall(width - 1, r));
            wallGrid[width - 1][r] = true;
        }

        return walls;
    }

    /**
     * Creates the coins for the arena, using a random placement approach.
     * @return A list of coins.
     */
    private List<Coin> createCoins() {
        Random random = new Random();
        ArrayList<Coin> coins = new ArrayList<>();
        while(coins.size() < 5){
            int x = random.nextInt(width - 2) + 1;
            int y = random.nextInt(height - 2) + 1;
            Position placeTaken = new Position(x, y);
            if (placeTaken.equals(hero.getPosition())) continue;
            boolean alreadyExists = false;
            for (Coin coin : coins){
                if (coin.getPosition().equals(placeTaken)){
                    alreadyExists = true;
                    break;
                }
            }
            if (alreadyExists) continue;
            coins.add(new Coin(x, y));
        }
        return coins;
    }

    /**
     * Creates the monsters for the arena, using a random placement approach.
     * @return A list of monsters.
     */
    private List<Monster> createMonsters() {
        Random random = new Random();
        List<Monster> monsters = new ArrayList<>();

        while (monsters.size() < 3) {
            int x = random.nextInt(width -2) + 1;
            int y = random.nextInt(height - 2) + 1;
            Position possiblePos = new Position(x,y);

            if (possiblePos.equals(hero.getPosition())) continue;

            boolean placeTaken = false;
            for (Monster monster : monsters){
                if (monster.getPosition().equals(possiblePos)){
                    placeTaken = true;
                    break;
                }
            }
            if (placeTaken) continue;

            if (monsters.isEmpty()){
                monsters.add(new TrackingMonster(x, y, hero));
                continue;
            }
            else if (monsters.size() == 1){
                monsters.add(new PatrolMonster(x, y));
                continue;
            }
            else {
                monsters.add(new RandomMonster(x, y));
            }
        }
        return monsters;
    }

    /**
     * Removes a coin from the arena if it is at the same position as the hero.
     */
    private void retrieveCoins(){
        for (int i=0; i<coins.size(); i++){
            if (coins.get(i).getPosition().equals(hero.getPosition())){
                coins.remove(i);
                break;
            }
        }
    }

    /**
     * Retrieves the monster at a given position.
     * Iterates through the list of monsters to check if any monster's position matches
     * the given position. If a match is found, the monster is returned.
     * @param position The position to check.
     * @return The monster at the given position, or null if no monster is found.
     */
    private Monster getMonsterAt(Position position) {
        for (Monster monster : monsters) {
            if (monster.getPosition().equals(position)) {
                return monster;
            }
        }
        return null;
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
    public void draw(TextGraphics graphics){
        // background
        graphics.setBackgroundColor(TextColor.Factory.fromString("#96e072"));
        graphics.fillRectangle(new TerminalPosition(0,0), new TerminalSize(width,height), ' ');
        // arena elements
        for (Wall wall : walls) wall.draw(graphics);
        for (Coin coin : coins) coin.draw(graphics);
        for (Monster monster : monsters) monster.draw(graphics);
        hero.draw(graphics);
        // hud on top wall
        graphics.setBackgroundColor(TextColor.Factory.fromString("#333333"));
        graphics.setForegroundColor(TextColor.Factory.fromString("#FFFFFF"));
        graphics.enableModifiers(SGR.BOLD);

        String hpText = " HP: " + hero.getHealth() + " ";
        String coinText = " Coins left: " + coins.size() + " ";
        graphics.putString(new TerminalPosition(2, 0), hpText);
        graphics.putString(new TerminalPosition(width - coinText.length() - 2, 0), coinText);
        graphics.clearModifiers();

        //end game messages
        if (verifyGameOver()) {
            drawCenterBanner(graphics, "GAME OVER! Press Q to quit", "#AA0000", "#FFFFFF");
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