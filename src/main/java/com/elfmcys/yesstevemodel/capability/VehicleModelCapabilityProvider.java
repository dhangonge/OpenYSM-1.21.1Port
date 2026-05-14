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

public final class VehicleModelCapabilityProvider implements ICapabilityProvider<Entity, Void, VehicleModelCapability> {

    public static final EntityCapability<VehicleModelCapability, Void> VEHICLE_MODEL_CAP =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "vehicle_model_id"), VehicleModelCapability.class);

    public static final VehicleModelCapabilityProvider INSTANCE = new VehicleModelCapabilityProvider();

    private final ConcurrentHashMap<UUID, VehicleModelCapability> cache = new ConcurrentHashMap<>();

    private VehicleModelCapabilityProvider() {}

    @Override
    @Nullable
    public VehicleModelCapability getCapability(Entity entity, Void context) {
        return cache.computeIfAbsent(entity.getUUID(), uuid -> new VehicleModelCapability());
    }

    public CompoundTag serializeNBT(Entity entity) {
        VehicleModelCapability cap = getCapability(entity, null);
        return cap != null ? cap.serializeNBT() : new CompoundTag();
    }

    public void deserializeNBT(Entity entity, CompoundTag nbt) {
        VehicleModelCapability cap = getCapability(entity, null);
        if (cap != null) {
            cap.deserializeNBT(nbt);
        }
    }
}
