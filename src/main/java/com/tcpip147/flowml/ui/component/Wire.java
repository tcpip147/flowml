package com.tcpip147.flowml.ui.component;

import com.tcpip147.flowml.ui.FmlColor;
import com.tcpip147.flowml.ui.core.PathFinder;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class Wire extends Shape {

    public Activity source;
    public String sourceOut;
    public int sourceX;
    public Activity target;
    public String targetIn;
    public int targetX;
    public List<Point> points = new ArrayList<>();

    public Wire(Activity source, String sourceOut, int sourceX, Activity target, String targetIn, int targetX) {
        this.source = source;
        this.sourceOut = sourceOut;
        this.sourceX = sourceX;
        this.target = target;
        this.targetIn = targetIn;
        this.targetX = targetX;
        refresh();
    }

    public void refresh() {
        PathFinder pathFinder = new PathFinder(source, sourceOut, source.x + sourceX, target, targetIn, target.x + targetX);
        points = pathFinder.find();
        if (points.size() > 1) {
            digestPoints();
            modifyLine();
            setOutlinePoint(points.get(0), sourceOut, sourceX, source);
            setOutlinePoint(points.get(points.size() - 1), targetIn, targetX, target);
        }
    }

    private void digestPoints() {
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

    private void modifyLine() {
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

    private void setOutlinePoint(Point point, String direction, int offset, Activity activity) {
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
        g.setColor(FmlColor.WIRE_DEFAULT);
        for (int i = 1; i < points.size(); i++) {
            Point current = points.get(i);
            Point prev = points.get(i - 1);
            if (selected) {
                g.setColor(FmlColor.WIRE_SELECTED);
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
}
