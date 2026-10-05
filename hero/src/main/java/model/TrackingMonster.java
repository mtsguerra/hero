package model;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.util.Random;

public class TrackingMonster extends Monster {
    private final Hero hero;
    private final Random random = new Random();

    private int stepCounter = 0;
    private Position lastKnownTarget;

    public TrackingMonster(int x, int y, Hero hero) {
        super(x, y);
        this.hero = hero;
        this.lastKnownTarget = hero.getPosition();
    }

    @Override
    public Position move() {
        stepCounter++;

        // Updates at every 7 turns
        if (stepCounter % 7 == 0) {
            lastKnownTarget = hero.getPosition();
        }

        // If at the last known spot, wander randomly
        if (lastKnownTarget == null || getPosition().equals(lastKnownTarget)) {
            int dx = random.nextInt(3) - 1;
            int dy = random.nextInt(3) - 1;
            return new Position(getPosition().getX() + dx, getPosition().getY() + dy);
        }

        // Focusing on the dominant axis
        int diffX = lastKnownTarget.getX() - getPosition().getX();
        int diffY = lastKnownTarget.getY() - getPosition().getY();
        int stepX = 0;
        int stepY = 0;

        if (Math.abs(diffX) >= Math.abs(diffY)) {
            stepX = Integer.compare(diffX, 0);
        } else {
            stepY = Integer.compare(diffY, 0);
        }

        return new Position(getPosition().getX() + stepX, getPosition().getY() + stepY);
    }

    @Override
    public void draw(TextGraphics graphics) {
        graphics.setForegroundColor(TextColor.Factory.fromString("#FF8800")); // Orange hunter
        graphics.enableModifiers(SGR.BOLD);
        graphics.putString(new TerminalPosition(getPosition().getX(), getPosition().getY()), "T");
    }
}