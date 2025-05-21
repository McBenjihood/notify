package com.notify.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class CustomWidget extends ClickableWidget {
    public CustomWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Text.literal("Notify"));
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw background
        int backgroundColor = 0x80000000; // Semi-transparent black
        context.fill(getX(), getY(), getX() + width, getY() + height, backgroundColor);

        // Draw border
        int borderColor = 0xFFFFFFFF; // White
        context.drawBorder(getX(), getY(), width, height, borderColor);

        // Draw text
        int textColor = 0xFFFFFF00; // Yellow
        context.drawCenteredTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                this.getMessage(),
                getX() + width / 2,
                getY() + (height - 8) / 2,
                textColor
        );
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getMessage());
    }
}