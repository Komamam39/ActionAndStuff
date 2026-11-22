package com.ans.client.render;

import com.ans.Ans;
import com.ans.client.bedrock.EntityBundle;
import com.ans.client.bedrock.EntityBundles;
import com.ans.client.bedrock.FrameResult;
import com.ans.client.mob.AnimatedMob;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;

public final class MobGeoModel extends GeoModel<MobAnimatable> {
    private final AnimatedMob<?> mob;

    public MobGeoModel(AnimatedMob<?> mob) {
        this.mob = mob;
    }

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        FrameResult frame = state.getGeckolibData(FrameResult.TICKET);
        if (frame != null && !frame.layers().isEmpty()) {
            return frame.layers().getFirst().model();
        }

        EntityBundle bundle = EntityBundles.get(this.mob.id(), this.mob.bundleFile());
        Identifier geo = bundle == null ? null : bundle.geometry.get("default");
        return geo != null ? geo : AnimatedMob.geoId(this.mob.id());
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        FrameResult frame = state.getGeckolibData(FrameResult.TICKET);
        if (frame != null && !frame.layers().isEmpty()) {
            return frame.layers().getFirst().texture();
        }

        EntityBundle bundle = EntityBundles.get(this.mob.id(), this.mob.bundleFile());
        Identifier tex = bundle == null ? null : bundle.textures.get("default");
        return tex != null ? tex : Ans.id("textures/entity/" + this.mob.id() + "/default.png");
    }

    public Identifier getAnimationResource(MobAnimatable animatable) {
        EntityBundle bundle = EntityBundles.get(this.mob.id(), this.mob.bundleFile());
        return bundle != null && bundle.animationFile != null ? bundle.animationFile : AnimatedMob.geoId(this.mob.id());
    }
}
