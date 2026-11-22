package com.ans.perf;

import com.ans.client.settings.AnsSettingsScreen;
import com.ans.perfutil.AnsHubScreen;
import com.ansviewmodel.gui.ViewmodelConfigScreen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class AnsHubRedirectMixin {
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void ans$hub(Screen var1, CallbackInfo var2) {
        if (AnsHubScreen.ENABLED) {
            if (var1 instanceof AnsSettingsScreen var3) {
                var2.cancel();
                ((Gui)(Object)this).setScreen(new AnsHubScreen(((OptionsSubScreenAccessor)(Object)var3).ans$lastScreen(), null));
            } else if (var1 instanceof ViewmodelConfigScreen var4) {
                var2.cancel();
                ((Gui)(Object)this).setScreen(new AnsHubScreen(((ViewmodelScreenAccessor)(Object)var4).ans$parent(), "viewmodel"));
            }
        }
    }
}
