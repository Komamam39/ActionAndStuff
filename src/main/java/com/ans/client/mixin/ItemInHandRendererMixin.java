package com.ans.client.mixin;

import com.ans.client.mob.player.FirstPerson;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Inject(method = "submitHandsWithItems", at = @At("HEAD"), cancellable = true)
    private void ans$firstPerson(
        float var1, PoseStack var2, SubmitNodeCollector var3, PlayerRenderState var4, FirstPersonHandsAndItemsRenderState var5, CallbackInfo var6
    ) {
        if (FirstPerson.submit((FirstPersonHandsAndItemsRenderer)(Object)this, var1, var2, var3, var4, var5)) {
            var6.cancel();
        }
    }
}
