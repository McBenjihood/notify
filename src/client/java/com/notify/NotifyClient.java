// src/client/java/com/notify/NotifyClient.java
package com.notify;

import com.notify.gui.CustomWidget;
import com.notify.gui.screen.NotifyScreen;
import com.notify.gui.widget.NotesDisplayWidget;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen; // Ensure this is resolvable
// Import specific inventory screens IF AbstractInventoryScreen cannot be resolved (THIS IS A WORKAROUND)
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen; // For chests, hoppers, etc.
import net.minecraft.client.gui.screen.ingame.CraftingScreen;       // For crafting table
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;         // For furnace, blast furnace, smoker
// Add other specific screens as needed, e.g.:
// import net.minecraft.client.gui.screen.ingame.BeaconScreen;
// import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
// import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
// import net.minecraft.client.gui.screen.ingame.MerchantScreen; // Villager trading

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

import java.util.List;


public class NotifyClient implements ClientModInitializer {

	private static NotesDisplayWidget notesDisplayWidgetInstance;

	public static void requestNotesDisplayReload() {
		if (notesDisplayWidgetInstance != null) {
			notesDisplayWidgetInstance.markForReload();
		}
	}

	@Override
	public void onInitializeClient() {
		final MinecraftClient mcClient = MinecraftClient.getInstance(); // Use final local variable
		notesDisplayWidgetInstance = new NotesDisplayWidget(mcClient);

		HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
			if (notesDisplayWidgetInstance == null) {
				return;
			}

			// This line is crucial and assumes renderTickCounter is a valid RenderTickCounter object
			float currentTickDelta = renderTickCounter.getTickDelta(true);

			Screen currentScreen = mcClient.currentScreen; // Use the mcClient instance
			boolean shouldDisplayOnHud = false;

			if (currentScreen == null) { // In-game, no screen open
				shouldDisplayOnHud = true;
			} else if (
				// Replace AbstractInventoryScreen with specific checks:
					currentScreen instanceof InventoryScreen ||
							currentScreen instanceof CreativeInventoryScreen ||
							currentScreen instanceof GenericContainerScreen ||
							currentScreen instanceof CraftingScreen ||
							currentScreen instanceof FurnaceScreen
				// Add more '|| currentScreen instanceof YourSpecificScreenType' here if needed
			) {
				shouldDisplayOnHud = true;
			}

			notesDisplayWidgetInstance.setVisible(shouldDisplayOnHud);
			if (shouldDisplayOnHud) {
				// This call requires currentTickDelta to be a float.
				// Ensure NotesDisplayWidget.render expects (DrawContext, float)
				notesDisplayWidgetInstance.render(drawContext, currentTickDelta);
			}
		});

		ScreenEvents.AFTER_INIT.register((screenClient, screen, scaledWidth, scaledHeight) -> {
			// Use screenClient from lambda parameters for consistency
			if (screen instanceof InventoryScreen || screen instanceof CreativeInventoryScreen) {
				int notifyButtonTextWidth = screenClient.textRenderer.getWidth("Notify") + 8;
				int commonWidgetHeight = 20;
				int margin = 5;

				int notifyButtonX = margin;
				int notifyButtonY = margin;

				List<ClickableWidget> screenWidgets = Screens.getButtons(screen);
				if (screenWidgets != null) {
					screenWidgets.removeIf(widget ->
							(widget instanceof CustomWidget && ((CustomWidget) widget).getMessage().getString().equals("Notify"))
					);

					CustomWidget notifyButton = new CustomWidget(
							notifyButtonX,
							notifyButtonY,
							notifyButtonTextWidth,
							commonWidgetHeight,
							Text.literal("Notify"),
							() -> screenClient.setScreen(new NotifyScreen(screen, Text.of("Notify Mod Screen")))
					);
					screenWidgets.add(notifyButton);
				}
			}
		});
	}
}