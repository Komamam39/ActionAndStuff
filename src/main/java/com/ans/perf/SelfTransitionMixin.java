package com.ans.perf;

import com.ans.client.bedrock.EntityBundle;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.ans.client.bedrock.EntityRuntime$ControllerPlayer", remap = false)
public abstract class SelfTransitionMixin {
    @Unique
    private static final boolean ANS_ON = !"false".equalsIgnoreCase(System.getProperty("ans.selfTransitionKeepsEntry", "true"));
    @Unique
    private EntityBundle.State ans$entered;

    @Redirect(method = "enter", at = @At(value = "INVOKE", target = "Lcom/ans/client/bedrock/EntityBundle$State;onEntry()Ljava/util/List;"))
    private List<?> ans$onEntry(EntityBundle.State var1) {
        boolean var2 = ANS_ON && var1 == this.ans$entered;
        this.ans$entered = var1;
        return var2 ? List.of() : var1.onEntry();
    }
}
