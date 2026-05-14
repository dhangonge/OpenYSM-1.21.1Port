package com.elfmcys.yesstevemodel.client.compat.slashblade;

import com.elfmcys.yesstevemodel.geckolib3.core.molang.util.StringPool;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;

public class SlashBladeStateAccess {

    @Deprecated
    public static void initialize() {
    }

    public static String getComboState(ISlashBladeState slashBladeState, long j) {

        ComboState comboState = ComboStateRegistry.REGISTRY.get(slashBladeState.getComboSeq());

        if (comboState != null && j > comboState.getTimeoutMS()) {
            return StringPool.EMPTY;
        }
        int timeoutMS = comboState.getTimeoutMS();
        if ("slashblade:standby".equals(slashBladeState.getComboSeq().toString())) {
            timeoutMS -= 553;
        }
        if (j <= timeoutMS) {
            String name = SlashBladeComboHelper.normalizeComboName(slashBladeState.getComboSeq().toString());
            if (name.startsWith("ex_")) {
                name = name.substring(3);
            }
            return "slashblade:" + name;
        }
        return StringPool.EMPTY;
    }
}