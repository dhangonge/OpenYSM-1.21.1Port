package com.elfmcys.yesstevemodel.capability;

import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class StarModelsCapabilityProvider implements ICapabilityProvider<Entity, Void, StarModelsCapability> {

    public static final EntityCapability<StarModelsCapability, Void> STAR_MODELS_CAP =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "star_models"), StarModelsCapability.class);

    public static final StarModelsCapabilityProvider INSTANCE = new StarModelsCapabilityProvider();

    private final ConcurrentHashMap<UUID, StarModelsCapability> cache = new ConcurrentHashMap<>();

    private StarModelsCapabilityProvider() {}

    @Override
    @Nullable
    public StarModelsCapability getCapability(Entity entity, Void context) {
        return cache.computeIfAbsent(entity.getUUID(), uuid -> new StarModelsCapability());
    }

    public ListTag serializeNBT(Entity entity) {
        StarModelsCapability cap = getCapability(entity, null);
        return cap != null ? cap.serializeNBT() : new ListTag();
    }

    public void deserializeNBT(Entity entity, ListTag nbt) {
        StarModelsCapability cap = getCapability(entity, null);
        if (cap != null) {
            cap.deserializeNBT(nbt);
        }
    }
}
