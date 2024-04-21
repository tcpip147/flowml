package com.tcpip147.flowml.ui;

import com.tcpip147.flowml.ui.component.Activity;
import com.tcpip147.flowml.ui.component.RangeSelection;
import com.tcpip147.flowml.ui.component.Shape;
import com.tcpip147.flowml.ui.component.Wire;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class FmlModel {

    private RangeSelection rangeSelection = new RangeSelection();
    private List<Shape> shapeList = new LinkedList<>();
    private List<Shape> ghostShapeList;


    public FmlModel() {
        add(rangeSelection);
    }

    public List<Shape> getShapeList() {
        return shapeList;
    }

    public void setShapeList(List<Shape> shapeList) {
        this.shapeList = shapeList;
    }

    public void add(Shape shape) {
        shapeList.add(shape);
    }

    public List<Shape> getGhostShapeList() {
        return ghostShapeList;
    }

    public void setGhostShapeList(List<Shape> ghostShapeList) {
        this.ghostShapeList = ghostShapeList;
    }

    public void addGhost(Shape shape) {
        ghostShapeList.add(shape);
    }

    public RangeSelection getRangeSelection() {
        return rangeSelection;
    }

    public Shape getShapeInBound(MouseEvent e) {
        List<Shape> list = shapeList.stream().filter(shape -> shape.inBound(e.getPoint())).toList();
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public List<Shape> getShapeListInRange() {
        List<Shape> list = new ArrayList<>();
        for (Shape shape : shapeList) {
            if (shape instanceof Activity) {
                Activity activity = (Activity) shape;
                if ((activity.x > rangeSelection.x && activity.x + activity.width < rangeSelection.x + rangeSelection.width) &&
                        (activity.y > rangeSelection.y && activity.y + activity.height < rangeSelection.y + rangeSelection.height)) {
                    list.add(activity);
                }
            }
        }
        return list;
    }

    public int isInResizableArea(MouseEvent e) {
        for (Shape shape : shapeList) {
            if (shape.selected) {
                if (shape instanceof Activity) {
                    Activity activity = (Activity) shape;
                    if (activity.x - 10 < e.getX() && activity.x + 10 > e.getX() &&
                            activity.y < e.getY() && activity.y + activity.height > e.getY()) {
                        return 0;
                    } else if (activity.x + activity.width - 10 < e.getX() && activity.x + activity.width + 10 > e.getX() &&
                            activity.y < e.getY() && activity.y + activity.height > e.getY()) {
                        return 1;
                    }
                }
            }
        }
        return -1;
    }

    public int isInWireMovableArea(MouseEvent e) {
        for (Shape shape : shapeList) {
            if (shape.selected) {
                if (shape instanceof Wire) {
                    Wire wire = (Wire) shape;
                    Point start = wire.points.get(0);
                    Point end = wire.points.get(wire.points.size() - 1);
                    if (start.x - 10 < e.getX() && start.x + 10 > e.getX() && start.y - 10 < e.getY() && start.y + 10 > e.getY()) {
                        return 0;
                    } else if (end.x - 10 < e.getX() && end.x + 10 > e.getX() && end.y - 10 < e.getY() && end.y + 10 > e.getY()) {
                        return 1;
                    }
                }
            }
        }
        return -1;
    }

    public List<Shape> getDrawingShapeList() {
        List<Shape> drawingShapeList = new ArrayList<>();
        if (ghostShapeList != null) {
            drawingShapeList.addAll(ghostShapeList);
        }
        drawingShapeList.addAll(shapeList);
        drawingShapeList.sort(new Comparator<Shape>() {
            @Override
            public int compare(Shape o1, Shape o2) {
                if (o1.selected) {
                    return 1;
                } else if (o2.selected) {
                    return -1;
                } else if (o1.renderingOrder > 0 || o2.renderingOrder > 0) {
                    return o1.renderingOrder - o2.renderingOrder;
                }
                return o1.frontLayerLevel - o2.frontLayerLevel;
            }
        });
        return drawingShapeList;
    }

    public Activity getActivityByName(String name) {
        for (Shape shape : shapeList) {
            if (shape instanceof Activity) {
                Activity activity = (Activity) shape;
                if (name.equals(activity.name)) {
                    return activity;
                }
            }
        }
        return null;
    }

    public Activity getActivityMarkedWire(MouseEvent e) {
        for (Shape shape : shapeList) {
            if (shape instanceof Activity) {
                Activity activity = (Activity) shape;
                if (e.getX() > activity.x - 5 && e.getX() < activity.x + activity.width + 5 && e.getY() > activity.y - 5 && e.getY() < activity.y + activity.height + 5) {
                    return activity;
                }
            }
        }
        return null;
    }

    public Set<Wire> getConnectedWireList(Activity activity) {
        Set<Wire> wireList = new HashSet<>();
        for (Shape shape : shapeList) {
            if (shape instanceof Wire) {
                Wire wire = (Wire) shape;
                if (wire.source == activity || wire.target == activity) {
                    wireList.add(wire);
                }
            }
        }
        return wireList;
    }
}
