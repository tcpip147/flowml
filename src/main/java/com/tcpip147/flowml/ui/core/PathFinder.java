package com.tcpip147.flowml.ui.core;

import com.tcpip147.flowml.ui.component.Activity;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PathFinder {

    private static final int MARGIN = 15;
    private static final int BASE_COST = 10;
    private static final int TURNING_COST = 20;

    private ARectangle sourceRect;
    private ARectangle targetRect;
    private ARectangle sourceOutRect;
    private ARectangle targetOutRect;
    private Point center;
    private String sourceOut;
    private int sourceX;
    private String targetIn;
    private int targetX;
    private Point start;
    private Point end;
    private AStarGraph graph;
    private AStarNode startNode;
    private AStarNode endNode;
    private PriorityQueue<AStarNode> openNodes;
    private boolean[][] closedNodes;

    public PathFinder(Activity source, String sourceOut, int sourceX, Activity target, String targetIn, int targetX) {
        this.sourceOut = sourceOut;
        this.sourceX = sourceX;
        this.targetIn = targetIn;
        this.targetX = targetX;
        sourceRect = new ARectangle(source.x, source.y, source.width, source.height);
        targetRect = new ARectangle(target.x, target.y, target.width, target.height);
        sourceOutRect = new ARectangle(source.x - MARGIN, source.y - MARGIN, source.width + 2 * MARGIN, source.height + 2 * MARGIN);
        targetOutRect = new ARectangle(target.x - MARGIN, target.y - MARGIN, target.width + 2 * MARGIN, target.height + 2 * MARGIN);
        center = new Point((sourceRect.center + targetRect.center) / 2, (sourceRect.middle + targetRect.middle) / 2);
    }

    public List<Point> find() {
        List<Point> path = new ArrayList<>();
        if (AStarUtils.isHitX(sourceRect, targetRect) && AStarUtils.isHitY(sourceRect, targetRect)) {
            return path;
        }

        reduceOuterOffset();
        setStartAndEndPoint();
        createGraph();

        openNodes.add(startNode);
        AStarNode current;
        while (true) {
            current = openNodes.poll();
            if (current == null) {
                break;
            }
            closedNodes[current.c][current.r] = true;
            if (current == endNode) {
                break;
            }

            for (int i = 0; i < current.neighbors.length; i++) {
                updateCostIfNeeded(current, current.neighbors[i], current.F + BASE_COST);
            }
        }

        current = endNode;
        while (current.parent != null) {
            current.solution = true;
            path.add(new Point(current.x, current.y));
            current = current.parent;
        }
        path.add(new Point(startNode.x, startNode.y));
        Collections.reverse(path);

        return path;
    }

    private void setStartAndEndPoint() {
        if ("W".equals(sourceOut) || "E".equals(sourceOut)) {
            start = new Point(sourceRect.center, sourceRect.middle);
        } else {
            start = new Point(sourceX, sourceRect.middle);
        }
        if ("W".equals(targetIn) || "E".equals(targetIn)) {
            end = new Point(targetRect.center, targetRect.middle);
        } else {
            end = new Point(targetX, targetRect.middle);
        }
    }

    private void reduceOuterOffset() {
        if (AStarUtils.isHitX(sourceOutRect, targetOutRect) && !AStarUtils.isHitX(sourceRect, targetRect)) {
            if (sourceOutRect.center < targetOutRect.center) {
                sourceOutRect.right = center.x;
                sourceOutRect.width = sourceOutRect.right - sourceOutRect.left;
                targetOutRect.left = center.x;
            } else {
                targetOutRect.right = center.x;
                targetOutRect.width = targetOutRect.right - targetOutRect.left;
                sourceOutRect.left = center.x;
            }
        }
        if (AStarUtils.isHitY(sourceOutRect, targetOutRect) && !AStarUtils.isHitY(sourceRect, targetRect)) {
            if (sourceOutRect.middle < targetOutRect.middle) {
                sourceOutRect.bottom = center.y;
                sourceOutRect.height = sourceOutRect.bottom - sourceOutRect.top;
                targetOutRect.top = center.y;
            } else {
                targetOutRect.bottom = center.y;
                targetOutRect.height = sourceOutRect.bottom - sourceOutRect.top;
                sourceOutRect.top = center.y;
            }
        }
    }

    private void createGraph() {
        int[] posX = Arrays.stream(new int[]{sourceOutRect.left, start.x, sourceOutRect.right, center.x, targetOutRect.left, end.x, targetOutRect.right}).distinct().sorted().toArray();
        int[] posY = Arrays.stream(new int[]{sourceOutRect.top, start.y, sourceOutRect.bottom, center.y, targetOutRect.top, end.y, targetOutRect.bottom}).distinct().sorted().toArray();
        graph = new AStarGraph(posX, posY);
        openNodes = new PriorityQueue<>((c1, c2) -> c1.F < c2.F ? -1 : c1.F > c2.F ? 1 : 0);
        closedNodes = new boolean[posX.length][posY.length];

        int outX = "W".equals(sourceOut) ? sourceOutRect.left : "E".equals(sourceOut) ? sourceOutRect.right : start.x;
        int outY = "N".equals(sourceOut) ? sourceOutRect.top : "S".equals(sourceOut) ? sourceOutRect.bottom : sourceOutRect.middle;
        int inX = "W".equals(targetIn) ? targetOutRect.left : "E".equals(targetIn) ? targetOutRect.right : end.x;
        int inY = "N".equals(targetIn) ? targetOutRect.top : "S".equals(targetIn) ? targetOutRect.bottom : targetOutRect.middle;
        int startC = -1;
        int startR = -1;
        int endC = -1;
        int endR = -1;
        int outC = -1;
        int outR = -1;
        int inC = -1;
        int inR = -1;

        for (int c = 0; c < graph.grid.length; c++) {
            for (int r = 0; r < graph.grid[c].length; r++) {
                if (graph.grid[c][r].x == start.x && graph.grid[c][r].y == start.y) {
                    startC = c;
                    startR = r;
                }
                if (graph.grid[c][r].x == end.x && graph.grid[c][r].y == end.y) {
                    endC = c;
                    endR = r;
                }
                if (graph.grid[c][r].x == outX && graph.grid[c][r].y == outY) {
                    outC = c;
                    outR = r;
                }
                if (graph.grid[c][r].x == inX && graph.grid[c][r].y == inY) {
                    inC = c;
                    inR = r;
                }
                if (AStarUtils.isInBound(graph.grid[c][r].x, graph.grid[c][r].y, sourceOutRect) || AStarUtils.isInBound(graph.grid[c][r].x, graph.grid[c][r].y, targetOutRect)) {
                    graph.grid[c][r].dispose();
                    graph.grid[c][r] = null;
                }
            }
        }

        startNode = new AStarNode(graph.grid, startC, startR, start.x, start.y);
        startNode.F = 0;
        graph.grid[startNode.c][startNode.r] = startNode;
        startNode.connect(graph.grid[outC][outR]);
        endNode = new AStarNode(graph.grid, endC, endR, end.x, end.y);
        graph.grid[endNode.c][endNode.r] = endNode;
        endNode.connect(graph.grid[inC][inR]);

        for (int c = 0; c < graph.grid.length; c++) {
            for (int r = 0; r < graph.grid[c].length; r++) {
                if (graph.grid[c][r] != null) {
                    graph.grid[c][r].H = Math.abs(c - endC) + Math.abs(r - endR);
                }
            }
        }
    }

    private void updateCostIfNeeded(AStarNode current, AStarNode next, int cost) {
        if (next == null || closedNodes[next.c][next.r]) {
            return;
        }

        int T = 0;
        if (current.parent != null) {
            boolean noMoveX = current.parent.c == current.c && current.parent.c == next.c;
            boolean noMoveY = current.parent.r == current.r && current.parent.r == next.r;
            if (!noMoveX && !noMoveY) {
                T = TURNING_COST;
            }
        }

        int F = next.H + cost + T;
        boolean isOpen = openNodes.contains(next);

        if (!isOpen || F < next.F) {
            next.F = F;
            next.parent = current;
            if (!isOpen) {
                openNodes.add(next);
            }
        }
    }
}
