package com.ansviewmodel.mixin;

import com.ans.client.bedrock.FrameResult;
import com.ans.client.mixin.ItemInHandRendererAccessor;
import com.ans.client.mob.player.FirstPerson;
import com.ansviewmodel.AnsViewmodel;
import com.ansviewmodel.render.ItemFollow;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FirstPerson.class, remap = false)
public abstract class FirstPersonMixin {
    @Unique
    private static boolean ansViewmodel$drawLogged;

    @Inject(method = "draw", at = @At("HEAD"))
    private static void ansViewmodel$beginDraw(CallbackInfo var0, @Local(argsOnly = true) PoseStack var1) {
        if (!ansViewmodel$drawLogged) {
            ansViewmodel$drawLogged = true;
            AnsViewmodel.LOGGER.info("[ans_viewmodel] Motion & Bits first-person renderer reached; viewmodel hooks installed");
        }

        ItemFollow.beginDraw(var1);
    }

    @Inject(
        method = "draw",
        at = @At(
            value = "INVOKE",
            target = "Lcom/geckolib/renderer/base/RenderPassInfo;create(Lcom/geckolib/renderer/base/GeoRenderer;Lcom/geckolib/renderer/base/GeoRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/level/CameraRenderState;Z)Lcom/geckolib/renderer/base/RenderPassInfo;"
        )
    )
    private static void ansViewmodel$captureModelRoot(CallbackInfo var0, @Local(argsOnly = true) PoseStack var1, @Local FrameResult var2) {
        ItemFollow.captureModelRoot(var1, var2);
    }

    @WrapOperation(
        method = "javaHands",
        at = @At(
            value = "INVOKE",
            target = "Lcom/ans/client/mixin/ItemInHandRendererAccessor;ans$submitArmWithItem(Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"
        )
    )
    private static void ansViewmodel$followVanillaItem(
        ItemInHandRendererAccessor var0,
        PlayerRenderState var1,
        FirstPersonHandsAndItemsRenderState var2,
        float var3,
        float var4,
        InteractionHand var5,
        float var6,
        ItemStack var7,
        float var8,
        PoseStack var9,
        SubmitNodeCollector var10,
        int var11,
        Operation<Void> var12
    ) {
        HumanoidArm var13 = var1.avatarRenderState.mainArm;
        HumanoidArm var14 = var5 == InteractionHand.MAIN_HAND ? var13 : var13.getOpposite();
        Matrix4f var15 = ItemFollow.forSide(var14);
        if (var15 == null) {
            var12.call(new Object[]{var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11});
        } else {
            var9.pushPose();
            var9.mulPose(var15);
            var12.call(new Object[]{var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11});
            var9.popPose();
        }
    }

    @WrapOperation(
        method = "javaHands",
        at = @At(
            value = "INVOKE",
            target = "Lcom/ans/client/mixin/ItemInHandRendererAccessor;ans$renderTwoHandedMap(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IFFFLnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;)V"
        )
    )
    private static void ansViewmodel$followTwoHandedMap(
        ItemInHandRendererAccessor var0,
        PoseStack var1,
        SubmitNodeCollector var2,
        int var3,
        float var4,
        float var5,
        float var6,
        PlayerRenderState var7,
        FirstPersonHandsAndItemsRenderState var8,
        Operation<Void> var9
    ) {
        Matrix4f var10 = ItemFollow.bothHands();
        if (var10 != null) {
            var1.mulPose(var10);
        }

        var9.call(new Object[]{var0, var1, var2, var3, var4, var5, var6, var7, var8});
    }

    @WrapOperation(
        method = "javaHands",
        at = @At(
            value = "INVOKE",
            target = "Lcom/ans/client/mixin/ItemInHandRendererAccessor;ans$renderOneHandedMap(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IFLnet/minecraft/world/entity/HumanoidArm;FLnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;)V"
        )
    )
    private static void ansViewmodel$followOneHandedMap(
        ItemInHandRendererAccessor var0,
        PoseStack var1,
        SubmitNodeCollector var2,
        int var3,
        float var4,
        HumanoidArm var5,
        float var6,
        ItemStack var7,
        PlayerRenderState var8,
        FirstPersonHandsAndItemsRenderState var9,
        Operation<Void> var10
    ) {
        Matrix4f var11 = ItemFollow.forSide(var5);
        if (var11 != null) {
            var1.mulPose(var11);
        }

        var10.call(new Object[]{var0, var1, var2, var3, var4, var5, var6, var7, var8, var9});
    }
}
