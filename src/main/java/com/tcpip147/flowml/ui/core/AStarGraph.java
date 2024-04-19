package com.tcpip147.flowml.ui.core;

public class AStarGraph {

    public AStarNode[][] grid;

    public AStarGraph(int[] posX, int[] posY) {
        grid = new AStarNode[posX.length][posY.length];
        for (int c = 0; c < grid.length; c++) {
            for (int r = 0; r < grid[c].length; r++) {
                grid[c][r] = new AStarNode(grid, c, r, posX[c], posY[r]);
                if (c > 0) {
                    grid[c][r].connect(grid[c - 1][r]);
                }
                if (r > 0) {
                    grid[c][r].connect(grid[c][r - 1]);
                }
            }
        }
    }
}
