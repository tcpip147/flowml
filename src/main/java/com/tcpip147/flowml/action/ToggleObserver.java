package com.tcpip147.flowml.action;

import java.util.ArrayList;
import java.util.List;

public class ToggleObserver {

    public List<FmlToggleAction> actionList = new ArrayList<>();

    public void addAction(FmlToggleAction action) {
        actionList.add(action);
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
