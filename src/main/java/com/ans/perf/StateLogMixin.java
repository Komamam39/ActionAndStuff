package com.ans.perf;

import com.ans.client.bedrock.EntityBundle;
import com.ans.client.bedrock.EntityRuntime;
import com.ans.client.molang.MolangContext;
import com.ans.client.molang.MolangVars;
import java.util.Set;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.ans.client.bedrock.EntityRuntime$ControllerPlayer", remap = false)
public abstract class StateLogMixin {
    @Unique
    private static final Logger ANS_LOG = LoggerFactory.getLogger("ans_swing");
    @Unique
    private static final boolean ANS_ON = !"false".equalsIgnoreCase(System.getProperty("ans.swingLog", "false"));
    @Unique
    private static final Set<String> ANS_STATES = Set.of(
        "fmriac", "zefcct", "thdqsk", "eqlzfq", "iovhzd", "iyrdcv", "punch", "muqdev", "tggbpq", "default", "mine", "vvgaoj"
    );
    @Shadow
    @Final
    EntityRuntime this$0;
    @Shadow
    @Final
    EntityBundle.ControllerDef def;

    @Inject(method = "enter", at = @At("HEAD"))
    private void ans$logEnter(EntityBundle.State var1, CallbackInfoReturnable<?> var2) {
        if (ANS_ON && var1 != null && ANS_STATES.contains(var1.name())) {
            if (this.def != null && ("ptaudk".equals(this.def.key()) || "mbpzen".equals(this.def.key()))) {
                EntityRuntime var3 = this.this$0;
                if ("player".equals(var3.mobId())) {
                    MolangContext var4 = ((RuntimeCtxAccessor)(Object)var3).ans$ctx();
                    if (var4.actor != null && var4.actor == Minecraft.getInstance().player) {
                        ANS_LOG.info(
                            "[ans_swing] estado {} ctrl={} ms={} pitch={} runtime={} 1a_persona={} tick={} pt={} attack_time={}",
                            var1.name(),
                            this.def == null ? "?" : this.def.key(),
                            System.currentTimeMillis() % 100000L,
                            String.format("%.0f", var4.actor.getXRot()),
                            Integer.toHexString(System.identityHashCode(var3)),
                            var3.isFirstPerson(),
                            var4.actor.tickCount,
                            String.format("%.2f", var4.partialTick),
                            String.format("%.3f", var3.variables().getNum(MolangVars.id("attack_time")))
                        );
                    }
                }
            }
        }
    }
}
