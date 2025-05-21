package com.notify;

import com.notify.gui.CustomWidget;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class NotifyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof InventoryScreen || screen instanceof CreativeInventoryScreen) {
				// Calculate text width to size widget appropriately
				int textWidth = MinecraftClient.getInstance().textRenderer.getWidth("Notify") + 8;
				int widgetHeight = 20;

				// Position in upper left corner
				int x = 5;
				int y = 5;

				// Use Fabric's Screens helper to properly add the widget
				Screens.getButtons(screen).add(new CustomWidget(
						x, y, textWidth, widgetHeight
				));
			}
		});
	}
}
