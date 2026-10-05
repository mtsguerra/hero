package model;

public class Position {

    private int x;
    private int y;

    /**
     * Initializes a position with the specified x and y coordinates.
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     */
    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return x;
    }
    void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    void setY(int y) {
        this.y = y;
    }

    /**
     * Checks if this position is equal to another object.
     * @param o The object to compare with.
     * @return True if the positions are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        Position p = (Position) o;
        return x == p.getX() && y == p.getY();
    }

}