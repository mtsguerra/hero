import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.screen.Screen;

public class Hero {

    private int x;
    private int y;
    private int speed = 1;

    Hero(int x, int y){
        this.x = x;
        this.y = y;
    }

    int getX(){return x;}
    void setX(int x) {this.x = x;}
    int getY() {return y;}
    void setY(int y) {this.y = y;}


    public void moveUp() {
        this.y--;
    }

    public void moveDown() {
        this.y++;
    }

    public void moveLeft() {
        this.x--;
    }

    public void moveRight() {
        this.x++;
    }

    void draw(Screen screen){
        screen.setCharacter(x, y, TextCharacter.fromCharacter('X')[0]);
    }

}