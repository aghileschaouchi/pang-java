package me.foly.si.core.misc;

public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void translate(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public void translateX(int x) {
        translate(x, 0);
    }

    public void translateY(int y) {
        translate(0, y);
    }
}
