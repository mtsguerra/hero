package model;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

public class Hero extends Element {

    private int speed = 1;
    private int health = 100;

    public Hero(int x, int y){
        super(x,y);
    }

    public Position moveUp() {
        return new Position(position.getX(), position.getY() - speed);
    }

    public Position moveDown() {
        return new Position(position.getX(), position.getY() + speed);
    }

    public Position moveLeft() {
        return new Position(position.getX() - speed, position.getY());
    }

    public Position  moveRight() {
        return new Position(position.getX() + speed, position.getY());
    }

    public int getHealth(){
        return health;
    }

    public void decreaseHealth(int damage){
        this.health = Math.max(0, this.health - damage);
    }

    public boolean isDead(){
        return health <= 0;
    }


    @Override
    public void draw(TextGraphics graphics){
        graphics.setForegroundColor(TextColor.Factory.fromString("#04471c"));
        graphics.enableModifiers(SGR.BOLD);
        graphics.putString(new TerminalPosition(position.getX(),
                position.getY()), "X");
    }

}