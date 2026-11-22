package com.ans.perf;

import com.ans.client.bedrock.EntityRuntime;
import com.ans.client.molang.MolangContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityRuntime.class, remap = false)
public interface RuntimeCtxAccessor {
    @Accessor("ctx")
    MolangContext ans$ctx();
}
