package com.elfmcys.yesstevemodel.client.renderer;

import com.elfmcys.yesstevemodel.capability.ProjectileCapability;
import com.elfmcys.yesstevemodel.capability.ProjectileCapabilityProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.projectile.Projectile;

public class CustomProjectileRenderer {
    public static boolean renderProjectile(Projectile projectile, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        ProjectileCapability cap = projectile.getCapability(ProjectileCapabilityProvider.PROJECTILE_CAP);
        if (cap != null && cap.isModelInitialized() && cap.isModelReady()) {
            RendererManager.getProjectileRenderer().render(cap, entityYaw, partialTick, poseStack, multiBufferSource, packedLight);
            return false;
        }
        return true;
    }
}