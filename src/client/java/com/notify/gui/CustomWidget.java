package com.notify.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;

public class CustomWidget extends ClickableWidget {

    private final Runnable onPressAction;

    public CustomWidget(int x, int y, int width, int height, Text message, Runnable onPressAction) {
        super(x, y, width, height, message);
        this.onPressAction = onPressAction;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible &&
                mouseX >= (double)this.getX() && mouseY >= (double)this.getY() &&
                mouseX < (double)(this.getX() + this.width) && mouseY < (double)(this.getY() + this.height) &&
                button == 0) {

            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            if (this.onPressAction != null) {
                this.onPressAction.run();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();

        int backgroundColor = 0x80000000;
        if (this.isHovered() && this.active) {
            backgroundColor = 0xA0000000;
        }
        context.fill(getX(), getY(), getX() + this.width, getY() + this.height, backgroundColor);

        int outlineColor = 0xFFFFFFFF;

        context.fill(getX(), getY(), getX() + this.width, getY() + 1, outlineColor);

        context.fill(getX(), getY() + this.height - 1, getX() + this.width, getY() + this.height, outlineColor);

        context.fill(getX(), getY() + 1, getX() + 1, getY() + this.height - 1, outlineColor);

        context.fill(getX() + this.width - 1, getY() + 1, getX() + this.width, getY() + this.height - 1, outlineColor);

        int textColor = 0xFFFFFFFF;
        if (!this.active) {
            textColor = 0xFFA0A0A0;
        }

        int fontHeight = client.textRenderer.fontHeight;
        int textY = this.getY() + (this.height - fontHeight) / 2;
        if (this.getMessage().getString().length() == 1) {
        }


        context.drawCenteredTextWithShadow(
                client.textRenderer,
                this.getMessage(),
                this.getX() + this.width / 2,
                textY,
                textColor
        );
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getMessage());
        if (this.active) {
            if (this.isFocused()) {
                builder.put(NarrationPart.USAGE, Text.translatable("narration.widget.usage.focused"));
            } else {
                builder.put(NarrationPart.USAGE, Text.translatable("narration.widget.usage.hovered"));
            }
        }
    }
}