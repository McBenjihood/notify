package com.notify.mixin;

import com.notify.gui.CustomWidget;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen {
    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        HandledScreen<?> screen = (HandledScreen<?>) (Object) this;

        CustomWidget customWidget = new CustomWidget(
                screen.width / 2 + 100,
                screen.height / 2 - 50,
                120,
                20
        );

        screen.addDrawableChild(customWidget);
    }
}