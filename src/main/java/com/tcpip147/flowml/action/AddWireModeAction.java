package com.tcpip147.flowml.action;

import com.intellij.icons.AllIcons;
import com.tcpip147.flowml.ui.FmlContext;
import com.tcpip147.flowml.ui.state.SelectionState;

public class AddWireModeAction extends FmlToggleAction {

    private FmlContext ctx;

    public AddWireModeAction(FmlContext ctx, ToggleActionManager observer) {
        super("Add Wire", "Add Wire", AllIcons.Vcs.CommitNode, observer);
        this.ctx = ctx;
    }

    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            super.setSelected(selected);
            if (selected) {
                ctx.getCanvas().setState(SelectionState.ADD_WIRE_READY);
            } else {
                ctx.getController().clearWireMarks();
                ctx.getCanvas().repaint();
            }
        }
    }
}
