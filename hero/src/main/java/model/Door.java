package model;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

public class Door extends Element {
    private boolean open = false;

    public Door(int x, int y) {
        super(x, y);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public void draw(TextGraphics graphics) {
        // Stay hidden until all coins are collected
        if (!open) {
            return;
        }
        graphics.setForegroundColor(TextColor.Factory.fromString("#00FFFF"));
        graphics.enableModifiers(SGR.BOLD);
        graphics.putString(new TerminalPosition(position.getX(), position.getY()), "D");
        graphics.clearModifiers();
    }
}