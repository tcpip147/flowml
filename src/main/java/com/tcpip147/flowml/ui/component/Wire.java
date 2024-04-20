package com.tcpip147.flowml.ui.component;

import com.tcpip147.flowml.ui.FmlColor;
import com.tcpip147.flowml.ui.core.PathFinder;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Wire extends Shape {

    public Activity source;
    public String sourceOut;
    public int sourceX;
    public Activity target;
    public String targetIn;
    public int targetX;
    public String transition;
    public List<Point> points = new ArrayList<>();

    public Wire(Activity source, String sourceOut, int sourceX, Activity target, String targetIn, int targetX) {
        this.source = source;
        this.sourceOut = sourceOut;
        this.sourceX = sourceX;
        this.target = target;
        this.targetIn = targetIn;
        this.targetX = targetX;
        refresh();
        setFrontLayerLevel(2);
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }

    public void setSource(Activity source) {
        this.source = source;
    }

    public void setSourceOut(String sourceOut) {
        this.sourceOut = sourceOut;
    }

    public void setSourceX(int sourceX) {
        this.sourceX = sourceX;
    }

    public void setTarget(Activity target) {
        this.target = target;
    }

    public void setTargetIn(String targetIn) {
        this.targetIn = targetIn;
    }

    public void setTargetX(int targetX) {
        this.targetX = targetX;
    }

    public void refresh() {
        Rectangle sourceRect = new Rectangle(source.x, source.y, source.width, source.height);
        Rectangle targetRect = new Rectangle(target.x, target.y, target.width, target.height);
        PathFinder pathFinder = new PathFinder(sourceRect, sourceOut, source.x + sourceX, targetRect, targetIn, target.x + targetX);
        points = pathFinder.find();
        if (points.size() > 1) {
            digestPoints();
            modifyLine();
            setOutlinePoint(points.get(0), sourceOut, sourceX, source);
            setOutlinePoint(points.get(points.size() - 1), targetIn, targetX, target);
        }
    }

    protected void digestPoints() {
        List<Point> digested = new ArrayList<>();
        digested.add(points.get(0));
        if (points.size() > 1) {
            digested.add(points.get(1));
        }
        for (int i = 2; i < points.size(); i++) {
            Point pPrev = points.get(i - 2);
            Point prev = points.get(i - 1);
            Point current = points.get(i);
            boolean noMoveX = pPrev.x == prev.x && pPrev.x == current.x;
            boolean noMoveY = pPrev.y == prev.y && pPrev.y == current.y;
            if (!noMoveX && !noMoveY) {
                digested.add(new Point(current));
            } else {
                digested.set(digested.size() - 1, current);
            }
        }
        points = digested;
    }

    protected void modifyLine() {
        Point center = new Point(((source.x + source.width / 2) + (target.x + target.width / 2)) / 2, ((source.y + source.height / 2) + (target.y + target.height / 2)) / 2);
        for (int i = 1; i < points.size(); i++) {
            Point p1 = points.get(i - 1);
            Point p2 = points.get(i);
            int minX = Math.min(Math.min(p1.x, p2.x), center.x);
            int maxX = Math.max(Math.max(p1.x, p2.x), center.x);
            int minY = Math.min(Math.min(p1.y, p2.y), center.y);
            int maxY = Math.max(Math.max(p1.y, p2.y), center.y);
            Rectangle rect = new Rectangle(minX, minY, maxX - minX, maxY - minY);
            if (!(isHitX(source, rect) && isHitY(source, rect)) && !(isHitX(target, rect) && isHitY(target, rect))) {
                if (p1.y == p2.y) {
                    p1.y = center.y;
                    p2.y = center.y;
                } else {
                    p1.x = center.x;
                    p2.x = center.x;
                }
            }
        }
    }

    private boolean isHitX(Activity activity, Rectangle rect) {
        int min = activity.x < rect.x ? activity.x : rect.x;
        int max = activity.x + activity.width > rect.x + rect.width ? activity.x + activity.width : rect.x + rect.width;
        if (max - min - activity.width - rect.width <= 0) {
            return true;
        }
        return false;
    }

    private boolean isHitY(Activity activity, Rectangle rect) {
        int min = activity.y < rect.y ? activity.y : rect.y;
        int max = activity.y + activity.height > rect.y + rect.height ? activity.y + activity.height : rect.y + rect.height;
        if (max - min - activity.height - rect.height <= 0) {
            return true;
        }
        return false;
    }

    protected void setOutlinePoint(Point point, String direction, int offset, Activity activity) {
        if ("N".equals(direction)) {
            point.x = activity.x + offset;
            point.y = activity.y;
        } else if ("E".equals(direction)) {
            point.x = activity.x + activity.width;
            point.y = activity.y + activity.height / 2;
        } else if ("S".equals(direction)) {
            point.x = activity.x + offset;
            point.y = activity.y + activity.height;
        } else if ("W".equals(direction)) {
            point.x = activity.x;
            point.y = activity.y + activity.height / 2;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        for (int i = 1; i < points.size(); i++) {
            Point current = points.get(i);
            Point prev = points.get(i - 1);
            if (selected) {
                g.setColor(FmlColor.WIRE_SELECTED);
            } else {
                g.setColor(FmlColor.WIRE_DEFAULT);
            }
            g.drawLine(current.x, current.y, prev.x, prev.y);
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

        if (selected) {
            if (points.size() > 1) {
                Point start = points.get(0);
                Point end = points.get(points.size() - 1);
                g.setColor(FmlColor.WIRE_SELECTION_MARK_OUTER);
                g.fillRect(start.x - 3, start.y - 3, 6, 6);
                g.fillRect(end.x - 3, end.y - 3, 6, 6);
                g.setColor(FmlColor.WIRE_SELECTION_MARK);
                g.fillRect(start.x - 2, start.y - 2, 4, 4);
                g.fillRect(end.x - 2, end.y - 2, 4, 4);
            }
        }

        if (points.size() > 1) {
            Point current = points.get(points.size() / 2);
            Point prev = points.get(points.size() / 2 - 1);
            FontMetrics metrics = g.getFontMetrics();
            int x = (prev.x + current.x - metrics.stringWidth(transition)) / 2;
            int y = (prev.y + current.y - metrics.getHeight()) / 2 + metrics.getAscent();
            g.setColor(FmlColor.DEFAULT);
            g.fillRect(x - 5, (prev.y + current.y - metrics.getHeight()) / 2, metrics.stringWidth(transition) + 10, metrics.getHeight());
            g.setColor(FmlColor.WIRE_DEFAULT);
            g.drawString(transition, x, y);
        }
    }

    @Override
    public boolean inBound(Point p) {
        for (int i = 1; i < points.size(); i++) {
            Point p1 = points.get(i - 1);
            Point p2 = points.get(i);
            int range = 10;
            if (p1.y == p2.y) {
                if (p1.x < p2.x) {
                    if (p.x > p1.x && p.x < p2.x && p.y > p1.y - range && p.y < p1.y + range) {
                        return true;
                    }
                } else {
                    if (p.x > p2.x && p.x < p1.x && p.y > p2.y - range && p.y < p2.y + range) {
                        return true;
                    }
                }
            } else {
                if (p1.y < p2.y) {
                    if (p.y > p1.y && p.y < p2.y && p.x > p1.x - range && p.x < p1.x + range) {
                        return true;
                    }
                } else {
                    if (p.y > p2.y && p.y < p1.y && p.x > p2.x - range && p.x < p2.x + range) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Shape createGhost() {
        return new GhostWire(this);
    }
}
