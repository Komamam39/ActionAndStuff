package com.ans.perf;

import com.ans.client.equipment.EquipmentLayer;
import com.ans.perfutil.HeldArmor;
import java.util.EnumSet;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EquipmentLayer.class, remap = false)
public abstract class HeldArmorThirdPersonMixin {
    @Inject(method = "attachables", at = @At("RETURN"))
    private void ans$hideHeldArmorItem(LivingEntity var1, LivingEntityRenderState var2, float var3, CallbackInfoReturnable<EnumSet<EquipmentSlot>> var4) {
        if (var1 instanceof Player && HeldArmor.active(AttachablesAccessor.ans$ownerRuntime(var1))) {
            ((EnumSet)var4.getReturnValue()).add(EquipmentSlot.MAINHAND);
        }
    }
}
