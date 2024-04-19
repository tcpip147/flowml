package com.tcpip147.flowml.ui.core;

public class AStarUtils {

    public static boolean isHitX(ARectangle a, ARectangle b) {
        int min = a.left < b.left ? a.left : b.left;
        int max = a.right > b.right ? a.right : b.right;
        if (max - min - a.width - b.width <= 0) {
            return true;
        }
        return false;
    }

    public static boolean isHitY(ARectangle a, ARectangle b) {
        int min = a.top < b.top ? a.top : b.top;
        int max = a.bottom > b.bottom ? a.bottom : b.bottom;
        if (max - min - a.height - b.height <= 0) {
            return true;
        }
        return false;
    }

    public static boolean isInBound(int x, int y, ARectangle rect) {
        return x > rect.left && x < rect.right && y > rect.top && y < rect.bottom;
    }
}
