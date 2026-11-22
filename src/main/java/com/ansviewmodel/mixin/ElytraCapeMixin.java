package com.ansviewmodel.mixin;

import com.ans.client.attachable.Attachables;
import com.ans.client.bedrock.FrameResult;
import com.ans.client.bedrock.RenderLayer;
import com.geckolib.renderer.base.GeoRenderState;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Attachables.class, remap = false)
public abstract class ElytraCapeMixin {
    @Inject(method = "tick", at = @At("RETURN"), cancellable = true)
    private static void ansViewmodel$capeOnElytra(
        LivingEntity var0, EquipmentSlot var1, ItemStack var2, float var3, GeoRenderState var4, boolean var5, CallbackInfoReturnable<Attachables.Frame> var6
    ) {
        Attachables.Frame var7 = (Attachables.Frame)var6.getReturnValue();
        if (var7 != null && var1 == EquipmentSlot.CHEST && var0 instanceof AbstractClientPlayer var8) {
            Identifier var9 = capeTexture(var8);
            if (var9 != null) {
                FrameResult var10 = var7.frame();
                ArrayList var11 = null;

                for (int var12 = 0; var12 < var10.layers().size(); var12++) {
                    RenderLayer var13 = var10.layers().get(var12);
                    String var14 = var13.texture().getPath();
                    if (var14.endsWith("models_armor_elytra.png") || var14.endsWith("equipment/wings/elytra.png")) {
                        if (var11 == null) {
                            var11 = new ArrayList<>(var10.layers());
                        }

                        var11.set(
                            var12,
                            new RenderLayer(
                                var13.model(),
                                var9,
                                var13.materials(),
                                var13.visibility(),
                                var13.tint(),
                                var13.hurtOverlay(),
                                var13.ignoreLighting(),
                                var13.uvOffsetU(),
                                var13.uvOffsetV(),
                                var13.changeColor(),
                                var13.overlay(),
                                var13.uvScaleU(),
                                var13.uvScaleV()
                            )
                        );
                    }
                }

                if (var11 != null) {
                    FrameResult var15 = new FrameResult(var10.pose(), List.copyOf(var11), var10.scaleX(), var10.scaleY(), var10.scaleZ());
                    var6.setReturnValue(new Attachables.Frame(var7.slot(), var7.javaSlot(), var7.bundle(), var7.runtime(), var15, var7.bindings()));
                }
            }
        }
    }

    private static Identifier capeTexture(AbstractClientPlayer var0) {
        PlayerSkin var1 = var0.getSkin();
        if (var1.elytra() != null) {
            return var1.elytra().texturePath();
        } else {
            return var1.cape() != null && var0.isModelPartShown(PlayerModelPart.CAPE) ? var1.cape().texturePath() : null;
        }
    }
}
