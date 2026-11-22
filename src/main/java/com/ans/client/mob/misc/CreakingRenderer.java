package com.ans.client.mob.misc;

import com.ans.client.mob.AnimatedMob;
import com.ans.client.render.MobAnimatable;
import com.ans.client.render.ReplacedMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.creaking.Creaking;
import org.jspecify.annotations.Nullable;

public final class CreakingRenderer extends ReplacedMobRenderer<Creaking> {
    public CreakingRenderer(Context context, AnimatedMob<Creaking> mob, @Nullable EntityRendererProvider<Creaking> vanilla) {
        super(context, mob, vanilla);
    }

    @Override
    protected void extractLivingEntityRenderState(LivingEntity entity, LivingEntityRenderState state, float partialTick, ItemModelResolver items) {
        super.extractLivingEntityRenderState(entity, state, partialTick, items);
        if (entity instanceof Creaking c && c.isTearingDown()) {
            state.deathTime = 0.0F;
            state.hasRedOverlay = false;
        }
    }

    public int getPackedOverlay(MobAnimatable animatable, Creaking entity, float u, float partialTick) {
        return entity.isTearingDown()
            ? OverlayTexture.pack(OverlayTexture.u(u), OverlayTexture.v(false))
            : super.getPackedOverlay(animatable, entity, u, partialTick);
    }
}
