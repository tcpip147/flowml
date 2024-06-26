package com.tcpip147.flowml.ui;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.tcpip147.flowml.ui.component.Activity;
import com.tcpip147.flowml.ui.component.Shape;
import com.tcpip147.flowml.ui.component.Wire;
import com.tcpip147.flowml.ui.context.MouseContext;
import com.tcpip147.flowml.ui.state.SelectionState;
import com.tcpip147.flowml.util.FmlUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class FmlCanvas extends JPanel {

    public static final int GRID_SIZE = 30;
    private static final int DRAG_DETECT_DISTANCE = 30;
    private static final int RANGE_DETECT_DISTANCE = 50;

    private final FmlContext ctx;
    private final FmlModel model = new FmlModel();
    private final FmlController controller = new FmlController(model);
    private final MouseContext mouseContext = new MouseContext();
    private SelectionState state = SelectionState.SELECT_READY;

    public FmlCanvas(FmlContext ctx) {
        setLayout(null);
        this.ctx = ctx;
        ctx.setController(controller);
        loadFile();
        setFocusable(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (state == SelectionState.SELECT_READY) {
                        mouseContext.setUp(e);
                        if (model.isInResizableArea(e) > -1) {
                            state = SelectionState.RESIZE_READY;
                            mouseContext.resizePosition = model.isInResizableArea(e);
                        } else if (model.isInWireMovableArea(e) > -1) {
                            state = SelectionState.WIRE_MOVE_READY;
                            mouseContext.wireMovePosition = model.isInWireMovableArea(e);
                        } else {
                            Shape shape = model.getShapeInBound(e);
                            if (shape == null) {
                                controller.setVisibleRangeSelection(e, true);
                                state = SelectionState.RANGE_MODE;
                                repaint();
                            } else if (shape.selected) {
                                if (shape instanceof Activity) {
                                    state = SelectionState.DRAG_READY;
                                }
                            } else {
                                controller.selectShape(mouseContext, e);
                                repaint();
                                if (shape instanceof Activity) {
                                    state = SelectionState.DRAG_READY;
                                }
                            }
                        }
                    } else if (state == SelectionState.ADD_WIRE_READY) {
                        controller.createGhostWire(e);
                        mouseContext.wireMovePosition = -1;
                        state = SelectionState.ADD_WIRE_TARGET_READY;
                        repaint();
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    state = SelectionState.SELECT_READY;
                    ctx.getToggleActionManager().clickAction("Selection");
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (state == SelectionState.DRAG_READY) {
                        if (Math.pow(mouseContext.originX - e.getX(), 2) + Math.pow(mouseContext.originY - e.getY(), 2) > DRAG_DETECT_DISTANCE) {
                            state = SelectionState.DRAG_STARTED;
                        }
                    } else if (state == SelectionState.DRAG_STARTED) {
                        controller.moveGhostShape(mouseContext, e);
                        repaint();
                    } else if (state == SelectionState.RANGE_MODE) {
                        if (Math.pow(mouseContext.originX - e.getX(), 2) + Math.pow(mouseContext.originY - e.getY(), 2) > RANGE_DETECT_DISTANCE) {
                            setCursor(FmlCursor.CROSS_HAIR);
                            controller.resizeRangeSelection(mouseContext, e);
                            repaint();
                        }
                    } else if (state == SelectionState.RESIZE_READY) {
                        controller.resizeGhostShape(mouseContext, e);
                        repaint();
                        state = SelectionState.RESIZE_STARTED;
                    } else if (state == SelectionState.RESIZE_STARTED) {
                        controller.resizeGhostShape(mouseContext, e);
                        repaint();
                    } else if (state == SelectionState.WIRE_MOVE_READY) {
                        controller.moveWireGhostShape(mouseContext, e);
                        repaint();
                    } else if (state == SelectionState.ADD_WIRE_TARGET_READY) {
                        controller.moveWireGhostShape(mouseContext, e);
                        repaint();
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (state == SelectionState.DRAG_READY) {
                        controller.selectShape(mouseContext, e);
                        repaint();
                        state = SelectionState.SELECT_READY;
                        mouseContext.reset();
                    } else if (state == SelectionState.DRAG_STARTED) {
                        controller.adjustGhostShapeList();
                        repaint();
                        state = SelectionState.SELECT_READY;
                        mouseContext.reset();
                    } else if (state == SelectionState.RANGE_MODE) {
                        setCursor(FmlCursor.DEFAULT);
                        List<Shape> shapeList = model.getShapeListInRange();
                        controller.selectShapeInList(shapeList);
                        controller.setVisibleRangeSelection(e, false);
                        repaint();
                        state = SelectionState.SELECT_READY;
                        mouseContext.reset();
                    } else if (state == SelectionState.RESIZE_READY) {
                        state = SelectionState.SELECT_READY;
                        mouseContext.reset();
                    } else if (state == SelectionState.RESIZE_STARTED) {
                        controller.adjustGhostShapeList();
                        repaint();
                        state = SelectionState.SELECT_READY;
                        mouseContext.reset();
                    } else if (state == SelectionState.WIRE_MOVE_READY) {
                        controller.adjustGhostShapeList();
                        repaint();
                        state = SelectionState.SELECT_READY;
                        mouseContext.reset();
                    } else if (state == SelectionState.ADD_ACTIVITY_READY) {
                        controller.adjustGhostShapeList();
                        repaint();
                        state = SelectionState.SELECT_READY;
                        ctx.getToggleActionManager().clickAction("Selection");
                        mouseContext.reset();
                    } else if (state == SelectionState.ADD_WIRE_TARGET_READY) {
                        boolean success = controller.adjustGhostShapeList();
                        repaint();
                        if (success) {
                            state = SelectionState.SELECT_READY;
                            ctx.getToggleActionManager().clickAction("Selection");
                        } else {
                            state = SelectionState.ADD_WIRE_READY;
                        }
                        mouseContext.reset();
                    }
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (state == SelectionState.SELECT_READY) {
                    if (model.isInResizableArea(e) > -1) {
                        setCursor(FmlCursor.RESIZE_HORIZONTAL);
                    } else {
                        setCursor(FmlCursor.DEFAULT);
                    }
                } else if (state == SelectionState.ADD_ACTIVITY_READY) {
                    controller.moveGhostShape(mouseContext, e);
                    repaint();
                } else if (state == SelectionState.ADD_WIRE_READY) {
                    controller.setShowWireMarkInBound(e);
                    repaint();
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown()) {
                    mouseContext.isControlDown = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.isControlDown()) {
                    mouseContext.isControlDown = false;
                } else if (e.getKeyCode() == 127) {
                    if (state == SelectionState.SELECT_READY) {
                        controller.removeSelectedShape();
                        repaint();
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g1) {
        super.paintComponent(g1);
        Graphics2D g = (Graphics2D) g1;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int maxX = 0;
        int maxY = 0;
        int order = 0;
        for (Shape shape : model.getDrawingShapeList()) {
            if (shape.visible) {
                shape.draw(g);
                if (shape instanceof Activity) {
                    Activity activity = (Activity) shape;
                    maxX = Math.max(maxX, activity.x + activity.width);
                    maxY = Math.max(maxY, activity.y + activity.height);
                }
                shape.setRenderingOrder(order++);
            }
        }
        setPreferredSize(new Dimension(maxX, maxY));
        revalidate();
    }

    private void loadFile() {
        PsiFile psiFile = PsiManager.getInstance(ctx.getProject()).findFile(ctx.getFile());
        PsiElement xmlDocument = FmlUtils.findFirstChildByName(psiFile, "PsiElement(XML_DOCUMENT)");
        if (xmlDocument != null) {
            PsiElement flow = FmlUtils.findFirstChildByName(xmlDocument, "XmlTag:flow");
            if (flow != null) {
                List<PsiElement> activityList = FmlUtils.findChildrenByName(flow, "XmlTag:activity");
                for (PsiElement activity : activityList) {
                    String name = FmlUtils.getAttributeValue(activity, "name");
                    int x = Integer.parseInt(FmlUtils.getAttributeValue(activity, "x"));
                    int y = Integer.parseInt(FmlUtils.getAttributeValue(activity, "y"));
                    int width = Integer.parseInt(FmlUtils.getAttributeValue(activity, "width"));
                    controller.addShape(new Activity(name, x, y, width));
                }
            }

            flow = FmlUtils.findFirstChildByName(xmlDocument, "XmlTag:flow");
            if (flow != null) {
                List<PsiElement> wireList = FmlUtils.findChildrenByName(flow, "XmlTag:wire");
                for (PsiElement wire : wireList) {
                    String source = FmlUtils.getAttributeValue(wire, "source");
                    String target = FmlUtils.getAttributeValue(wire, "target");
                    String out = FmlUtils.getAttributeValue(wire, "out");
                    String in = FmlUtils.getAttributeValue(wire, "in");
                    int outx = Integer.parseInt(FmlUtils.getAttributeValue(wire, "outx"));
                    int inx = Integer.parseInt(FmlUtils.getAttributeValue(wire, "inx"));
                    Wire w = new Wire(model.getActivityByName(source), out, outx, model.getActivityByName(target), in, inx);
                    String transition = FmlUtils.getAttributeValue(wire, "transition");
                    if (transition != null) {
                        w.setTransition(transition);
                    }
                    controller.addShape(w);
                }
            }
        }
    }

    public void setState(SelectionState state) {
        this.state = state;
    }

    public FmlController getController() {
        return controller;
    }
}
