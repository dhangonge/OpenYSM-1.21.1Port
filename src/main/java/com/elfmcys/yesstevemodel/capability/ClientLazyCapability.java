package com.elfmcys.yesstevemodel.capability;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class ClientLazyCapability {

    private final Entity entity;

    public ClientLazyCapability(Entity entity) {
        this.entity = entity;
    }

    public VehicleCapability getEntityRenderCapability() {
        return entity.getCapability(VehicleCapabilityProvider.VEHICLE_CAP, null);
    }

    @Nullable
    public ProjectileCapability getProjectileCapability() {
        return entity.getCapability(ProjectileCapabilityProvider.PROJECTILE_CAP, null);
    }
}
