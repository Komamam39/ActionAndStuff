package com.ans.perf;

import com.geckolib.renderer.GeoReplacedEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GeoReplacedEntityRenderer.class, remap = false)
public abstract class ArmorStandNameMixin {
    @Inject(method = "shouldShowName", at = @At("HEAD"), cancellable = true)
    private void ans$armorStandName(Entity var1, double var2, CallbackInfoReturnable<Boolean> var4) {
        if (var1 instanceof ArmorStand var5) {
            if (!var5.hasCustomName()) {
                var4.setReturnValue(false);
            } else {
                String var6 = var5.getCustomName().getString();
                if (!var6.equalsIgnoreCase("prop") && !var6.equalsIgnoreCase("item") && !var6.startsWith("§")) {
                    Minecraft var7 = Minecraft.getInstance();
                    var4.setReturnValue(!var7.gui.hud.isHidden() && var7.getEntityRenderDispatcher().crosshairPickEntity == var5);
                } else {
                    var4.setReturnValue(false);
                }
            }
        }
    }
}
