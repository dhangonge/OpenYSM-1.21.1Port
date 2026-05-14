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

public final class ModelInfoCapabilityProvider implements ICapabilityProvider<Entity, Void, ModelInfoCapability> {

    public static final EntityCapability<ModelInfoCapability, Void> MODEL_INFO_CAP =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "model_id"), ModelInfoCapability.class);

    public static final ModelInfoCapabilityProvider INSTANCE = new ModelInfoCapabilityProvider();

    private final ConcurrentHashMap<UUID, ModelInfoCapability> cache = new ConcurrentHashMap<>();

    private ModelInfoCapabilityProvider() {}

    @Override
    @Nullable
    public ModelInfoCapability getCapability(Entity entity, Void context) {
        return cache.computeIfAbsent(entity.getUUID(), uuid -> new ModelInfoCapability());
    }

    public CompoundTag serializeNBT(Entity entity) {
        ModelInfoCapability cap = getCapability(entity, null);
        return cap != null ? cap.serializeNBT() : new CompoundTag();
    }

    public void deserializeNBT(Entity entity, CompoundTag nbt) {
        ModelInfoCapability cap = getCapability(entity, null);
        if (cap != null) {
            cap.deserializeNBT(nbt);
        }
    }
}
