package com.ans.perf;

import com.ans.client.mob.player.PlayerRenderer;
import com.ans.client.render.MobAnimatable;
import com.ans.perfutil.CushionSeat;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.decoration.Cushion;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerRenderer.class, remap = false)
public abstract class CushionSeatMixin {
    @Inject(
        method = "addRenderData(Lcom/ans/client/render/MobAnimatable;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
        at = @At("TAIL")
    )
    private void ans$cushionSeat(MobAnimatable var1, Player var2, LivingEntityRenderState var3, float var4, CallbackInfo var5) {
        if (var2 != null) {
            float var6 = var2.getVehicle() instanceof Cushion var7 ? CushionSeat.OFFSET + (CushionSeat.furniture(var7) ? CushionSeat.FURNITURE : 0.0F) : 0.0F;
            ((GeoRenderState)var3).addGeckolibData(CushionSeat.TICKET, var6);
        }
    }

    @Inject(method = "applyRotations", at = @At("HEAD"))
    private void ans$cushionLift(RenderPassInfo<?> var1, PoseStack var2, float var3, CallbackInfo var4) {
        Float var5 = var1.getGeckolibData(CushionSeat.TICKET);
        if (var5 != null && var5 != 0.0F) {
            var2.translate(0.0F, var5, 0.0F);
        }
    }
}
