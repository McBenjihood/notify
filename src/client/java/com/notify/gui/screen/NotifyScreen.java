// src/client/java/com/notify/gui/screen/NotifyScreen.java
package com.notify.gui.screen;

import com.notify.gui.CustomWidget;
import com.notify.gui.widget.EnterAwareTextField;
import com.notify.NotifyClient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element; // Import for iterating children
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

public class NotifyScreen extends Screen {
    private final Screen parent;

    private static final int MARGIN = 5;
    private static final int WIDGET_HEIGHT = 20;
    private static final int TEXT_FIELD_WIDTH = 150;
    private static final int DELETE_BUTTON_WIDTH = WIDGET_HEIGHT;
    private static final int DELETE_BUTTON_MARGIN_LEFT = 2;
    private static final int TOTAL_WIDGET_ROW_X_OFFSET = DELETE_BUTTON_WIDTH + DELETE_BUTTON_MARGIN_LEFT;

    private final List<EnterAwareTextField> textFields = new ArrayList<>();
    private final List<ClickableWidget> deleteButtons = new ArrayList<>();


    public NotifyScreen(Screen parent, Text title) {
        super(title);
        this.parent = parent;
    }

    private Path getSaveFilePath() {
        return Paths.get(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), "notify", "notes.txt");
    }

    private void saveNotes() {
        Path filePath = getSaveFilePath();
        Path dirPath = filePath.getParent();
        try {
            if (dirPath != null && !Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            List<String> linesToSave = new ArrayList<>();
            for (EnterAwareTextField textField : textFields) {
                String text = textField.getText();
                if (text != null && !text.isBlank()) {
                    linesToSave.add(text);
                }
            }
            Files.write(filePath, linesToSave, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("NotifyScreen: Error saving notes: " + e.getMessage());
            if (this.client != null) {
                this.client.getToastManager().add(
                        SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE,
                                Text.of("Save Error"), Text.of("Could not save notes."))
                );
            }
        }
    }

    private void loadNotes() {
        Path filePath = getSaveFilePath();
        if (!Files.exists(filePath)) return;
        try {
            List<String> loadedLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (String line : loadedLines) {
                if (line != null && !line.isBlank()) {
                    addNewTextFieldRow(null, line);
                }
            }
        } catch (IOException e) {
            System.err.println("NotifyScreen: Error loading notes: " + e.getMessage());
            if (this.client != null) {
                this.client.getToastManager().add(
                        SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE,
                                Text.of("Load Error"), Text.of("Could not load notes."))
                );
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        this.textFields.clear();
        this.deleteButtons.clear();

        int currentY = MARGIN;

        CustomWidget plusButton = new CustomWidget(
                MARGIN,
                currentY,
                WIDGET_HEIGHT,
                WIDGET_HEIGHT,
                Text.literal("+"),
                () -> addNewTextFieldRow(null, null)
        );
        this.addDrawableChild(plusButton);

        // currentY += WIDGET_HEIGHT + MARGIN; // Not needed here, reposition handles Y

        loadNotes();

        if (textFields.isEmpty()) {
            addNewTextFieldRow(null, null); // This will call reposition
        } else {
            repositionAllTextFieldRows(); // Ensure initial loaded notes are positioned
        }
    }

    private void addNewTextFieldRow(@Nullable EnterAwareTextField sourceField, @Nullable String initialText) {
        if (this.client == null) return;

        // Temporary Y, will be updated by repositionAllTextFieldRows
        int tempY = MARGIN + WIDGET_HEIGHT + MARGIN + (textFields.size() * (WIDGET_HEIGHT + MARGIN / 2));

        if (tempY + WIDGET_HEIGHT > this.height - MARGIN && !textFields.isEmpty()) { // Only check if not the first field
            this.client.getToastManager().add(
                    SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE,
                            Text.of("Screen Full"), Text.of("No more space."))
            );
            return;
        }

        EnterAwareTextField newTextField = new EnterAwareTextField(
                this.textRenderer,
                MARGIN + TOTAL_WIDGET_ROW_X_OFFSET,
                0, // Y will be set by repositionAllTextFieldRows
                TEXT_FIELD_WIDTH,
                WIDGET_HEIGHT,
                Text.translatable("gui.notify.textfield_placeholder")
        );

        if (initialText != null) {
            newTextField.setText(initialText);
        }
        newTextField.setMaxLength(256);
        newTextField.setOnEnterPressedCallback((pressedField) -> addNewTextFieldRow(pressedField, null));

        textFields.add(newTextField);

        CustomWidget deleteButton = new CustomWidget(
                MARGIN,
                0, // Y will be set by repositionAllTextFieldRows
                DELETE_BUTTON_WIDTH,
                WIDGET_HEIGHT,
                Text.literal("X"),
                () -> deleteTextFieldRow(newTextField)
        );
        deleteButtons.add(deleteButton);

        this.addDrawableChild(newTextField);
        this.addSelectableChild(newTextField);
        this.addDrawableChild(deleteButton);

        repositionAllTextFieldRows();
        if (this.textRenderer != null) { // Ensure textRenderer is available before focusing
            this.setFocused(newTextField);
            newTextField.setFocused(true); // Explicitly set focus on the new field
        }
    }

    private void deleteTextFieldRow(EnterAwareTextField textFieldToDelete) {
        int indexToDelete = textFields.indexOf(textFieldToDelete);
        if (indexToDelete == -1) return;

        textFields.remove(indexToDelete);
        ClickableWidget buttonToRemove = deleteButtons.remove(indexToDelete);

        this.remove(textFieldToDelete);
        this.remove(buttonToRemove);

        if (textFields.isEmpty()) {
            addNewTextFieldRow(null, null);
        } else {
            repositionAllTextFieldRows();
            if (!textFields.isEmpty()) {
                int focusIndex = Math.max(0, indexToDelete -1);
                if (focusIndex < textFields.size() && this.textRenderer != null) { // Check textRenderer
                    this.setFocused(textFields.get(focusIndex));
                    textFields.get(focusIndex).setFocused(true);
                }
            }
        }
    }

    private void repositionAllTextFieldRows() {
        if (this.client == null) return;

        int currentY = MARGIN;
        // Reposition the "+" button
        for (Element element : this.children()) { // Iterate over Element
            if (element instanceof CustomWidget) {
                CustomWidget cw = (CustomWidget) element;
                if (cw.getMessage().getString().equals("+")) {
                    cw.setY(currentY);
                    break;
                }
            }
        }

        currentY += WIDGET_HEIGHT + MARGIN;

        for (int i = 0; i < textFields.size(); i++) {
            EnterAwareTextField textField = textFields.get(i);
            ClickableWidget deleteButton = deleteButtons.get(i);

            if (currentY + WIDGET_HEIGHT > this.height - MARGIN) {
                textField.visible = false;    // Use the public field
                deleteButton.visible = false; // Use the public field
            } else {
                textField.visible = true;     // Use the public field
                deleteButton.visible = true;  // Use the public field

                textField.setX(MARGIN + TOTAL_WIDGET_ROW_X_OFFSET);
                textField.setY(currentY);

                deleteButton.setX(MARGIN);
                deleteButton.setY(currentY);
            }
            currentY += WIDGET_HEIGHT + (MARGIN / 2);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (this.title != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, MARGIN + 7, 0xFFFFFF);
        }
    }

    @Override
    public void close() {
        saveNotes();
        NotifyClient.requestNotesDisplayReload();
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        // Store texts before super.resize() clears everything
        List<String> currentTexts = new ArrayList<>();
        for (EnterAwareTextField tf : textFields) {
            currentTexts.add(tf.getText());
        }

        // super.resize calls init(), which clears our lists.
        super.resize(client, width, height);

    }
}