package com.ans.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public interface ItemInHandRendererAccessor {
    @Invoker("submitArmWithItem")
    void ans$submitArmWithItem(
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
        int var11
    );

    @Invoker("renderTwoHandedMap")
    void ans$renderTwoHandedMap(
        PoseStack var1,
        SubmitNodeCollector var2,
        int var3,
        float var4,
        float var5,
        float var6,
        PlayerRenderState var7,
        FirstPersonHandsAndItemsRenderState var8
    );

    @Invoker("renderOneHandedMap")
    void ans$renderOneHandedMap(
        PoseStack var1,
        SubmitNodeCollector var2,
        int var3,
        float var4,
        HumanoidArm var5,
        float var6,
        ItemStack var7,
        PlayerRenderState var8,
        FirstPersonHandsAndItemsRenderState var9
    );
}
