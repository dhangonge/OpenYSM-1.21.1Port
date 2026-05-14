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

public final class AuthModelsCapabilityProvider implements ICapabilityProvider<Entity, Void, AuthModelsCapability> {

    public static final EntityCapability<AuthModelsCapability, Void> AUTH_MODELS_CAP =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, "own_models"), AuthModelsCapability.class);

    public static final AuthModelsCapabilityProvider INSTANCE = new AuthModelsCapabilityProvider();

    private final ConcurrentHashMap<UUID, AuthModelsCapability> cache = new ConcurrentHashMap<>();

    private AuthModelsCapabilityProvider() {}

    @Override
    @Nullable
    public AuthModelsCapability getCapability(Entity entity, Void context) {
        return cache.computeIfAbsent(entity.getUUID(), uuid -> new AuthModelsCapability());
    }

    public ListTag serializeNBT(Entity entity) {
        AuthModelsCapability cap = getCapability(entity, null);
        return cap != null ? cap.serializeNBT() : new ListTag();
    }

    public void deserializeNBT(Entity entity, ListTag nbt) {
        AuthModelsCapability cap = getCapability(entity, null);
        if (cap != null) {
            cap.deserializeNBT(nbt);
        }
    }
}
