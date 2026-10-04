import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

public class Hero extends Element {

    private Position position;
    private int speed = 1;

    Hero(int x, int y){ super(x,y); }

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

    @Override
    public void draw(TextGraphics graphics){
        graphics.setForegroundColor(TextColor.Factory.fromString("#04471c"));
        graphics.enableModifiers(SGR.BOLD);
        graphics.putString(new TerminalPosition(position.getX(),
                position.getY()), "X");
    }

}