package com.ans.perf;

import com.ans.client.bedrock.EntityBundle;
import com.ans.client.mixin.ItemInHandRendererAccessor;
import com.ans.client.mob.player.PlayerRenderer;
import com.ans.perfutil.FpFlag;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.ans.client.mob.player.FirstPerson", remap = false)
public abstract class FpParticleMixin {
    @Inject(method = "draw", at = @At("HEAD"), require = 0)
    private static void ans$head(
        PlayerRenderer var0,
        EntityBundle var1,
        ItemInHandRendererAccessor var2,
        float var3,
        PoseStack var4,
        SubmitNodeCollector var5,
        LocalPlayer var6,
        int var7,
        PlayerRenderState var8,
        FirstPersonHandsAndItemsRenderState var9,
        AvatarRenderState var10,
        CallbackInfo var11
    ) {
        FpFlag.active = true;
    }

    @Inject(method = "draw", at = @At("RETURN"), require = 0)
    private static void ans$tail(
        PlayerRenderer var0,
        EntityBundle var1,
        ItemInHandRendererAccessor var2,
        float var3,
        PoseStack var4,
        SubmitNodeCollector var5,
        LocalPlayer var6,
        int var7,
        PlayerRenderState var8,
        FirstPersonHandsAndItemsRenderState var9,
        AvatarRenderState var10,
        CallbackInfo var11
    ) {
        FpFlag.active = false;
    }
}
