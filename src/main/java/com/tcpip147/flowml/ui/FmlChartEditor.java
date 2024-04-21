package com.tcpip147.flowml.ui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.SideBorder;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.tcpip147.flowml.action.AddActivityModeAction;
import com.tcpip147.flowml.action.AddWireModeAction;
import com.tcpip147.flowml.action.SelectionModeAction;
import com.tcpip147.flowml.action.ToggleObserver;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;

public class FmlChartEditor extends JPanel {

    private FmlContext ctx;

    public FmlChartEditor(FmlContext ctx) {
        setLayout(new BorderLayout());
        this.ctx = ctx;
        FmlCanvas canvas = new FmlCanvas(ctx);
        ctx.setCanvas(canvas);
        createToolbarPanel();
        add(new JBScrollPane(canvas), BorderLayout.CENTER);
    }

    private void createToolbarPanel() {
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        toolbarPanel.setBorder(new CompoundBorder(IdeBorderFactory.createBorder(SideBorder.BOTTOM), IdeBorderFactory.createEmptyBorder(JBUI.insets(1))));

        ActionManager actionManager = ActionManager.getInstance();

        DefaultActionGroup group1 = new DefaultActionGroup();
        ToggleObserver observer = new ToggleObserver();
        SelectionModeAction selectionModeAction = new SelectionModeAction(ctx, observer);
        group1.add(selectionModeAction);
        group1.add(new AddActivityModeAction(ctx, observer));
        group1.add(new AddWireModeAction(ctx, observer));
        selectionModeAction.setSelected(true);

        ActionToolbar toolbar1 = actionManager.createActionToolbar("FlowMLToolbarGroup1", group1, true);
        toolbar1.setLayoutPolicy(ActionToolbar.WRAP_LAYOUT_POLICY);
        toolbar1.setTargetComponent(toolbarPanel);
        toolbarPanel.add(toolbar1.getComponent(), BorderLayout.CENTER);

        add(toolbarPanel, BorderLayout.NORTH);
    }
}
