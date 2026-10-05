package model;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

public class PatrolMonster extends Monster {
    // 1: right, -1: left
    private int directionX = 1;

    public PatrolMonster(int x, int y) {
        super(x, y);
    }

    @Override
    public Position move() {
        return new Position(getPosition().getX() + directionX, getPosition().getY());
    }

    public void reverseDirection() {
        this.directionX = -this.directionX;
    }

    @Override
    public void draw(TextGraphics graphics) {
        graphics.setForegroundColor(TextColor.Factory.fromString("#0000FF")); // Azul
        graphics.enableModifiers(SGR.BOLD);
        graphics.putString(new TerminalPosition(position.getX(), position.getY()), "P");
    }
}