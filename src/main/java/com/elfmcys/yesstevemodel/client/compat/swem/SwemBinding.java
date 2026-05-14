package com.elfmcys.yesstevemodel.client.compat.swem;

import com.alaharranhonor.swem.entity.horse.AbstractSwemHorse;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.context.IContext;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.util.StringPool;
import com.elfmcys.yesstevemodel.client.animation.molang.CtrlBinding;
import com.google.common.collect.Maps;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Locale;

public class SwemBinding {

    private static final EnumMap<AbstractSwemHorse.Gait, String> GAIT_NAMES = Maps.newEnumMap(AbstractSwemHorse.Gait.class);

    public static void registerBindings(CtrlBinding binding) {
        binding.livingEntityVar("swem_is_ride", it -> {
            return it.entity().getVehicle() instanceof AbstractSwemHorse;
        });
        binding.livingEntityVar("swem_state", SwemBinding::getHorseGait);
    }

    @Nullable
    public static String getHorseGait(IContext<LivingEntity> context) {
        Entity vehicle = context.entity().getVehicle();
        if (vehicle instanceof AbstractSwemHorse sWEMHorseEntityBase) {
            AbstractSwemHorse.Gait gait = sWEMHorseEntityBase.getGait();
            double jumpHeight = sWEMHorseEntityBase.jumpHeight;
            if (jumpHeight > 0.0d) {
                return "jump_lv" + ((Math.min(Mth.ceil(jumpHeight), 5) - 1) + 1);
            }
            if (SWEMHorseUtils.isRiding(sWEMHorseEntityBase)) {
                return "idle";
            }
            return GAIT_NAMES.computeIfAbsent(gait, gait2 -> gait2.name().toLowerCase(Locale.ENGLISH));
        }
        return StringPool.EMPTY;
    }
}