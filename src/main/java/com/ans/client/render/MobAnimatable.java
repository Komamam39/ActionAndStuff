package com.ans.client.render;

import com.ans.client.mob.AnimatedMob;
import com.geckolib.animatable.GeoReplacedEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public final class MobAnimatable implements GeoReplacedEntity {
    private final AnimatedMob<?> mob;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MobAnimatable(AnimatedMob<?> mob) {
        this.mob = mob;
    }

    public AnimatedMob<?> mob() {
        return this.mob;
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
