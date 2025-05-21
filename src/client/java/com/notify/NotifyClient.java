package com.notify;

import com.notify.gui.CustomNotifyScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class NotifyClient implements ClientModInitializer {
	private boolean hasShownWidget = false;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!hasShownWidget && client.player != null) {
				// Show widget once the player exists (game has fully loaded)
				client.setScreen(new CustomNotifyScreen());
				hasShownWidget = true;
			}
		});
	}
}