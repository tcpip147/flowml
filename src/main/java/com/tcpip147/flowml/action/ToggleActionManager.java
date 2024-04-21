package com.tcpip147.flowml.action;

import com.intellij.openapi.actionSystem.ToggleAction;

import java.util.ArrayList;
import java.util.List;

public class ToggleActionManager {

    public List<FmlToggleAction> actionList = new ArrayList<>();

    public void addAction(FmlToggleAction action) {
        actionList.add(action);
    }

    public void clickAction(String name) {
        for (FmlToggleAction action : actionList) {
            if (name.equals(action.getTemplateText())) {
                notifySelected(action);
            }
        }
    }

    public void notifySelected(FmlToggleAction action) {
        for (FmlToggleAction a : actionList) {
            if (action == a) {
                a.setSelected(true);
            } else {
                a.setSelected(false);
            }
        }
    }
}
