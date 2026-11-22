package com.ans.perf;

import com.ans.client.mixin.ItemInHandRendererAccessor;
import com.ans.client.mob.player.FirstPerson;
import com.ans.perfutil.HeldArmor;
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

@Mixin(value = FirstPerson.class, remap = false)
public abstract class HeldArmorFirstPersonMixin {
    @Inject(method = "javaHands", at = @At("HEAD"))
    private static void ans$hideHeldArmorItem(
        ItemInHandRendererAccessor var0,
        float var1,
        PoseStack var2,
        SubmitNodeCollector var3,
        LocalPlayer var4,
        int var5,
        boolean[] var6,
        PlayerRenderState var7,
        FirstPersonHandsAndItemsRenderState var8,
        AvatarRenderState var9,
        CallbackInfo var10
    ) {
        if (HeldArmor.active(FirstPerson.runtime())) {
            var6[0] = true;
        }
    }
}
