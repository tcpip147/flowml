package com.tcpip147.flowml.ui.context;

import java.awt.event.MouseEvent;

public class MouseContext implements Context {

    public int originX;
    public int originY;
    public int prevX;
    public int prevY;
    public boolean isControlDown;
    public int resizePosition;

    @Override
    public void reset() {
        originX = 0;
        originY = 0;
        prevX = 0;
        prevY = 0;
        isControlDown = false;
        resizePosition = 0;
    }

    public void setUp(MouseEvent e) {
        originX = e.getX();
        originY = e.getY();
        prevX = e.getX();
        prevY = e.getY();
    }
}
