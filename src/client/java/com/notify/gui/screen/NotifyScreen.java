package com.notify.gui.screen;

import com.notify.gui.CustomWidget; // Your styled "+" button
import com.notify.gui.widget.EnterAwareTextField; // Our new custom TextField

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public class NotifyScreen extends Screen {
    private final Screen parent;

    private static final int MARGIN = 5;
    private static final int WIDGET_HEIGHT = 20;
    private static final int TEXT_FIELD_WIDTH = 150;

    private final List<EnterAwareTextField> textFields = new ArrayList<>(); // Now stores EnterAwareTextField
    private int nextTextFieldY;

    public NotifyScreen(Screen parent, Text title) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.textFields.clear();
        nextTextFieldY = MARGIN + WIDGET_HEIGHT + MARGIN;

        int plusButtonX = MARGIN;
        int plusButtonY = MARGIN;

        CustomWidget plusButton = new CustomWidget(
                plusButtonX,
                plusButtonY,
                WIDGET_HEIGHT,
                WIDGET_HEIGHT,
                Text.literal("+"),
                () -> {
                    // When "+" is clicked, pass null because no specific field triggered it
                    addNewTextFieldAndFocus(null);
                }
        );
        this.addDrawableChild(plusButton);

        // Add an initial text field when the screen opens
        addNewTextFieldAndFocus(null);
    }

    // This method will be called by the "+" button and by the EnterAwareTextField's callback
    private void addNewTextFieldAndFocus(EnterAwareTextField sourceField /* can be null */) {
        if (this.client == null) return;

        if (nextTextFieldY + WIDGET_HEIGHT > this.height - MARGIN) {
            this.client.getToastManager().add(
                    SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE,
                            Text.of("Screen Full"), Text.of("No more space."))
            );
            return;
        }

        int textFieldX = MARGIN;
        EnterAwareTextField newTextField = new EnterAwareTextField(
                this.textRenderer,
                textFieldX,
                nextTextFieldY,
                TEXT_FIELD_WIDTH,
                WIDGET_HEIGHT,
                Text.translatable("gui.notify.textfield_placeholder") // Placeholder
        );

        newTextField.setChangedListener(text -> {
            System.out.println("Field (" + textFields.indexOf(newTextField) + ") changed: " + text);
        });
        newTextField.setMaxLength(256); // Allow more characters per "line"

        // Set the callback for when Enter is pressed in *this specific new field*
        newTextField.setOnEnterPressedCallback((pressedField) -> {
            addNewTextFieldAndFocus(pressedField); // Recursively add another, passing the field that triggered it
        });

        this.addDrawableChild(newTextField);
        this.addSelectableChild(newTextField);
        textFields.add(newTextField);
        nextTextFieldY += WIDGET_HEIGHT + (MARGIN / 2);

        // Attempt to set focus to the newly created text field
        this.setFocused(newTextField);
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta); // Renders children

        if (this.title != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, MARGIN + 7, 0xFFFFFF);
        }
    }

    @Override
    public void close() {
        System.out.println("NotifyScreen (Vanilla with EnterAwareTextField) closing. Contents:");
        for (EnterAwareTextField textField : textFields) {
            System.out.println(" - " + textField.getText());
            // Add saving logic
        }
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}