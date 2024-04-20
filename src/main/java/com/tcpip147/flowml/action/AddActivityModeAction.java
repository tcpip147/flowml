package com.tcpip147.flowml.action;

import com.intellij.icons.AllIcons;
import com.tcpip147.flowml.ui.FmlContext;
import com.tcpip147.flowml.ui.state.SelectionState;

public class AddActivityModeAction extends FmlToggleAction {

    private FmlContext ctx;

    public AddActivityModeAction(FmlContext ctx, ToggleObserver observer) {
        super("Add Activity", "Add Activity", AllIcons.General.Add, observer);
        this.ctx = ctx;
    }

    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            ctx.getCanvas().setState(SelectionState.ADD_ACTIVITY_READY);
            ctx.getController().createGhostActivity(0, 0);
        } else {
            ctx.getController().releaseGhostShape();
            ctx.getCanvas().repaint();
        }
    }
}
