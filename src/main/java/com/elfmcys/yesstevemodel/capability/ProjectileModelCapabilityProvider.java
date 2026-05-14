package com.elfmcys.yesstevemodel.capability;

import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ProjectileModelCapabilityProvider implements ICapabilityProvider<Entity, Void, ProjectileModelCapability> {

    public static final EntityCapability<ProjectileModelCapability, Void> PROJECTILE_MODEL =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "projectile_model_id"), ProjectileModelCapability.class);

    public static final ProjectileModelCapabilityProvider INSTANCE = new ProjectileModelCapabilityProvider();

    private final ConcurrentHashMap<UUID, ProjectileModelCapability> cache = new ConcurrentHashMap<>();

    private ProjectileModelCapabilityProvider() {}

    @Override
    @Nullable
    public ProjectileModelCapability getCapability(Entity entity, Void context) {
        return cache.computeIfAbsent(entity.getUUID(), uuid -> new ProjectileModelCapability());
    }

    public CompoundTag serializeNBT(Entity entity) {
        ProjectileModelCapability cap = getCapability(entity, null);
        return cap != null ? cap.serializeNBT() : new CompoundTag();
    }

    public void deserializeNBT(Entity entity, CompoundTag nbt) {
        ProjectileModelCapability cap = getCapability(entity, null);
        if (cap != null) {
            cap.deserializeNBT(nbt);
        }
    }
}
