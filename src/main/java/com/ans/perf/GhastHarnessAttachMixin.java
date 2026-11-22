package com.ans.perf;

import com.ans.client.attachable.Attachables;
import com.ans.perfutil.GhastHarness;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Attachables.class, remap = false)
public abstract class GhastHarnessAttachMixin {
    @Shadow
    @Final
    @Mutable
    public static Map<EquipmentSlot, String> SLOT_NAMES;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void ans$bodySlot(CallbackInfo var0) {
        HashMap var1 = new HashMap<>(SLOT_NAMES);
        var1.put(EquipmentSlot.BODY, "body");
        SLOT_NAMES = Map.copyOf(var1);
    }

    @Inject(method = "slots", at = @At("RETURN"), cancellable = true)
    private static void ans$ghastBody(LivingEntity var0, CallbackInfoReturnable<EquipmentSlot[]> var1) {
        if (GhastHarness.ON && var0.getType() == EntityTypes.HAPPY_GHAST) {
            EquipmentSlot[] var2 = (EquipmentSlot[])var1.getReturnValue();
            EquipmentSlot[] var3 = Arrays.copyOf(var2, var2.length + 1);
            var3[var2.length] = EquipmentSlot.BODY;
            var1.setReturnValue(var3);
        }
    }
}
