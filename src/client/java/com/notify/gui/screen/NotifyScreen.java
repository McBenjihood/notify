// src/client/java/com/notify/gui/screen/NotifyScreen.java
package com.notify.gui.screen;

import com.notify.gui.CustomWidget;
import com.notify.gui.widget.EnterAwareTextField;
import com.notify.NotifyClient; // For signaling HUD update

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
// No need for Collectors here as we're not using streams in this specific file for saving/loading
// import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

public class NotifyScreen extends Screen {
    private final Screen parent;

    private static final int MARGIN = 5;
    private static final int WIDGET_HEIGHT = 20;
    private static final int TEXT_FIELD_WIDTH = 150;

    private final List<EnterAwareTextField> textFields = new ArrayList<>();
    private int nextTextFieldY;

    // Konstanten für das Speichern/Laden
    private static final String SAVE_DIRECTORY_NAME = "notify";
    private static final String SAVE_FILE_NAME = "notes.txt";

    public NotifyScreen(Screen parent, Text title) {
        super(title);
        this.parent = parent;
    }

    private Path getSaveFilePath() {
        // Pfad zum Minecraft-Verzeichnis, dann zum Unterordner "notify"
        return Paths.get(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), SAVE_DIRECTORY_NAME, SAVE_FILE_NAME);
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
            System.out.println("NotifyScreen: Notes saved to " + filePath);

        } catch (IOException e) {
            System.err.println("NotifyScreen: Error saving notes: " + e.getMessage());
            e.printStackTrace();
            if (this.client != null) {
                this.client.getToastManager().add(
                        SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE, // Was SYSTEM_ERROR
                                Text.of("Save Error"), Text.of("Could not save notes."))
                );
            }
        }
    }

    private void loadNotes() {
        Path filePath = getSaveFilePath();
        if (!Files.exists(filePath)) {
            System.out.println("NotifyScreen: No notes file found at " + filePath + ". Starting fresh.");
            return;
        }

        try {
            List<String> loadedLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            System.out.println("NotifyScreen: Loaded " + loadedLines.size() + " notes from " + filePath);

            for (String line : loadedLines) {
                if (line != null && !line.isBlank()) {
                    addNewTextFieldAndFocus(null, line);
                }
            }
        } catch (IOException e) {
            System.err.println("NotifyScreen: Error loading notes: " + e.getMessage());
            e.printStackTrace();
            if (this.client != null) {
                this.client.getToastManager().add(
                        SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE, // Was SYSTEM_ERROR
                                Text.of("Load Error"), Text.of("Could not load notes."))
                );
            }
        }
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
                    addNewTextFieldAndFocus(null, null);
                }
        );
        this.addDrawableChild(plusButton);

        loadNotes();

        if (textFields.isEmpty()) {
            addNewTextFieldAndFocus(null, null);
        }
    }

    private EnterAwareTextField addNewTextFieldAndFocus(@Nullable EnterAwareTextField sourceField, @Nullable String initialText) {
        if (this.client == null) return null;

        if (nextTextFieldY + WIDGET_HEIGHT > this.height - MARGIN) {
            this.client.getToastManager().add(
                    SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE,
                            Text.of("Screen Full"), Text.of("No more space."))
            );
            return null;
        }

        int textFieldX = MARGIN;
        EnterAwareTextField newTextField = new EnterAwareTextField(
                this.textRenderer,
                textFieldX,
                nextTextFieldY,
                TEXT_FIELD_WIDTH,
                WIDGET_HEIGHT,
                Text.translatable("gui.notify.textfield_placeholder")
        );

        if (initialText != null) {
            newTextField.setText(initialText);
        }

        newTextField.setChangedListener(text -> {
            // Optional: System.out.println("Field (" + textFields.indexOf(newTextField) + ") changed: " + text);
        });
        newTextField.setMaxLength(256);

        newTextField.setOnEnterPressedCallback((pressedField) -> {
            addNewTextFieldAndFocus(pressedField, null);
        });

        this.addDrawableChild(newTextField);
        this.addSelectableChild(newTextField);
        textFields.add(newTextField);
        nextTextFieldY += WIDGET_HEIGHT + (MARGIN / 2);

        this.setFocused(newTextField);
        return newTextField;
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // In Minecraft 1.19.3+ (and likely 1.20+) renderBackground is handled by Screen directly
        // or you can call this.renderDarkening(context) if you want a custom background.
        // For simplicity, we'll rely on Screen's default behavior which calls renderBackground.
        // If you have specific background needs, you might call:
        // this.renderBackground(context, mouseX, mouseY, delta); explicitly if super.render doesn't do it or you do it before.
        super.render(context, mouseX, mouseY, delta); // Renders children, including their backgrounds if they have one

        if (this.title != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, MARGIN + 7, 0xFFFFFF);
        }
    }

    @Override
    public void close() {
        System.out.println("NotifyScreen closing. Saving contents...");
        saveNotes();

        NotifyClient.requestNotesDisplayReload(); // Signal HUD widget to reload notes

        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}