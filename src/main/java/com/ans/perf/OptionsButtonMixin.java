package com.ans.perf;

import com.ans.perfutil.AnsHubScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(OptionsScreen.class)
public abstract class OptionsButtonMixin {
    @ModifyArg(
        method = "init",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/layouts/HeaderAndFooterLayout;addToContents(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;"
        ),
        index = 0,
        require = 0
    )
    private LayoutElement ans$settingsButton(LayoutElement var1) {
        if (var1 instanceof GridLayout var2) {
            Screen var3 = (Screen)(Object)this;
            var2.addChild(
                Button.builder(Component.translatable("ans.settings.title"), var1x -> Minecraft.getInstance().gui.setScreen(new AnsHubScreen(var3, null)))
                    .width(308)
                    .build(),
                5,
                0,
                1,
                2
            );
        }

        return var1;
    }
}
