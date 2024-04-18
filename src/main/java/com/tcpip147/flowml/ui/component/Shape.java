package com.tcpip147.flowml.ui.component;

import java.awt.*;

public abstract class Shape {

    public boolean selected = false;
    public boolean visible = true;
    public int frontLayerLevel;

    public abstract void draw(Graphics2D g);

    public abstract boolean inBound(Point p);

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setFrontLayerLevel(int frontLayerLevel) {
        this.frontLayerLevel = frontLayerLevel;
    }
}
