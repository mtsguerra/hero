import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.screen.Screen;

public class Hero {

    private Position position;
    private int speed = 1;

    Hero(int x, int y){
        position = new Position(x, y);
    }

    public Position getPosition(){return position;}
    public void setPosition(Position position){this.position = position;}

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

    void draw(Screen screen){
        screen.setCharacter(position.getX(), position.getY(),
                TextCharacter.fromCharacter('X')[0]);
    }

}