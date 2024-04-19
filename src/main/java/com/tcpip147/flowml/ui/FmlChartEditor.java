package com.tcpip147.flowml.ui;

import javax.swing.*;
import java.awt.*;

public class FmlChartEditor extends JPanel {

    private FmlContext ctx;

    public FmlChartEditor(FmlContext ctx) {
        setLayout(new BorderLayout());
        this.ctx = ctx;
        add(new FmlCanvas(ctx), BorderLayout.CENTER);
    }
}
