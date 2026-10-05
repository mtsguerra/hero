package model;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.util.Random;

public class RandomMonster extends Monster {
    private final Random random = new Random();

    public RandomMonster(int x, int y) {
        super(x, y);
    }

    @Override
    public Position move() {
        int dx = random.nextInt(3) - 1;
        int dy = random.nextInt(3) - 1;
        return new Position(getPosition().getX() + dx, getPosition().getY() + dy);
    }

    @Override
    public void draw(TextGraphics graphics) {
        graphics.setForegroundColor(TextColor.Factory.fromString("#800080")); // Roxo
        graphics.enableModifiers(SGR.BOLD);
        graphics.putString(new TerminalPosition(position.getX(), position.getY()), "R");
    }
}