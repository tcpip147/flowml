package com.tcpip147.flowml.ui.component;

import com.tcpip147.flowml.ui.FmlColor;

import java.awt.*;

public class RangeSelection extends Shape {

    public int x;
    public int y;
    public int width;
    public int height;

    public RangeSelection() {
        setVisible(false);
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
    public boolean inBound(Point p) {
        return false;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(FmlColor.RANGE_DEFAULT);
        g.drawRect(x, y, width, height);
    }
}
