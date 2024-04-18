package com.tcpip147.flowml.ui.component;

import com.tcpip147.flowml.ui.FmlColor;

import java.awt.*;

public class GhostActivity extends Activity {

    public Activity activity;

    public GhostActivity(Activity activity) {
        super(activity.x, activity.y, activity.width, activity.height);
        this.activity = activity;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(FmlColor.GHOST_DEFAULT);
        g.drawRect(x, y, width, height);
    }
}
