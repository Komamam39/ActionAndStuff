package com.ansviewmodel;

import com.ansviewmodel.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public final class AnsViewmodelClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModContainer var1 = FabricLoader.getInstance().getModContainer("ans").orElse(null);
        if (var1 == null) {
            AnsViewmodel.LOGGER.error("[ans_viewmodel] Motion & Bits (mod id 'ans') is not installed; the viewmodel settings do nothing");
        } else {
            AnsViewmodel.LOGGER.info("[ans_viewmodel] Motion & Bits {} found", var1.getMetadata().getVersion().getFriendlyString());
        }

        ConfigManager.load();
    }
}
