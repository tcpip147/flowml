package com.tcpip147.flowml.ui;

import com.tcpip147.flowml.ui.component.Activity;
import com.tcpip147.flowml.ui.component.Shape;
import com.tcpip147.flowml.ui.context.MouseContext;
import com.tcpip147.flowml.ui.state.SelectionState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class FmlCanvas extends JPanel {

    public static final int GRID_SIZE = 15;

    private final FmlModel model = new FmlModel();
    private final FmlController controller = new FmlController(model);
    private final MouseContext mouseContext = new MouseContext();
    private SelectionState state = SelectionState.SELECT_READY;

    public FmlCanvas() {
        setLayout(null);

        Activity activity = new Activity(100, 100, 100, 30);
        controller.addShape(activity);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (state == SelectionState.SELECT_READY) {
                    mouseContext.setUp(e);
                    if (controller.isInResizableArea(e)) {
                        state = SelectionState.RESIZE_READY;
                    } else {
                        Shape shape = model.getShapeInBound(e);
                        if (shape == null) {
                            controller.setVisibleRangeSelection(e, true);
                            setCursor(FmlCursor.CROSS_HAIR);
                            state = SelectionState.RANGE_MODE;
                            repaint();
                        } else if (shape.selected) {
                            state = SelectionState.DRAG_READY;
                        } else {
                            controller.selectShape(mouseContext, e);
                            state = SelectionState.DRAG_READY;
                            repaint();
                        }
                    }
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (state == SelectionState.DRAG_READY) {
                    if (Math.pow(mouseContext.originX - e.getX(), 2) + Math.pow(mouseContext.originY - e.getY(), 2) > 100) {
                        state = SelectionState.DRAG_STARTED;
                    }
                } else if (state == SelectionState.DRAG_STARTED) {
                    controller.moveGhostShape(mouseContext, e);
                    repaint();
                } else if (state == SelectionState.RANGE_MODE) {
                    controller.resizeRangeSelection(mouseContext, e);
                    repaint();
                } else if (state == SelectionState.RESIZE_READY) {
                    controller.resizeGhostShape(mouseContext, e);
                    repaint();
                    state = SelectionState.RESIZE_STARTED;
                } else if (state == SelectionState.RESIZE_STARTED) {
                    controller.resizeGhostShape(mouseContext, e);
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (state == SelectionState.DRAG_READY) {
                    controller.selectShape(mouseContext, e);
                    repaint();
                    state = SelectionState.SELECT_READY;
                } else if (state == SelectionState.DRAG_STARTED) {
                    controller.adjustGhostShapeList();
                    repaint();
                    state = SelectionState.SELECT_READY;
                } else if (state == SelectionState.RANGE_MODE) {
                    setCursor(FmlCursor.DEFAULT);
                    List<Shape> shapeList = model.getShapeListInRange();
                    controller.selectShapeInList(shapeList);
                    controller.setVisibleRangeSelection(e, false);
                    repaint();
                    state = SelectionState.SELECT_READY;
                } else if (state == SelectionState.RESIZE_READY) {
                    state = SelectionState.SELECT_READY;
                } else if (state == SelectionState.RESIZE_STARTED) {
                    controller.adjustGhostShapeList();
                    repaint();
                    state = SelectionState.SELECT_READY;
                }
                mouseContext.reset();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (state == SelectionState.SELECT_READY) {
                    if (controller.isInResizableArea(e)) {
                        setCursor(FmlCursor.RESIZE_HORIZONTAL);
                    } else {
                        setCursor(FmlCursor.DEFAULT);
                    }
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 17) {
                    mouseContext.isControlDown = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 17) {
                    mouseContext.isControlDown = false;
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g1) {
        super.paintComponent(g1);
        Graphics2D g = (Graphics2D) g1;
        if (model.getGhostShapeList() != null) {
            for (Shape shape : model.getGhostShapeList()) {
                if (shape.visible) {
                    shape.draw(g);
                }
            }
        }
        for (Shape shape : model.getShapeList()) {
            if (shape.visible) {
                shape.draw(g);
            }
        }
    }
}
