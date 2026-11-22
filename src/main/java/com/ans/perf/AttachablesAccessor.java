package com.ans.perf;

import com.ans.client.attachable.Attachables;
import com.ans.client.bedrock.EntityRuntime;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Attachables.class, remap = false)
public interface AttachablesAccessor {
    @Invoker("ownerRuntime")
    static EntityRuntime ans$ownerRuntime(Entity var0) {
        throw new AssertionError();
    }
}
