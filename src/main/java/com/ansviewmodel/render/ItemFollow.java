package com.ansviewmodel.render;

import com.ans.client.bedrock.BoneTransforms;
import com.ans.client.bedrock.FrameResult;
import com.ans.client.bedrock.MobPose;
import com.ans.client.bedrock.RenderLayer;
import com.ansviewmodel.AnsViewmodel;
import com.ansviewmodel.config.ConfigManager;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public final class ItemFollow {
    private static final Matrix4f OUTER = new Matrix4f();
    private static final Matrix4f[] CAMERA_DELTA = new Matrix4f[]{new Matrix4f(), new Matrix4f()};
    private static final boolean[] VALID = new boolean[2];
    private static final Matrix4f BLENDED = new Matrix4f();
    private static boolean outerCaptured;
    private static boolean warnedMissingBone;

    private ItemFollow() {
    }

    public static void beginDraw(PoseStack poseStack) {
        VALID[0] = VALID[1] = false;
        outerCaptured = ConfigManager.get().active();
        if (outerCaptured) {
            OUTER.set(poseStack.last().pose());
        }
    }

    public static void captureModelRoot(PoseStack poseStack, FrameResult frame) {
        VALID[0] = VALID[1] = false;
        if (outerCaptured && ConfigManager.get().active()) {
            Matrix4f rel = new Matrix4f(OUTER).invert().mul(poseStack.last().pose());
            if (isFinite(rel)) {
                Matrix4f relInverse = new Matrix4f(rel).invert();
                MobPose pose = frame.pose();

                for (int arm = 0; arm < 2; arm++) {
                    String name = ArmPoseDelta.ARM_BONES[arm];
                    float[] current = pose.get(name);
                    if (current != null) {
                        GeoBone bone = findBone(frame, name);
                        if (bone == null) {
                            if (!warnedMissingBone) {
                                warnedMissingBone = true;
                                AnsViewmodel.LOGGER
                                    .warn("[ans_viewmodel] A&S player geometry has no '{}' bone; vanilla-drawn held items will not follow the arm", name);
                            }
                        } else {
                            float[] original = withoutContribution(current, ArmPoseDelta.contribution(arm));
                            Matrix4f modified = boneMatrix(bone, pose, null, null);
                            Matrix4f unmodified = boneMatrix(bone, pose, name, original);
                            Matrix4f deltaModel = modified.mul(unmodified.invert());
                            Matrix4f deltaCamera = CAMERA_DELTA[arm].set(rel).mul(deltaModel).mul(relInverse);
                            VALID[arm] = isFinite(deltaCamera);
                        }
                    }
                }
            }
        }
    }

    public static @Nullable Matrix4f forSide(HumanoidArm side) {
        int arm = side == HumanoidArm.RIGHT ? 0 : 1;
        return VALID[arm] ? CAMERA_DELTA[arm] : null;
    }

    public static @Nullable Matrix4f bothHands() {
        if (VALID[0] && VALID[1]) {
            Matrix4f a = CAMERA_DELTA[0];
            Matrix4f b = CAMERA_DELTA[1];
            Vector3f t = a.getTranslation(new Vector3f()).lerp(b.getTranslation(new Vector3f()), 0.5F);
            Vector3f s = a.getScale(new Vector3f()).lerp(b.getScale(new Vector3f()), 0.5F);
            Quaternionf r = a.getUnnormalizedRotation(new Quaternionf()).normalize().slerp(b.getUnnormalizedRotation(new Quaternionf()).normalize(), 0.5F);
            return BLENDED.translationRotateScale(t, r, s);
        } else {
            return VALID[0] ? CAMERA_DELTA[0] : (VALID[1] ? CAMERA_DELTA[1] : null);
        }
    }

    private static float[] withoutContribution(float[] current, float[] contribution) {
        float[] out = (float[])current.clone();

        for (int a = 0; a < 6; a++) {
            out[a] = finite(current[a], 0.0F) - contribution[a];
        }

        for (int a = 6; a < 9; a++) {
            float c = contribution[a];
            out[a] = Math.abs(c) > 1.0E-4F ? finite(current[a], 1.0F) / c : 1.0F;
        }

        return out;
    }

    private static @Nullable GeoBone findBone(FrameResult frame, String name) {
        for (RenderLayer layer : frame.layers()) {
            BakedGeoModel model = BoneTransforms.model(layer.model());
            if (model != null) {
                GeoBone bone = model.getBone(name).orElse(null);
                if (bone != null) {
                    return bone;
                }
            }
        }

        return null;
    }

    private static Matrix4f boneMatrix(GeoBone bone, MobPose pose, @Nullable String overrideBone, float @Nullable [] overrideValues) {
        List<GeoBone> chain = new ArrayList<>();

        for (GeoBone b = bone; b != null; b = b.parent()) {
            chain.add(b);
        }

        Matrix4f m = new Matrix4f();

        for (int i = chain.size() - 1; i >= 0; i--) {
            GeoBone b = chain.get(i);
            float[] v = b.name().equals(overrideBone) ? overrideValues : pose.get(b.name());
            if (v != null) {
                m.translate(-finite(v[3], 0.0F) / 16.0F, finite(v[4], 0.0F) / 16.0F, finite(v[5], 0.0F) / 16.0F);
            }

            m.translate(b.pivotX() / 16.0F, b.pivotY() / 16.0F, b.pivotZ() / 16.0F);
            float rx = b.baseRotX() + (v == null ? 0.0F : finite(v[0], 0.0F));
            float ry = b.baseRotY() + (v == null ? 0.0F : finite(v[1], 0.0F));
            float rz = b.baseRotZ() + (v == null ? 0.0F : finite(v[2], 0.0F));
            m.rotateZYX(rz, ry, rx);
            if (v != null) {
                m.scale(finite(v[6], 1.0F), finite(v[7], 1.0F), finite(v[8], 1.0F));
            }

            m.translate(-b.pivotX() / 16.0F, -b.pivotY() / 16.0F, -b.pivotZ() / 16.0F);
        }

        return m;
    }

    private static float finite(float f, float fallback) {
        return Float.isFinite(f) ? f : fallback;
    }

    private static boolean isFinite(Matrix4f m) {
        return m.isFinite();
    }
}
