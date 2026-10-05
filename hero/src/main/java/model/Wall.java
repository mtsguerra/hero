package model;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.awt.*;

public class Wall extends Element {

    /**
     * Initializes a wall at the specified position.
     * @param x The x-coordinate of the wall.
     * @param y The y-coordinate of the wall.
     */
    public Wall(int x, int y) {
        super(x,y);
    }

    /**
     * Draws the wall.
     * @param graphics The text graphics object to draw on.
     */
    @Override
    public void draw(TextGraphics graphics) {
        graphics.setForegroundColor(TextColor.Factory.fromString("#582f0e"));
        graphics.enableModifiers(SGR.BOLD);
        graphics.putString(new TerminalPosition(position.getX(), position.getY()), "#");
    }
}