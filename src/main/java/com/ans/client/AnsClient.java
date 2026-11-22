package com.ans.client;

import com.ans.Ans;
import com.ans.client.attachable.AttachableQueries;
import com.ans.client.attachable.Attachables;
import com.ans.client.dev.DevSelfTest;
import com.ans.client.dev.ScenarioTest;
import com.ans.client.gecko.AnsGeckoLoader;
import com.ans.client.mob.AnimatedMobs;
import com.ans.client.mob.AnsMobs;
import com.ans.client.molang.BedrockQueries;
import com.ans.client.particle.BedrockParticles;
import com.ans.client.settings.AnsSettings;
import com.ans.client.sound.PackSounds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

public class AnsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BedrockQueries.install();
        AttachableQueries.install();
        Attachables.installHook();
        AnsGeckoLoader.install();
        AnsMobs.registerAll();
        AnimatedMobs.registerRenderers();
        PackSounds.init();
        BedrockParticles.init();
        AnsSettings.init();
        registerTexturePack();
        DevSelfTest.init();
        ScenarioTest.init();
        Ans.LOGGER.info("[ans] Motion & Bits client initialised");
    }

    private static void registerTexturePack() {
        FabricLoader.getInstance().getModContainer("ans").ifPresent(mod -> {
            for (String[] p : new String[][]{{"ans_textures", "ans.resourcepack.textures"}, {"ans_colors", "ans.resourcepack.colors"}}) {
                boolean ok = ResourceLoader.registerBuiltinPack(Ans.id(p[0]), mod, Component.translatable(p[1]), PackActivationType.DEFAULT_ENABLED);
                if (!ok) {
                    Ans.LOGGER.warn("[ans] built-in resource pack resourcepacks/{} not found: run the converter (texturepack.py)", p[0]);
                }
            }
        });
    }
}
