package com.elfmcys.yesstevemodel.client.gui.button;

import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.capability.MaidCapability;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.capability.MaidCapabilityProvider;
import com.elfmcys.yesstevemodel.client.entity.PlayerPreviewEntity;
import com.elfmcys.yesstevemodel.client.model.ModelAssembly;
import com.elfmcys.yesstevemodel.util.ComponentUtil;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.tartaricacid.touhoulittlemaid.network.message.YsmMaidModelPackage;
import net.minecraft.network.chat.Component;

public class TouhouMaidModelButton extends ModelButton {

    private final EntityMaid maid;

    public TouhouMaidModelButton(int x, int y, boolean isAuthLocked, PlayerPreviewEntity previewEntity, ModelAssembly modelAssembly, EntityMaid entityMaid) {
        super(x, y, isAuthLocked, previewEntity, modelAssembly);
        this.maid = entityMaid;
    }

    @Override
    public void onPress() {
        if (this.isStarred) {
            return;
        }
        Component component = ComponentUtil.getDisplayName(this.renderContext, this.modelIdHolder.getModelId());
        MaidCapability cap = this.maid.getCapability(MaidCapabilityProvider.MAID_CAP);
        if(cap != null) {
                cap.setYsmModel(this.modelIdHolder.getModelId(), this.modelIdHolder.getCurrentTextureName());
                // NetworkHandler.CHANNEL.sendToServer(new YsmMaidModelPackage(this.maid.getId(), this.modelIdHolder.getModelId(), this.modelIdHolder.getCurrentTextureName(), component));
        }

    }
}