package com.notify.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class CustomNotifyScreen extends Screen {
    public CustomNotifyScreen() {
        super(Text.literal("")); // Empty title
    }

    @Override
    protected void init() {
        super.init();

        // Add custom widget (centered)
        int widgetWidth = 200;
        int widgetHeight = 40;

        this.addDrawableChild(new CustomWidget(
                this.width / 2 - widgetWidth / 2,
                this.height / 4, // Positioned a bit higher than center
                widgetWidth,
                widgetHeight
        ));
    }

    @Override
    public boolean shouldPause() {
        return false; // Game continues in background
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true; // Allow closing with ESC
    }
}