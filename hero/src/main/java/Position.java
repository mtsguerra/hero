public class Position {

    private int x;
    private int y;

    Position (int x, int y){
        this.x = x;
        this.y = y;
    }

    int getX(){return x;}
    void setX(int x) {this.x = x;}
    int getY() {return y;}
    void setY(int y) {this.y = y;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        Position p = (Position) o;
        return x == p.getX() && y == p.getY();
    }

}