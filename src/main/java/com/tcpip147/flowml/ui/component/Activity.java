package com.tcpip147.flowml.ui.component;

import com.tcpip147.flowml.ui.FmlColor;
import org.apache.http.impl.conn.Wire;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class Activity extends Shape {

    public int x;
    public int y;
    public int width;
    public int height;
    public String name;
    public boolean primary;
    public List<Wire> wireList = new ArrayList<>();
    public boolean showWireMark;

    public Activity(String name, int x, int y, int width) {
        if ("$start".equals(name) || "$end".equals(name)) {
            primary = true;
        }
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = 26;
        setFrontLayerLevel(1);
    }

    public void setX(int x) {
        if (x < 0) {
            this.x = 0;
        } else {
            this.x = x;
        }
    }

    public void setY(int y) {
        if (y < 0) {
            this.y = 0;
        } else {
            this.y = y;
        }
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setShowWireMark(boolean showWireMark) {
        this.showWireMark = showWireMark;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(FmlColor.ACTIVITY_OUTLINE);
        g.fillRoundRect(x - 1, y - 1, width + 2, height + 2, 5, 5);
        g.setColor(FmlColor.ACTIVITY_DEFAULT);
        g.fillRoundRect(x, y, width, height, 5, 5);
        g.setColor(FmlColor.ACTIVITY_NAME);
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(name, x + (width - metrics.stringWidth(name)) / 2, y + (height - metrics.getHeight()) / 2 + metrics.getAscent());
        if (selected) {
            g.setColor(FmlColor.ACTIVITY_SELECTION_MARK_OUTER);
            g.fillRect(x - 3, y + height / 2 - 3, 6, 6);
            g.fillRect(x + width - 3, y + height / 2 - 3, 6, 6);
            g.setColor(FmlColor.ACTIVITY_SELECTION_MARK);
            g.fillRect(x - 2, y + height / 2 - 2, 4, 4);
            g.fillRect(x + width - 2, y + height / 2 - 2, 4, 4);
        }
        if (showWireMark) {
            g.setColor(FmlColor.WIRE_SELECTION_MARK_OUTER);
            g.fillRect(x - 3, y + height / 2 - 3, 6, 6);
            g.fillRect(x + width - 3, y + height / 2 - 3, 6, 6);
            g.fillRect(x + width / 2 - 3, y - 3, 6, 6);
            g.fillRect(x + width / 2 - 3, y + height - 3, 6, 6);
            g.setColor(FmlColor.WIRE_SELECTION_MARK);
            g.fillRect(x - 2, y + height / 2 - 2, 4, 4);
            g.fillRect(x + width - 2, y + height / 2 - 2, 4, 4);
            g.fillRect(x + width / 2 - 2, y - 2, 4, 4);
            g.fillRect(x + width / 2 - 2, y + height - 2, 4, 4);
        }
    }

    @Override
    public boolean inBound(Point p) {
        return p.x > x && p.x < x + width && p.y > y && p.y < y + height;
    }

    public Shape createGhost() {
        return new GhostActivity(this);
    }

    @Override
    public String toString() {
        return hashCode() + " Activity{" +
                "x=" + x +
                ", y=" + y +
                ", name='" + name + '\'' +
                ", selected='" + selected + "'" +
                '}';
    }
}
