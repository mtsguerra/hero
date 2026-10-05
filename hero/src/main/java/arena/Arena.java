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

    public Arena(int width, int height){
        this.width = width;
        this.height = height;
        this.hero = new Hero(10,10);
        this.walls = createWalls();
        this.coins = createCoins();
        this.monsters = createMonsters();
    }

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
            monsters.add(new Monster(x, y));
        }
        return monsters;
    }

    private void retrieveCoins(){
        for (int i=0; i<coins.size(); i++){
            if (coins.get(i).getPosition().equals(hero.getPosition())){
                coins.remove(i);
                break;
            }
        }
    }

    private Monster getMonsterAt(Position position) {
        for (Monster monster : monsters) {
            if (monster.getPosition().equals(position)) {
                return monster;
            }
        }
        return null;
    }

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

    private boolean canMoveElement(Position position){
        int x = position.getX();
        int y = position.getY();
        if (x < 0 || x >= width || y < 0 || y >= height){
            return false;
        }
        return !wallGrid[x][y];
    }

    public void moveMonsters(){
        for (Monster monster : monsters){
            int tries = 0;
            while (tries++<11){
                Position newPosition = monster.move();
                if (canMoveElement(newPosition)) {
                    monster.setPosition(newPosition);
                    break;
                }
            }
        }
    }

    public void moveHero(Position position){
        if(canMoveElement(position)){
            hero.setPosition(position);
            retrieveCoins();
            moveMonsters();
            verifyMonsterCollisions();
        }
    }


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