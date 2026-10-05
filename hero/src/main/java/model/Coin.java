package model;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

public class Coin extends Element {

    /**
     * Initializes a coin at the specified position.
     * @param x The x-coordinate of the coin.
     * @param y The y-coordinate of the coin.
     */
    public Coin(int x, int y){
        super(x,y);
    }

    /**
     * Draws the coin.
     * @param graphics The text graphics object to draw on.
     */
    @Override
    public void draw(TextGraphics graphics){
        graphics.setForegroundColor(TextColor.Factory.fromString("#ffff3f"));
        graphics.enableModifiers(SGR.BOLD);
        graphics.putString(new TerminalPosition(getPosition().getX(), getPosition().getY()), "O");
    }
}