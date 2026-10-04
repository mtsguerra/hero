import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;

import java.security.Key;

public class Arena {

    private int width;
    private int height;
    private Hero hero;

    public Arena(int width, int height){
        this.width = width;
        this.height = height;
        this.hero = new Hero(10,10);
    }

    private boolean canMoveHero(Position position){
        return position.getX() >= 0 && position.getX() < width &&
                position.getY() >= 0 && position.getY() < height;
    }

    public void moveHero(Position position){
        if(canMoveHero(position)){
            hero.setPosition(position);
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

    void draw(Screen screen){
        hero.draw(screen);
    }



}