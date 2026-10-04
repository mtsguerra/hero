import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.awt.*;

public class Wall {
    private Position position;

    public Wall(int x, int y) {
        this.position = new Position(x, y);
    }

    Position getPosition() { return position; }
    void setPosition(Position position) { this.position = position;}

    void draw(TextGraphics graphics) {
        graphics.setForegroundColor(TextColor.Factory.fromString("#582f0e"));
        graphics.enableModifiers(SGR.BOLD);
        graphics.putString(new TerminalPosition(position.getX(), position.getY()), "#");
    }
}