package com.ans.perfutil;

import com.ans.client.molang.PackSettings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.Resource;

public final class MultiSelect {
    public static final String NONE = "none";
    private static Map<String, List<String>> defaults;
    private static final Map<String, Object[]> CACHE = new ConcurrentHashMap<>();

    private MultiSelect() {
    }

    private static synchronized Map<String, List<String>> defaults() {
        if (defaults == null) {
            HashMap var0 = new HashMap();

            try {
                Optional var1 = Minecraft.getInstance().getResourceManager().getResource(PackSettings.DEFAULTS);
                if (var1.isPresent()) {
                    try (BufferedReader var2 = ((Resource)var1.get()).openAsReader()) {
                        JsonObject var3 = JsonParser.parseReader(var2).getAsJsonObject();

                        for (Entry var5 : var3.entrySet()) {
                            if (((JsonElement)var5.getValue()).isJsonObject()) {
                                JsonObject var6 = ((JsonElement)var5.getValue()).getAsJsonObject();
                                JsonElement var7 = var6.get("type");
                                if (var7 != null && "multiselect".equals(var7.getAsString())) {
                                    ArrayList var8 = new ArrayList();
                                    if (var6.has("defaults")) {
                                        var6.getAsJsonArray("defaults").forEach(var1x -> var8.add(var1x.getAsString()));
                                    }

                                    var0.put((String)var5.getKey(), List.copyOf(var8));
                                }
                            }
                        }
                    }
                }
            } catch (Exception var11) {
            }

            defaults = var0;
        }

        return defaults;
    }

    public static void invalidate() {
        synchronized (MultiSelect.class) {
            defaults = null;
        }

        CACHE.clear();
    }

    public static boolean isMulti(String var0) {
        return defaults().containsKey(var0);
    }

    public static Set<String> get(String var0) {
        String var1 = PackSettings.value(var0);
        LinkedHashSet var2 = new LinkedHashSet();
        if (var1.isEmpty()) {
            var2.addAll(defaults().getOrDefault(var0, List.of()));
        } else if (!"none".equals(var1)) {
            for (String var6 : var1.split(",")) {
                if (!var6.isBlank()) {
                    var2.add(var6.strip());
                }
            }
        }

        return var2;
    }

    public static String encode(Set<String> var0) {
        return var0.isEmpty() ? "none" : String.join(",", var0);
    }

    public static boolean contains(String var0, String var1) {
        String var2 = PackSettings.value(var0);
        Object[] var3 = CACHE.get(var0);
        if (var3 == null || !var2.equals(var3[0])) {
            var3 = new Object[]{var2, get(var0)};
            CACHE.put(var0, var3);
        }

        return ((Set)var3[1]).contains(var1);
    }
}
