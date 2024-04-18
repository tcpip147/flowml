package com.tcpip147.flowml.ui;

import com.tcpip147.flowml.ui.component.Activity;
import com.tcpip147.flowml.ui.component.RangeSelection;
import com.tcpip147.flowml.ui.component.Shape;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FmlModel {

    private RangeSelection rangeSelection = new RangeSelection();
    private List<Shape> shapeList = new ArrayList<>();
    private List<Shape> ghostShapeList;
    

    public FmlModel() {
        add(rangeSelection);
    }

    public List<Shape> getShapeList() {
        return shapeList;
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

    public void setRangeSelection(RangeSelection rangeSelection) {
        this.rangeSelection = rangeSelection;
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

    public List<Shape> getDrawingShapeList() {
        List<Shape> drawingShapeList = new ArrayList<>();
        if (ghostShapeList != null) {
            drawingShapeList.addAll(ghostShapeList);
        }
        drawingShapeList.addAll(shapeList);
        drawingShapeList.sort(new Comparator<Shape>() {
            @Override
            public int compare(Shape o1, Shape o2) {
                return o1.frontLayerLevel - o2.frontLayerLevel;
            }
        });
        return drawingShapeList;
    }
}
