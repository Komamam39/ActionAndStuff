package com.ans.client.mixin;

import com.ans.client.render.UiRender;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {
    @WrapMethod(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;")
    private static EntityRenderState ans$uiRender(LivingEntity entity, Operation<EntityRenderState> original) {
        UiRender.begin();

        try {
            return (EntityRenderState)original.call(new Object[]{entity});
        } finally {
            UiRender.end();
        }
    }
}
