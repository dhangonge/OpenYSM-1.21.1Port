package com.elfmcys.yesstevemodel.client.compat.swem;

import com.alaharranhonor.swem.entity.horse.AbstractSwemHorse;
import com.google.common.collect.Maps;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Locale;

public class SWEMHorseUtils {

    private static final EnumMap<AbstractSwemHorse.Gait, String> GAIT_NAMES = Maps.newEnumMap(AbstractSwemHorse.Gait.class);

    @Nullable
    public static String getGaitName(LivingEntity livingEntity) {
        Entity vehicle = livingEntity.getVehicle();
        if (vehicle instanceof AbstractSwemHorse sWEMHorseEntityBase) {
            AbstractSwemHorse.Gait gait = sWEMHorseEntityBase.getGait();
            double jumpHeight = sWEMHorseEntityBase.jumpHeight;
            if (jumpHeight > 0.0d) {
                return "swem:jump_lv" + ((Math.min(Mth.ceil(jumpHeight), 5) - 1) + 1);
            }
            if (isRiding(sWEMHorseEntityBase)) {
                return "swem:idle";
            }
            return GAIT_NAMES.computeIfAbsent(gait, gait2 -> {
                return "swem:" + gait2.name().toLowerCase(Locale.ENGLISH);
            });
        }
        return null;
    }

    public static boolean isRiding(AbstractSwemHorse sWEMHorseEntityBase) {
        double x = sWEMHorseEntityBase.getX() - sWEMHorseEntityBase.xOld;
        double z = sWEMHorseEntityBase.getZ() - sWEMHorseEntityBase.zOld;
        return Math.sqrt((x * x) + (z * z)) <= 0.0d;
    }
}