package com.ans.perf;

import com.ans.client.molang.BedrockQueries;
import com.ans.client.molang.MolangQueries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BedrockQueries.class, remap = false)
public abstract class BlockingQueryMixin {
    @Inject(method = "state", at = @At("TAIL"), require = 0)
    private static void ans$blocking(CallbackInfo var0) {
        if (!"false".equals(System.getProperty("ans.instantBlock", "true"))) {
            MolangQueries.flag("blocking", BlockingQueryMixin::ans$isBlocking);
        }
    }

    @Unique
    private static boolean ans$isBlocking(LivingEntity var0) {
        return var0.isBlocking() || var0.isUsingItem() && var0.getUseItem().has(DataComponents.BLOCKS_ATTACKS);
    }
}
