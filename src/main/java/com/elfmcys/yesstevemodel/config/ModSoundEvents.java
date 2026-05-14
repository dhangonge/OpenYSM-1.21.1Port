package com.elfmcys.yesstevemodel.config;

import com.elfmcys.yesstevemodel.YesSteveModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModSoundEvents {

    public static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, YesSteveModel.MOD_ID);

    public static final SoundEvent CUSTOM_SOUND = createSoundEvent("custom");

    private static SoundEvent createSoundEvent(String str) {
        SoundEvent soundEventCreateFixedRangeEvent = SoundEvent.createFixedRangeEvent(ResourceLocation.fromNamespaceAndPath(YesSteveModel.MOD_ID, str), 16.0f);
        REGISTER.register(str, () -> soundEventCreateFixedRangeEvent);
        return soundEventCreateFixedRangeEvent;
    }
}