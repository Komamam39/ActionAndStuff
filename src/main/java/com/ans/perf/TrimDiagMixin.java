package com.ans.perf;

import com.ans.client.attachable.TrimTextures;
import com.ans.perfutil.TrimCompat;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TrimTextures.class, remap = false)
public abstract class TrimDiagMixin {
    @Unique
    private static final Logger ANS_LOG = LoggerFactory.getLogger("ans_trim");
    @Unique
    private static final boolean ANS_LOGON = Boolean.getBoolean("ans.trimLog");
    @Unique
    private static long ans$last;

    @Inject(
        method = "texture(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/resources/Identifier;)Lnet/minecraft/resources/Identifier;",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private static void ans$texture(ItemStack var0, EquipmentSlot var1, Identifier var2, CallbackInfoReturnable<Identifier> var3) {
        Identifier var4 = TrimCompat.texture(var0, var1, var2);
        if (ANS_LOGON) {
            long var5 = System.currentTimeMillis();
            if (var5 - ans$last > 3000L) {
                ans$last = var5;
                ANS_LOG.info("[ans_trim] {} {} -> {}", var0, var1, var4);
            }
        }

        var3.setReturnValue(var4);
    }
}
