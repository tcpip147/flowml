package com.tcpip147.flowml.action;

import com.intellij.icons.AllIcons;
import com.tcpip147.flowml.ui.FmlContext;
import com.tcpip147.flowml.ui.state.SelectionState;

public class AddActivityModeAction extends FmlToggleAction {

    private FmlContext ctx;

    public AddActivityModeAction(FmlContext ctx, ToggleActionManager observer) {
        super("Add Activity", "Add Activity", AllIcons.General.Add, observer);
        this.ctx = ctx;
    }

    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            super.setSelected(selected);
            if (selected) {
                ctx.getCanvas().setState(SelectionState.ADD_ACTIVITY_READY);
                ctx.getController().createGhostActivity(0, 0);
                ctx.getCanvas().repaint();
            } else {
                ctx.getController().releaseGhostShape();
                ctx.getCanvas().repaint();
            }
        }
    }
}
