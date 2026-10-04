import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Arena {

    private int width;
    private int height;
    private Hero hero;
    private List<Wall> walls;
    private List<Coin> coins;

    public Arena(int width, int height){
        this.width = width;
        this.height = height;
        this.hero = new Hero(10,10);
        this.walls = createWalls();
        this.coins = createCoins();
    }

    private List<Wall> createWalls() {
        List<Wall> walls = new ArrayList<>();

        for (int c = 0; c < width; c++) {
            walls.add(new Wall(c, 0));
            walls.add(new Wall(c, height - 1));
        }

        for (int r = 1; r < height - 1; r++) {
            walls.add(new Wall(0, r));
            walls.add(new Wall(width - 1, r));
        }

        return walls;
    }

    private List<Coin> createCoins() {
        Random random = new Random();
        ArrayList<Coin> coins = new ArrayList<>();
        while(coins.size() < 5){
            int x = random.nextInt(width - 2) + 1;
            int y = random.nextInt(height - 2) + 1;
            Position possiblePos = new Position(x, y);
            if (possiblePos.equals(hero.getPosition())) continue;
            boolean alreadyExists = false;
            for (Coin coin : coins){
                if (coin.getPosition().equals(possiblePos)){
                    alreadyExists = true;
                    break;
                }
            }
            if (alreadyExists) continue;
            coins.add(new Coin(x, y));
        }
        return coins;
    }

    private void retrieveCoins(){
        for (int i=0; i<coins.size(); i++){
            if (coins.get(i).getPosition().equals(hero.getPosition())){
                coins.remove(i);
                break;
            }
        }
    }

    private boolean canMoveHero(Position position){
        if (position.getX() < 0 || position.getX() >= width ||
            position.getY() < 0 || position.getY() >= height){return false;}
        for (Wall wall : walls){
            if (wall.getPosition().equals(position)) return false;
        }
        return true;
    }

    public void moveHero(Position position){
        if(canMoveHero(position)){
            hero.setPosition(position);
            retrieveCoins();
        }
    }

    void processKey(KeyStroke key){
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
        graphics.setBackgroundColor(TextColor.Factory.fromString("#96e072"));
        graphics.fillRectangle(new TerminalPosition(0,0), new TerminalSize(width,height), ' ');
        for (Wall wall : walls) wall.draw(graphics);
        for (Coin coin : coins) coin.draw(graphics);
        hero.draw(graphics);
    }



}