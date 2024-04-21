package com.tcpip147.flowml.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class FmlToggleAction extends ToggleAction {

    private ToggleActionManager observer;
    protected boolean selected;

    public FmlToggleAction(String text, String description, Icon icon, ToggleActionManager observer) {
        super(text, description, icon);
        this.observer = observer;
        observer.addAction(this);
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        if (state) {
            observer.notifySelected(this);
            setSelected(true);
        }
    }
}
