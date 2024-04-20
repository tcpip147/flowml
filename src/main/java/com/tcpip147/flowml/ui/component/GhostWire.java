package com.tcpip147.flowml.ui.component;

import com.tcpip147.flowml.ui.FmlColor;

import java.awt.*;

public class GhostWire extends Wire {

    public Wire wire;
    public boolean showArrow = true;

    public GhostWire(Wire wire) {
        super(wire.source, wire.sourceOut, wire.sourceX, wire.target, wire.targetIn, wire.targetX);
        this.wire = wire;
    }

    public void setShowArrow(boolean showArrow) {
        this.showArrow = showArrow;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(FmlColor.GHOST_DEFAULT);
        for (int i = 1; i < points.size(); i++) {
            Point current = points.get(i);
            Point prev = points.get(i - 1);
            g.drawLine(current.x, current.y, prev.x, prev.y);
            if (showArrow) {
                if (i == points.size() - 1) {
                    if ("N".equals(targetIn)) {
                        g.fillPolygon(new int[]{current.x - 5, current.x, current.x + 5}, new int[]{current.y - 7, current.y, current.y - 7}, 3);
                    } else if ("E".equals(targetIn)) {
                        g.fillPolygon(new int[]{current.x + 7, current.x, current.x + 7}, new int[]{current.y - 5, current.y, current.y + 5}, 3);
                    } else if ("S".equals(targetIn)) {
                        g.fillPolygon(new int[]{current.x - 5, current.x, current.x + 5}, new int[]{current.y - 7, current.y, current.y + 7}, 3);
                    } else {
                        g.fillPolygon(new int[]{current.x - 7, current.x, current.x - 7}, new int[]{current.y - 5, current.y, current.y + 5}, 3);
                    }
                }
            }
        }
    }
}
