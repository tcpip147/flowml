package com.tcpip147.flowml.ui.core;

public class ARectangle {

    public int left;
    public int center;
    public int right;
    public int top;
    public int middle;
    public int bottom;
    public int width;
    public int height;

    public ARectangle(int x, int y, int width, int height) {
        this.left = x;
        this.center = x + width / 2;
        this.right = x + width;
        this.top = y;
        this.middle = y + height / 2;
        this.bottom = y + height;
        this.width = width;
        this.height = height;
    }
}
