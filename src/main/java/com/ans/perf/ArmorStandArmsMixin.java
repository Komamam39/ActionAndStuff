package com.ans.perf;

import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ArmorStand.class, remap = false)
public abstract class ArmorStandArmsMixin {
    @Unique
    private static final boolean ANS_ON = !"false".equals(System.getProperty("ans.armorStandArms", "true"));

    @Redirect(
        method = {"interact", "isDisabled", "canUseSlot"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/ArmorStand;showArms()Z"),
        require = 0
    )
    private boolean ans$arms(ArmorStand var1) {
        return ANS_ON || var1.showArms();
    }
}
