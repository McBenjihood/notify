package com.notify.gui.screen;

import com.notify.gui.CustomWidget;

import net.minecraft.client.MinecraftClient; // For toasts (if you still use them)
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class NotifyScreen extends Screen {
    private final Screen parent;

    private static final int MARGIN = 5;
    private static final int BUTTON_SIZE = 20;

    public NotifyScreen(Screen parent, Text title) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();


        int buttonX = MARGIN;
        int buttonY = MARGIN;


        CustomWidget plusButton = new CustomWidget(
                buttonX,
                buttonY,
                BUTTON_SIZE,
                BUTTON_SIZE,
                Text.literal("+"),
                () -> {
                    System.out.println("Plus button on NotifyScreen clicked!");

                    if (this.client != null) {
                        this.client.getToastManager().add(
                                SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE, Text.of("Action!"), Text.of("Plus button pressed."))
                        );
                    }
                }
        );

        this.addDrawableChild(plusButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render the default background (usually a transparent dark overlay)
        super.render(context, mouseX, mouseY, delta);

        // Optionally, draw a title on the screen
        if (this.title != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        }
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}