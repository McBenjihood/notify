// src/client/java/com/notify/gui/widget/NotesDisplayWidget.java
package com.notify.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotesDisplayWidget {
    private List<String> lines;
    private TextRenderer textRenderer; // Initialize later
    private final MinecraftClient client;

    public static final int PADDING = 5;
    public static final int LINE_SPACING = 2;
    public static final int WIDGET_WIDTH = 150;
    private static final int BACKGROUND_COLOR = 0x90000000;
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int MARGIN_FROM_LEFT = 10;

    private int currentX, currentY, currentHeight;
    private boolean visible = true;
    private boolean needsReload = true;
    private boolean clientFullyReady = false; // New flag to check if client.window and textRenderer are set

    private static final String SAVE_DIRECTORY_NAME = "notify";
    private static final String SAVE_FILE_NAME = "notes.txt";

    public NotesDisplayWidget(MinecraftClient client) {
        this.client = client;
        // DO NOT initialize textRenderer here yet
        this.lines = new ArrayList<>();
        this.currentHeight = 0;
        this.currentX = MARGIN_FROM_LEFT; // X can be set
        this.currentY = PADDING; // Default Y
    }

    private boolean ensureClientReady() {
        if (clientFullyReady) return true;

        if (this.client != null && this.client.getWindow() != null && this.client.textRenderer != null) {
            this.textRenderer = this.client.textRenderer; // Now it's safe to assign
            this.clientFullyReady = true;
            // Trigger initial dimension calculation if it wasn't done due to client not being ready
            if (needsReload || !this.lines.isEmpty()) { // If there's something to calculate for
                recalculateDimensionsAndPosition();
            }
            return true;
        }
        return false;
    }

    private Path getSaveFilePath() {
        return Paths.get(this.client.runDirectory.getAbsolutePath(), SAVE_DIRECTORY_NAME, SAVE_FILE_NAME);
    }

    private void loadNotesFromFile() {
        if (!ensureClientReady()) {
            System.out.println("NotesDisplayWidget: Client not fully ready, delaying note load.");
            this.needsReload = true; // Ensure it tries again
            return;
        }

        Path filePath = getSaveFilePath();
        List<String> loaded = new ArrayList<>();
        if (Files.exists(filePath)) {
            try {
                loaded = Files.readAllLines(filePath, StandardCharsets.UTF_8)
                        .stream()
                        .filter(line -> line != null && !line.isBlank())
                        .collect(Collectors.toList());
            } catch (IOException e) {
                System.err.println("NotesDisplayWidget: Error loading notes: " + e.getMessage());
            }
        }
        this.lines = loaded;
        this.needsReload = false;
        recalculateDimensionsAndPosition(); // This is now safe as ensureClientReady was true
    }

    public void markForReload() {
        this.needsReload = true;
    }

    private void recalculateDimensionsAndPosition() {
        if (!ensureClientReady() || this.textRenderer == null) { // Double check textRenderer
            return; // Cannot calculate without textRenderer
        }

        if (this.lines.isEmpty()) {
            this.currentHeight = 0;
            return;
        }

        int textBlockHeight = (this.lines.size() * this.textRenderer.fontHeight) +
                (Math.max(0, this.lines.size() - 1) * LINE_SPACING);
        this.currentHeight = textBlockHeight + (2 * PADDING);
        this.currentX = MARGIN_FROM_LEFT;

        Window window = this.client.getWindow(); // Should be non-null if ensureClientReady passed
        if (window != null && window.getScaledHeight() > 0) {
            this.currentY = (window.getScaledHeight() - this.currentHeight) / 2;
        } else {
            // Fallback Y if something is still unusual, though less likely now
            this.currentY = PADDING + this.textRenderer.fontHeight;
        }
    }

    public void render(DrawContext context, float tickDelta) {
        if (!ensureClientReady()) { // Attempt to ensure client is ready each frame until it is
            return; // Don't render if client isn't fully ready
        }

        if (needsReload) {
            loadNotesFromFile(); // Will also check ensureClientReady internally
        }

        if (!this.visible || this.lines.isEmpty() || this.currentHeight <= 0 || this.textRenderer == null) {
            return;
        }

        // Recalculate Y position for centering (now safer)
        Window window = this.client.getWindow();
        if (window != null && window.getScaledHeight() > 0) {
            this.currentY = (window.getScaledHeight() - this.currentHeight) / 2;
        }
        // else currentY retains its last calculated or default value

        context.fill(currentX, currentY, currentX + WIDGET_WIDTH, currentY + currentHeight, BACKGROUND_COLOR);

        int textY = currentY + PADDING;
        for (String line : this.lines) {
            if (textY + this.textRenderer.fontHeight > currentY + currentHeight - PADDING) {
                break;
            }
            String trimmedLine = this.textRenderer.trimToWidth(line, WIDGET_WIDTH - (2 * PADDING));
            context.drawTextWithShadow(this.textRenderer, trimmedLine, currentX + PADDING, textY, TEXT_COLOR);
            textY += this.textRenderer.fontHeight + LINE_SPACING;
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (visible && needsReload && ensureClientReady()) {
            loadNotesFromFile();
        }
    }
}