package com.notify.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class EnterAwareTextField extends TextFieldWidget {

    private Consumer<EnterAwareTextField> onEnterPressedCallback = (textField) -> {};

    public EnterAwareTextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
    }

    public void setOnEnterPressedCallback(Consumer<EnterAwareTextField> callback) {
        this.onEnterPressedCallback = callback;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // If this text field is focused and Enter is pressed
        if (this.isFocused()) { // isFocused() is inherited from AbstractButtonWidget
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                if (this.onEnterPressedCallback != null) {
                    this.onEnterPressedCallback.accept(this); // Trigger our custom action
                }
                return true; // Indicate we've handled the key press
            }
        }
        // Otherwise, let the default TextFieldWidget behavior handle the key press
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}