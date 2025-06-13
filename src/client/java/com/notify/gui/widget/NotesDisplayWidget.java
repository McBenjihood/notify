// src/client/java/com/notify/gui/widget/NotesDisplayWidget.java
package com.notify.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

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
    private TextRenderer textRenderer;
    private final MinecraftClient client;

    public static final int PADDING = 5;
    // Adjusted LINE_SPACING for a slightly larger gap globally
    public static final int LINE_SPACING = 4; // Was 2. This affects all lines.
    public static final int WIDGET_WIDTH = 150;
    private static final int BACKGROUND_COLOR = 0x90000000;
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int MARGIN_FROM_LEFT = 10;
    // We will remove the NOTE_ENTRY_SPACING_LINES logic for now, relying on the new LINE_SPACING

    private int currentX, currentY, currentHeight;
    private boolean visible = true;
    private boolean needsReload = true;
    private boolean clientFullyReady = false;

    private static final String SAVE_DIRECTORY_NAME = "notify";
    private static final String SAVE_FILE_NAME = "notes.txt";

    public NotesDisplayWidget(MinecraftClient client) {
        this.client = client;
        this.lines = new ArrayList<>();
        this.currentHeight = 0;
        this.currentX = MARGIN_FROM_LEFT;
        this.currentY = PADDING;
    }

    private boolean ensureClientReady() {
        if (clientFullyReady) return true;
        if (this.client != null && this.client.getWindow() != null && this.client.textRenderer != null) {
            this.textRenderer = this.client.textRenderer;
            this.clientFullyReady = true;
            if (needsReload || !this.lines.isEmpty()) {
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
            this.needsReload = true;
            return;
        }

        Path filePath = getSaveFilePath();
        List<String> rawUserNotes = new ArrayList<>();
        if (Files.exists(filePath)) {
            try {
                rawUserNotes = Files.readAllLines(filePath, StandardCharsets.UTF_8)
                        .stream()
                        .filter(line -> line != null && !line.isBlank())
                        .collect(Collectors.toList());
            } catch (IOException e) {
                System.err.println("NotesDisplayWidget: Error loading raw notes from file: " + e.getMessage());
            }
        }

        List<String> processedDisplayLines = new ArrayList<>();
        int wrapWidth = WIDGET_WIDTH - (2 * PADDING);

        for (String userNote : rawUserNotes) { // Iterate through original notes
            if (this.textRenderer == null) {
                processedDisplayLines.add(userNote);
                continue;
            }

            List<StringVisitable> wrappedVisitableLines = this.textRenderer.getTextHandler().wrapLines(userNote, wrapWidth, Style.EMPTY);
            for (StringVisitable sv : wrappedVisitableLines) {
                processedDisplayLines.add(sv.getString());
            }
            // No more adding extra blank lines here. The increased LINE_SPACING will handle the gap.
        }

        this.lines = processedDisplayLines;
        this.needsReload = false;
        recalculateDimensionsAndPosition();
    }

    public void markForReload() {
        this.needsReload = true;
    }

    private void recalculateDimensionsAndPosition() {
        if (!ensureClientReady() || this.textRenderer == null) {
            return;
        }
        if (this.lines.isEmpty()) {
            this.currentHeight = 0;
            return;
        }

        int textBlockHeight = (this.lines.size() * this.textRenderer.fontHeight) +
                (Math.max(0, this.lines.size() - 1) * LINE_SPACING); // LINE_SPACING is now larger
        this.currentHeight = textBlockHeight + (2 * PADDING);
        this.currentX = MARGIN_FROM_LEFT;

        Window window = this.client.getWindow();
        if (window != null && window.getScaledHeight() > 0) {
            this.currentY = (window.getScaledHeight() - this.currentHeight) / 2;
        } else {
            this.currentY = PADDING + this.textRenderer.fontHeight;
        }
    }

    public void render(DrawContext context, float tickDelta) {
        if (!ensureClientReady()) {
            return;
        }
        if (needsReload) {
            loadNotesFromFile();
        }
        if (!this.visible || this.lines.isEmpty() || this.currentHeight <= 0 || this.textRenderer == null) {
            return;
        }

        Window window = this.client.getWindow();
        if (window != null && window.getScaledHeight() > 0) {
            this.currentY = (window.getScaledHeight() - this.currentHeight) / 2;
        }

        context.fill(currentX, currentY, currentX + WIDGET_WIDTH, currentY + currentHeight, BACKGROUND_COLOR);

        int textY = currentY + PADDING;
        for (String displayLine : this.lines) {
            if (textY + this.textRenderer.fontHeight > currentY + currentHeight - PADDING) {
                break;
            }
            // No need to check for empty displayLine here as we're not adding them as separators anymore
            String lineToRender = this.textRenderer.trimToWidth(displayLine, WIDGET_WIDTH - (2 * PADDING));
            context.drawTextWithShadow(this.textRenderer, lineToRender, currentX + PADDING, textY, TEXT_COLOR);
            textY += this.textRenderer.fontHeight + LINE_SPACING; // LINE_SPACING is now larger
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (visible && needsReload && ensureClientReady()) {
            loadNotesFromFile();
        }
    }
}