package com.notify;

import com.notify.gui.CustomWidget;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ClickableWidget;

public class NotifyClient implements ClientModInitializer {
	private ClickableWidget inventoryWidget;

	@Override
	public void onInitializeClient() {
		// Register screen event for when inventory opens
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof InventoryScreen || screen instanceof CreativeInventoryScreen) {
				// Create widget (but don't add it yet)
				inventoryWidget = new CustomWidget(
						screen.width / 2 + 100, // Right side of inventory
						screen.height / 2 - 60,  // Above center
						120,
						40
				);

				// Add widget to the screen
				Screens.getButtons(screen).add(inventoryWidget);
			}
		});
	}
}