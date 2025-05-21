package com.notify.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class CustomNotifyScreen extends Screen {

    public CustomNotifyScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init(); // Always call the super method first

        // --- ADD YOUR CUSTOM WIDGET HERE ---
        // Example: x = 40, y = 80, width = 120, height = 20
        CustomWidget customWidget = new CustomWidget(this.width / 2 - 60, this.height / 2 - 10, 120, 20); // Centered example
        this.addDrawableChild(customWidget);

        // You might also have other buttons or elements here
    }

    // ... other methods like render() etc.
}
