package com.tcpip147.flowml.ui;

import com.tcpip147.flowml.ui.component.*;
import com.tcpip147.flowml.ui.component.Shape;
import com.tcpip147.flowml.ui.context.MouseContext;

import java.awt.*;
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
                refreshDependentWires(activity);
            }
        }
        c.prevX = e.getX();
        c.prevY = e.getY();
    }

    public void createGhostActivity(int x, int y) {
        if (model.getGhostShapeList() == null) {
            model.setGhostShapeList(new ArrayList<>());
            Activity activity = new Activity("", x, y, FmlCanvas.GRID_SIZE * 4);
            model.add(activity);
            model.addGhost(activity.createGhost());
        }
    }

    public void releaseGhostShape() {
        if (model.getGhostShapeList() != null) {
            for (Shape shape : model.getGhostShapeList()) {
                GhostActivity ghostActivity = (GhostActivity) shape;
                model.getShapeList().remove(ghostActivity.activity);
            }
            model.setGhostShapeList(null);
        }
    }

    private void refreshDependentWires(Activity activity) {
        for (Shape shape : model.getShapeList()) {
            if (shape instanceof Wire) {
                Wire wire = (Wire) shape;
                if (wire.source == activity || wire.target == activity) {
                    wire.refresh();
                }
            }
        }
    }

    public void moveWireGhostShape(MouseContext c, MouseEvent e) {
        if (model.getGhostShapeList() == null) {
            model.setGhostShapeList(new ArrayList<>());
            for (Shape shape : model.getShapeList()) {
                if (shape.selected) {
                    if (shape instanceof Wire) {
                        Wire wire = (Wire) shape;
                        model.addGhost(wire.createGhost());
                    }
                }
            }
        }
        clearWireMarks();
        for (Shape shape : model.getGhostShapeList()) {
            if (shape instanceof GhostWire) {
                GhostWire ghostWire = (GhostWire) shape;
                Wire wire = ghostWire.wire;
                wire.setVisible(false);
                Activity anchor = c.wireMovePosition == 0 ? wire.target : wire.source;
                Activity activity = model.getActivityMarkedWire(e);
                if (activity != null) {
                    if (activity != anchor) {
                        activity.setShowWireMark(true);
                    }
                }

                if (activity != null && anchor != null) {
                    String direction;
                    if (e.getX() < activity.x + FmlCanvas.GRID_SIZE / 2) {
                        direction = "W";
                    } else if (e.getX() > activity.x + activity.width - FmlCanvas.GRID_SIZE / 2) {
                        direction = "E";
                    } else if (e.getY() < activity.y + activity.height / 2) {
                        direction = "N";
                    } else {
                        direction = "S";
                    }

                    int offsetX = ((e.getX() - activity.x + FmlCanvas.GRID_SIZE / 2) / FmlCanvas.GRID_SIZE) * FmlCanvas.GRID_SIZE;
                    if (offsetX > activity.width - FmlCanvas.GRID_SIZE / 2) {
                        direction = "E";
                    }
                    if (c.wireMovePosition == 0) {
                        ghostWire.sourceOut = direction;
                        ghostWire.sourceX = offsetX;
                        ghostWire.setSource(activity);
                        ghostWire.setTarget(anchor);
                    } else {
                        ghostWire.targetIn = direction;
                        ghostWire.targetX = offsetX;
                        ghostWire.setSource(anchor);
                        ghostWire.setTarget(activity);
                    }
                    ghostWire.refresh();
                    ghostWire.setShowArrow(true);
                } else {
                    ghostWire.points.clear();
                    if (c.wireMovePosition == 0) {
                        ghostWire.points.add(new Point(e.getX(), e.getY()));
                        ghostWire.points.add(getWirePoint(ghostWire.target, ghostWire.targetIn, ghostWire.targetX));
                    } else {
                        ghostWire.points.add(getWirePoint(ghostWire.source, ghostWire.sourceOut, ghostWire.sourceX));
                        ghostWire.points.add(new Point(e.getX(), e.getY()));
                    }
                    ghostWire.setShowArrow(false);
                }
            }
        }
    }

    private Point getWirePoint(Activity activity, String direction, int offsetX) {
        int outX = "W".equals(direction) ? activity.x : "E".equals(direction) ? activity.x + activity.width : activity.x + offsetX;
        int outY = "N".equals(direction) ? activity.y : "S".equals(direction) ? activity.y + activity.height : activity.y + activity.height / 2;
        return new Point(outX, outY);
    }

    public void clearWireMarks() {
        for (Shape shape : model.getShapeList()) {
            if (shape instanceof Activity) {
                Activity activity = (Activity) shape;
                if (activity.showWireMark) {
                    activity.setShowWireMark(false);
                }
            }
        }
    }

    public void clearSelected() {
        for (Shape shape : model.getShapeList()) {
            if (shape instanceof Activity) {
                Activity activity = (Activity) shape;
                if (activity.selected) {
                    activity.setSelected(false);
                }
            }
        }
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
        if (model.getGhostShapeList() != null) {
            for (Shape shape : model.getGhostShapeList()) {
                if (shape instanceof GhostActivity) {
                    GhostActivity ghostActivity = (GhostActivity) shape;
                    ghostActivity.activity.setX(ghostActivity.x);
                    ghostActivity.activity.setY(ghostActivity.y);
                    ghostActivity.activity.setWidth(ghostActivity.width);
                    ghostActivity.activity.setHeight(ghostActivity.height);
                    refreshDependentWires(ghostActivity.activity);
                } else if (shape instanceof GhostWire) {
                    GhostWire ghostWire = (GhostWire) shape;
                    ghostWire.wire.setSource(ghostWire.source);
                    ghostWire.wire.setSourceOut(ghostWire.sourceOut);
                    ghostWire.wire.setSourceX(ghostWire.sourceX);
                    ghostWire.wire.setTarget(ghostWire.target);
                    ghostWire.wire.setTargetIn(ghostWire.targetIn);
                    ghostWire.wire.setTargetX(ghostWire.targetX);
                    ghostWire.wire.setVisible(true);
                    if (ghostWire.wire.source.showWireMark) {
                        ghostWire.wire.source.setShowWireMark(false);
                    }
                    if (ghostWire.wire.target.showWireMark) {
                        ghostWire.wire.target.setShowWireMark(false);
                    }
                    ghostWire.wire.refresh();
                }
            }
            model.setGhostShapeList(null);
        }
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
                int unit = FmlCanvas.GRID_SIZE * 2;
                if (c.resizePosition == 0) {
                    int x = activity.x + (int) Math.round((e.getX() - c.originX) / (double) unit) * unit;
                    int width = activity.x + activity.width - x;
                    if (width > unit) {
                        ghostActivity.setX(x);
                        ghostActivity.setWidth(width);
                    }
                } else {
                    int width = activity.width + (int) Math.round((e.getX() - c.originX) / (double) unit) * unit;
                    if (width > unit) {
                        ghostActivity.setWidth(width);
                    }
                }
                ghostActivity.setFrontLayerLevel(10);
            }
        }
        c.prevX = e.getX();
        c.prevY = e.getY();
    }

    public void setShowWireMarkInBound(MouseEvent e) {
        Activity activity = model.getActivityMarkedWire(e);
        if (activity != null) {
            activity.setShowWireMark(true);
        } else {
            clearWireMarks();
        }
    }

    public void createGhostWire(MouseEvent e) {
        Activity activity = model.getActivityMarkedWire(e);
        if (activity != null) {
            String direction;
            if (e.getX() < activity.x + FmlCanvas.GRID_SIZE / 2) {
                direction = "W";
            } else if (e.getX() > activity.x + activity.width - FmlCanvas.GRID_SIZE / 2) {
                direction = "E";
            } else if (e.getY() < activity.y + activity.height / 2) {
                direction = "N";
            } else {
                direction = "S";
            }

            int offsetX = ((e.getX() - activity.x + FmlCanvas.GRID_SIZE / 2) / FmlCanvas.GRID_SIZE) * FmlCanvas.GRID_SIZE;
            if (offsetX > activity.width - FmlCanvas.GRID_SIZE / 2) {
                direction = "E";
            }

            if (model.getGhostShapeList() == null) {
                model.setGhostShapeList(new ArrayList<>());
                Wire wire = new Wire(activity, direction, offsetX, null, null, 0);
                model.add(wire);
                model.addGhost(wire.createGhost());
            }
        }
    }
}
