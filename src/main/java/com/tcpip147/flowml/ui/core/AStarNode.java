package com.tcpip147.flowml.ui.core;

public class AStarNode {

    public int x;
    public int y;
    public int c;
    public int r;
    public AStarNode parent;
    public int H;
    public int F;
    public AStarNode[] neighbors;
    public boolean solution;

    public AStarNode(AStarNode[][] grid, int c, int r, int x, int y) {
        this.x = x;
        this.y = y;
        this.c = c;
        this.r = r;
        int maxX = grid.length - 1;
        int maxY = grid[0].length - 1;

        if ((c == 0 && r == 0) || (c == 0 && r == maxY) || (c == maxX && r == 0) || (c == maxX && r == maxY)) {
            neighbors = new AStarNode[2];
        } else if (c == 0 || r == 0 || c == maxX || r == maxY) {
            neighbors = new AStarNode[3];
        } else {
            neighbors = new AStarNode[4];
        }
    }

    public void connect(AStarNode node) {
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] == null) {
                neighbors[i] = node;
                break;
            }
        }
        for (int i = 0; i < node.neighbors.length; i++) {
            if (node.neighbors[i] == null) {
                node.neighbors[i] = this;
                break;
            }
        }
    }

    public void dispose() {
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] != null) {
                for (int j = 0; j < neighbors[i].neighbors.length; j++) {
                    if (neighbors[i].neighbors[j] == this) {
                        neighbors[i].neighbors[j] = null;
                    }
                }
            }
        }
    }
}
