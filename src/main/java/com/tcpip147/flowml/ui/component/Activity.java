package com.tcpip147.flowml.ui.component;

import com.tcpip147.flowml.ui.FmlColor;

import java.awt.*;

public class Activity extends Shape {

    public int x;
    public int y;
    public int width;
    public int height;

    public Activity(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(FmlColor.ACTIVITY_DEFAULT);
        g.fillRect(x, y, width, height);
        if (selected) {
            g.setColor(FmlColor.ACTIVITY_SELECTION_MARK_OUTER);
            g.fillRect(x - 3, y + height / 2 - 3, 6, 6);
            g.fillRect(x + width - 3, y + height / 2 - 3, 6, 6);
            g.setColor(FmlColor.ACTIVITY_SELECTION_MARK);
            g.fillRect(x - 2, y + height / 2 - 2, 4, 4);
            g.fillRect(x + width - 2, y + height / 2 - 2, 4, 4);
        }
    }

    @Override
    public boolean inBound(Point p) {
        return p.x > x && p.x < x + width && p.y > y && p.y < y + height;
    }

    public Shape createGhost() {
        return new GhostActivity(this);
    }
}
