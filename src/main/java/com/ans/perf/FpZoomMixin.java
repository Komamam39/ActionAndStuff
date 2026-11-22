package com.ans.perf;

import com.ans.client.bedrock.EntityBundle;
import com.ans.client.mixin.ItemInHandRendererAccessor;
import com.ans.client.mob.player.PlayerRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.ans.client.mob.player.FirstPerson", remap = false)
public abstract class FpZoomMixin {
    @Unique
    private static final float ANS_ZOOM = ans$zoom();
    @Unique
    private static int ans$depth;

    @Unique
    private static float ans$zoom() {
        try {
            return Float.parseFloat(System.getProperty("ans.fpZoom", "1.0"));
        } catch (NumberFormatException var1) {
            return 1.0F;
        }
    }

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
        if (ANS_ZOOM != 1.0F && !(ANS_ZOOM <= 0.0F)) {
            var4.pushPose();
            var4.scale(ANS_ZOOM, ANS_ZOOM, 1.0F);
            ans$depth++;
        }
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
        if (ans$depth > 0) {
            ans$depth--;
            var4.popPose();
        }
    }
}
