package com.ans.perf;

import com.ans.client.mob.groups.ObjectMobs;
import com.ans.client.molang.MolangQueries;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ObjectMobs.class, remap = false)
public abstract class XpOrbSizeMixin {
    @Unique
    private static final double ANS_MAX = ans$max();

    @Unique
    private static double ans$max() {
        try {
            return Double.parseDouble(System.getProperty("ans.xpOrbMaxSize", "4"));
        } catch (NumberFormatException var1) {
            return 4.0;
        }
    }

    @Inject(method = "register", at = @At("TAIL"))
    private static void ans$xpOrbSize(CallbackInfo var0) {
        MolangQueries.actor("texture_frame_index", var0x -> var0x instanceof ExperienceOrb var1 ? Math.min(var1.getIcon(), ANS_MAX) : 0.0);
    }
}
