package com.tcpip147.flowml.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import com.tcpip147.flowml.plugin.FmlFileType;
import com.tcpip147.flowml.ui.FmlContext;
import com.tcpip147.flowml.ui.state.SelectionState;

public class SelectionModeAction extends FmlToggleAction {

    private FmlContext ctx;

    public SelectionModeAction(FmlContext ctx, ToggleObserver observer) {
        super("Selection", "Selection", IconLoader.getIcon("/pointer.svg", FmlFileType.class), observer);
        this.ctx = ctx;
    }

    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            super.setSelected(selected);
            if (selected) {
                ctx.getCanvas().setState(SelectionState.SELECT_READY);
            } else {
                ctx.getController().clearSelected();
                ctx.getCanvas().repaint();
            }
        }
    }
}
