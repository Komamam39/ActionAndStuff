package com.ans.perf;

import com.ans.client.attachable.AttachableQueries;
import com.ans.client.bedrock.EntityRuntime;
import com.ans.client.molang.MolangQueries;
import java.util.Locale;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AttachableQueries.class, remap = false)
public abstract class OwnerIdentifierMixin {
    @Inject(method = "install", at = @At("TAIL"))
    private static void ans$ownerIdentifier(CallbackInfo var0) {
        MolangQueries.register("is_owner_identifier_any", var0x -> var1 -> {
            String var3 = null;
            String var2;
            if (var1.attachable != null) {
                var2 = var1.attachable.ownerIdentifier();
            } else {
                if (var1.actor == null) {
                    return 0.0;
                }

                var2 = var1.owner instanceof EntityRuntime var4 ? "minecraft:" + var4.mobId() : null;
                var3 = BuiltInRegistries.ENTITY_TYPE.getKey(var1.actor.getType()).toString();
                if (var2 == null) {
                    var2 = var3;
                }
            }

            for (String var7 : MolangQueries.strings(var0x, 0, var1)) {
                String var8 = (var7.contains(":") ? var7 : "minecraft:" + var7).toLowerCase(Locale.ROOT);
                if (var8.equals(var2) || var8.equals(var3)) {
                    return 1.0;
                }
            }

            return 0.0;
        });
    }
}
