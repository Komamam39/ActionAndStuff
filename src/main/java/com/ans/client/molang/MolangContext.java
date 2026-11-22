package com.ans.client.molang;

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

public final class MolangContext {
    public static final int FLOW_NORMAL = 0;
    public static final int FLOW_RETURN = 1;
    public static final int FLOW_BREAK = 2;
    public static final int FLOW_CONTINUE = 3;
    private static final ThreadLocal<MolangContext> CURRENT = new ThreadLocal<>();
    private static final MolangContext EMPTY = new MolangContext(new MolangScope());
    public @Nullable LivingEntity entity;
    public @Nullable Entity actor;
    public final MolangScope vars;
    public final MolangScope temps = new MolangScope();
    public float partialTick;
    public double deltaTime;
    public double animTime;
    public double clock;
    public long frame;
    public MolangContext.@Nullable ControllerView controller;
    public @Nullable Map<String, List<String>> arrays;
    public double thisValue;
    public boolean keepTemps;
    public final Object2DoubleOpenHashMap<String> contextVars = new Object2DoubleOpenHashMap<>();
    public final Map<Object, Object> frameCache = new Object2ObjectOpenHashMap<>();
    public @Nullable Object owner;
    public @Nullable AttachableContext attachable;
    public boolean firstPerson;
    int flow;
    @Nullable Object returned;

    public MolangContext(MolangScope vars) {
        this.vars = vars;
    }

    public static MolangContext current() {
        MolangContext c = CURRENT.get();
        return c != null ? c : EMPTY;
    }

    public @Nullable MolangContext enter() {
        MolangContext previous = CURRENT.get();
        CURRENT.set(this);
        return previous;
    }

    public static void restore(@Nullable MolangContext previous) {
        if (previous == null) {
            CURRENT.remove();
        } else {
            CURRENT.set(previous);
        }
    }

    public <T> T cached(Object key, Supplier<T> supplier) {
        Object v = this.frameCache.get(key);
        if (v == null) {
            v = supplier.get();
            this.frameCache.put(key, v);
        }

        return (T)v;
    }

    public interface BoneSource {
        double @Nullable [] boneOrientationTrs(String var1);

        double @Nullable [] boneOrigin(String var1);
    }

    public interface ControllerView {
        double stateTime();

        boolean anyAnimationFinished();

        boolean allAnimationsFinished();
    }
}
