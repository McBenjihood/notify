package com.notify;

import com.notify.gui.CustomNotifyScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class NotifyClient implements ClientModInitializer {
	private static KeyBinding openScreenKeybind;

	@Override
	public void onInitializeClient() {
		// Register your keybinding
		openScreenKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.notify.open_screen", // The translation key for the keybinding's name
				InputUtil.Type.KEYSYM, // The type of input (keyboard, mouse)
				GLFW.GLFW_KEY_E,       // The default key (O key)
				"category.notify.gui"  // The translation key for the keybinding's category
		));

		// Register a client tick event listener to check for key presses
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (openScreenKeybind.wasPressed()) {
				// When the key is pressed, open your custom screen
				client.setScreen(new CustomNotifyScreen(Text.translatable("notify.gui.title")));
			}
		});
	}
}