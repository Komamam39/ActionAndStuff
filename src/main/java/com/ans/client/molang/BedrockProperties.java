package com.ans.client.molang;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.Nullable;

public final class BedrockProperties {
    private static final Map<String, Function<Entity, @Nullable Object>> GLOBAL = new ConcurrentHashMap<>();
    private static final Map<String, Map<EntityType<?>, Function<Entity, @Nullable Object>>> PER_TYPE = new ConcurrentHashMap<>();
    private static final List<DataComponentType<? extends Holder<?>>> CLIMATE_COMPONENTS = List.of(
        DataComponents.COW_VARIANT, DataComponents.PIG_VARIANT, DataComponents.CHICKEN_VARIANT
    );

    private BedrockProperties() {
    }

    public static void register(String name, Function<Entity, @Nullable Object> provider) {
        GLOBAL.put(name, provider);
    }

    public static <E extends Entity> void register(EntityType<E> type, String name, Function<E, @Nullable Object> provider) {
        PER_TYPE.computeIfAbsent(name, var0x -> new ConcurrentHashMap<>()).put(type, e -> provider.apply((E)e));
    }

    public static Object get(Entity entity, String name) {
        if (entity == null) {
            return null;
        }

        Map<EntityType<?>, Function<Entity, Object>> perType = PER_TYPE.get(name);
        if (perType != null) {
            Function<Entity, Object> f = perType.get(entity.getType());
            if (f != null) {
                return f.apply(entity);
            }
        }

        Function<Entity, Object> f = GLOBAL.get(name);
        return f == null ? null : f.apply(entity);
    }

    public static <T> @Nullable String keyPath(@Nullable Holder<T> holder) {
        return holder == null ? null : holder.unwrapKey().map(ResourceKey::identifier).map(Identifier::getPath).orElse(null);
    }

    private static @Nullable Object climateVariant(Entity entity) {
        for (DataComponentType<? extends Holder<?>> type : CLIMATE_COMPONENTS) {
            Holder<?> holder = entity.get(type);
            if (holder != null) {
                return keyPath(holder);
            }
        }

        return null;
    }

    static {
        register("minecraft:climate_variant", BedrockProperties::climateVariant);
    }
}
