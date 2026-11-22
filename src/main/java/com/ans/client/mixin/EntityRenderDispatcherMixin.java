package com.ans.client.mixin;

import com.ans.client.mob.player.PlayerRenderState;
import com.ans.client.mob.player.PlayerRendering;
import com.ans.client.render.EffectsOnlyRuntimes;
import java.util.Map;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow
    private Map<EntityType<?>, EntityRenderer<?, ?>> renderers;

    @Inject(
        method = "getRenderer(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/client/renderer/entity/EntityRenderer;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void ans$playerRenderer(Entity var1, CallbackInfoReturnable<EntityRenderer<?, ?>> var2) {
        if (var1 instanceof AbstractClientPlayer var3 && PlayerRendering.replaces(var3)) {
            EntityRenderer var4 = this.renderers.get(var1.getType());
            if (var4 != null) {
                var2.setReturnValue(var4);
            }
        }
    }

    @Inject(
        method = "getRenderer(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)Lnet/minecraft/client/renderer/entity/EntityRenderer;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void ans$playerStateRenderer(EntityRenderState var1, CallbackInfoReturnable<EntityRenderer<?, ?>> var2) {
        if (var1 instanceof PlayerRenderState) {
            EntityRenderer var3 = this.renderers.get(var1.entityType);
            if (var3 != null) {
                var2.setReturnValue(var3);
            }
        }
    }

    @Inject(method = "extractEntity(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;", at = @At("HEAD"))
    private void ans$effectsOnly(Entity var1, float var2, CallbackInfoReturnable<EntityRenderState> var3) {
        EffectsOnlyRuntimes.extracted(var1, var2);
    }
}
