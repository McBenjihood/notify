package com.notify.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class CustomWidget extends ClickableWidget {
    public CustomWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Text.literal("Custom Widget"));
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw a simple rectangle with gradient
        int startColor = isHovered() ? 0xFFFF0000 : 0xFF00FF00; // Red if hovered, else Green
        int endColor = isHovered() ? 0xFF00FFFF : 0xFF0000FF;   // Cyan if hovered, else Blue

        context.fillGradient(getX(), getY(), getX() + width, getY() + height, startColor, endColor);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getMessage());
    }
}