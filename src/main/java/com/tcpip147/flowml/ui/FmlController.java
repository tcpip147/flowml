package com.tcpip147.flowml.ui;

import com.tcpip147.flowml.ui.component.Activity;
import com.tcpip147.flowml.ui.component.GhostActivity;
import com.tcpip147.flowml.ui.component.RangeSelection;
import com.tcpip147.flowml.ui.component.Shape;
import com.tcpip147.flowml.ui.context.MouseContext;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class FmlController {

    private final FmlModel model;

    public FmlController(FmlModel model) {
        this.model = model;
    }

    public void addShape(Shape shape) {
        model.add(shape);
    }

    public void selectShape(MouseContext c, MouseEvent e) {
        for (Shape shape : model.getShapeList()) {
            if (shape.inBound(e.getPoint())) {
                if (!shape.selected) {
                    shape.setSelected(true);
                }
            } else {
                if (shape.selected) {
                    if (!c.isControlDown) {
                        shape.setSelected(false);
                    }
                }
            }
        }
    }

    public void selectShapeInList(List<Shape> shapeList) {
        for (Shape shape : model.getShapeList()) {
            if (shapeList.contains(shape)) {
                if (!shape.selected) {
                    shape.setSelected(true);
                }
            } else {
                if (shape.selected) {
                    shape.setSelected(false);
                }
            }
        }
    }

    public void moveGhostShape(MouseContext c, MouseEvent e) {
        int diffX = e.getX() - c.prevX;
        int diffY = e.getY() - c.prevY;
        if (model.getGhostShapeList() == null) {
            model.setGhostShapeList(new ArrayList<>());
            for (Shape shape : model.getShapeList()) {
                if (shape.selected) {
                    if (shape instanceof Activity) {
                        Activity activity = (Activity) shape;
                        model.addGhost(activity.createGhost());
                    }
                }
            }
        }
        for (Shape shape : model.getGhostShapeList()) {
            if (shape instanceof GhostActivity) {
                GhostActivity ghostActivity = (GhostActivity) shape;
                Activity activity = ghostActivity.activity;
                activity.setX(activity.x + diffX);
                activity.setY(activity.y + diffY);
                ghostActivity.setX((int) Math.round((activity.x + diffX) / (double) FmlCanvas.GRID_SIZE) * FmlCanvas.GRID_SIZE);
                ghostActivity.setY((int) Math.round((activity.y + diffY) / (double) FmlCanvas.GRID_SIZE) * FmlCanvas.GRID_SIZE);
            }
        }
        c.prevX = e.getX();
        c.prevY = e.getY();
    }

    public void setVisibleRangeSelection(MouseEvent e, boolean visible) {
        RangeSelection rangeSelection = model.getRangeSelection();
        if (visible) {
            rangeSelection.setX(e.getX());
            rangeSelection.setY(e.getY());
        } else {
            rangeSelection.setX(-1);
            rangeSelection.setY(-1);
        }
        rangeSelection.setWidth(0);
        rangeSelection.setHeight(0);
        rangeSelection.setVisible(visible);
    }

    public void resizeRangeSelection(MouseContext c, MouseEvent e) {
        int diffX = e.getX() - c.prevX;
        int diffY = e.getY() - c.prevY;
        RangeSelection rangeSelection = model.getRangeSelection();

        if (e.getX() < c.originX) {
            rangeSelection.setX(e.getX());
            rangeSelection.setWidth(c.originX - e.getX());
        } else {
            rangeSelection.setWidth(rangeSelection.width + diffX);
        }

        if (e.getY() < c.originY) {
            rangeSelection.setY(e.getY());
            rangeSelection.setHeight(c.originY - e.getY());
        } else {
            rangeSelection.setHeight(rangeSelection.height + diffY);
        }
        c.prevX = e.getX();
        c.prevY = e.getY();
    }

    public void adjustGhostShapeList() {
        for (Shape shape : model.getGhostShapeList()) {
            if (shape instanceof GhostActivity) {
                GhostActivity ghostActivity = (GhostActivity) shape;
                ghostActivity.activity.setX(ghostActivity.x);
                ghostActivity.activity.setY(ghostActivity.y);
                ghostActivity.activity.setWidth(ghostActivity.width);
                ghostActivity.activity.setHeight(ghostActivity.height);
            }
        }
        model.setGhostShapeList(null);
    }

    public boolean isInResizableArea(MouseEvent e) {
        for (Shape shape : model.getShapeList()) {
            if (shape.selected) {
                if (shape instanceof Activity) {
                    Activity activity = (Activity) shape;
                    if ((activity.x - 10 < e.getX() && activity.x + 10 > e.getX() &&
                            activity.y < e.getY() && activity.y + activity.height > e.getY()) ||
                            (activity.x + activity.width - 10 < e.getX() && activity.x + activity.width + 10 > e.getX() &&
                                    activity.y < e.getY() && activity.y + activity.height > e.getY())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void resizeGhostShape(MouseContext c, MouseEvent e) {
        if (model.getGhostShapeList() == null) {
            model.setGhostShapeList(new ArrayList<>());
            for (Shape shape : model.getShapeList()) {
                if (shape.selected) {
                    if (shape instanceof Activity) {
                        Activity activity = (Activity) shape;
                        model.addGhost(activity.createGhost());
                    }
                }
            }
        }
        for (Shape shape : model.getGhostShapeList()) {
            if (shape instanceof GhostActivity) {
                GhostActivity ghostActivity = (GhostActivity) shape;
                Activity activity = ghostActivity.activity;
                ghostActivity.setWidth((int) Math.round((activity.width - c.originX + e.getX()) / (double) FmlCanvas.GRID_SIZE) * FmlCanvas.GRID_SIZE);
                ghostActivity.setFrontLayerLevel(1);
            }
        }
        c.prevX = e.getX();
        c.prevY = e.getY();
    }
}
