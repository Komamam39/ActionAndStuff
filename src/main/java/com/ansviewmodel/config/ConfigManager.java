package com.ansviewmodel.config;

import com.ansviewmodel.AnsViewmodel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import net.fabricmc.loader.api.FabricLoader;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ViewmodelConfig CONFIG = new ViewmodelConfig();
    private static Path path;

    private ConfigManager() {
    }

    public static ViewmodelConfig get() {
        return CONFIG;
    }

    public static Path path() {
        if (path == null) {
            path = FabricLoader.getInstance().getConfigDir().resolve("ans-viewmodel.json");
        }

        return path;
    }

    public static void load() {
        Path var0 = path();
        if (!Files.isRegularFile(var0)) {
            CONFIG.resetAll();
            save();
            AnsViewmodel.LOGGER.info("[ans_viewmodel] No config yet; wrote defaults to {}", var0);
        } else {
            try (BufferedReader var1 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
                JsonObject var2 = JsonParser.parseReader(var1).getAsJsonObject();
                ViewmodelConfig var3 = new ViewmodelConfig();
                if (var2.has("enabled")) {
                    var3.setEnabled(var2.get("enabled").getAsBoolean());
                }

                String[] var4 = new String[]{"right", "left"};

                for (int var5 = 0; var5 < 2; var5++) {
                    JsonElement var6 = var2.get(var4[var5]);
                    JsonObject var7 = var6 != null && var6.isJsonObject() ? var6.getAsJsonObject() : var2;

                    for (ViewmodelSetting var11 : ViewmodelSetting.VALUES) {
                        JsonElement var12 = var7.get(var11.section.jsonKey);
                        if (var12 != null && var12.isJsonObject()) {
                            JsonElement var13 = var12.getAsJsonObject().get(var11.axis.jsonKey());
                            if (var13 != null && var13.isJsonPrimitive()) {
                                var3.set(var5, var11, var13.getAsFloat());
                            }
                        }
                    }
                }

                CONFIG.copyFrom(var3);
                AnsViewmodel.LOGGER.info("[ans_viewmodel] Loaded {}: {}", var0, CONFIG);
            } catch (Exception var16) {
                AnsViewmodel.LOGGER.warn("[ans_viewmodel] Could not read {} ({}); using defaults", var0, var16.toString());
                CONFIG.resetAll();
                CONFIG.setEnabled(true);
            }
        }
    }

    public static void save() {
        Path var0 = path();
        JsonObject var1 = new JsonObject();
        var1.addProperty(
            "_comment",
            "Motion & Bits Viewmodel. offset: Bedrock pixels (-50..50). scale: additive, effective scale = 1 + value (-1..1). rotation: degrees (-50..50). All zero = unmodified Motion & Bits. 'right' and 'left' are the two arms."
        );
        var1.addProperty("enabled", CONFIG.enabled());
        String[] var2 = new String[]{"right", "left"};

        for (int var3 = 0; var3 < 2; var3++) {
            JsonObject var4 = new JsonObject();

            for (ViewmodelSetting.Section var8 : ViewmodelSetting.Section.values()) {
                JsonObject var9 = new JsonObject();

                for (ViewmodelSetting var13 : ViewmodelSetting.VALUES) {
                    if (var13.section == var8) {
                        var9.addProperty(var13.axis.jsonKey(), CONFIG.get(var3, var13));
                    }
                }

                var4.add(var8.jsonKey, var9);
            }

            var1.add(var2[var3], var4);
        }

        try {
            Files.createDirectories(var0.getParent());
            Path var17 = var0.resolveSibling(var0.getFileName() + ".tmp");

            try (BufferedWriter var18 = Files.newBufferedWriter(var17, StandardCharsets.UTF_8)) {
                GSON.toJson(var1, var18);
            }

            Files.move(var17, var0, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException var16) {
            AnsViewmodel.LOGGER.error("[ans_viewmodel] Could not save {}", var0, var16);
        }
    }
}
