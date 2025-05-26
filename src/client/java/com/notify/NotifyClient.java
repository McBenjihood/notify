// File: src/client/java/com/notify/NotifyClient.java
package com.notify;

import com.notify.gui.CustomWidget;
import com.notify.gui.screen.NotifyScreen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
// NEW/CORRECTED IMPORT for the Screens utility class
import net.fabricmc.fabric.api.client.screen.v1.Screens; // <--- ADD THIS IMPORT
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.Text;
import java.util.List;

public class NotifyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof InventoryScreen || screen instanceof CreativeInventoryScreen) {
				int textWidth = MinecraftClient.getInstance().textRenderer.getWidth("Notify") + 8;
				int widgetHeight = 20;

				int x = 5;
				int y = 5;

				CustomWidget notifyButton = new CustomWidget(
						x,
						y,
						textWidth,
						widgetHeight,
						Text.literal("Notify"),
						() -> {
							MinecraftClient mc = MinecraftClient.getInstance();
							Screen currentScreen = mc.currentScreen;
							mc.setScreen(new NotifyScreen(currentScreen, Text.of("Notify Mod Screen")));
						}
				);

				try {
					List<ClickableWidget> buttons = Screens.getButtons(screen);
					if (buttons != null) {
						buttons.add(notifyButton);
					} else {
						System.err.println("NotifyMod: Screens.getButtons(screen) returned null for screen: " + screen.getClass().getName());
					}
				} catch (Exception e) {
					System.err.println("NotifyMod: Error accessing Screens.getButtons. Check Fabric API version and setup.");
					e.printStackTrace();
				}
			}
		});
	}
}