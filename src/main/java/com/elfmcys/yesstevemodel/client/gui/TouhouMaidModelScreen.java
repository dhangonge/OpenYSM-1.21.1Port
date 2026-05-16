package com.elfmcys.yesstevemodel.client.gui;

import com.elfmcys.yesstevemodel.client.ClientModelManager;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.capability.MaidCapability;
import com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.capability.MaidCapabilityProvider;
import com.elfmcys.yesstevemodel.resource.models.Metadata;
import com.elfmcys.yesstevemodel.client.gui.button.ModelButton;
import com.elfmcys.yesstevemodel.client.gui.button.TouhouMaidModelButton;
import com.elfmcys.yesstevemodel.client.entity.PlayerPreviewEntity;
import com.elfmcys.yesstevemodel.client.model.ModelAssembly;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.util.StringPool;
import com.elfmcys.yesstevemodel.util.FileTypeUtil;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

public class TouhouMaidModelScreen extends PlayerModelScreen {

    private final EntityMaid maid;

    public TouhouMaidModelScreen(EntityMaid entityMaid) {
        this.maid = entityMaid;
    }

    @Override
    public ModelButton createModelButton(int x, int y, boolean isAuthLocked, PlayerPreviewEntity previewEntity, ModelAssembly modelAssembly) {
        return new TouhouMaidModelButton(x, y, isAuthLocked, previewEntity, modelAssembly, this.maid);
    }

    @Override
    public PlayerTextureScreen createTextureScreen(PlayerModelScreen modelScreen, String str, ModelAssembly modelAssembly) {
        MaidCapability cap = this.maid.getCapability(MaidCapabilityProvider.MAID_CAP);
        return new TouhouMaidTextureScreen(modelScreen, str, Objects.requireNonNullElse(cap != null ? cap.getModelAssembly() : null, modelAssembly), this.maid);
    }

    @Override
    public ModelInfoScreen createModelInfoScreen(PlayerModelScreen modelScreen, ModelAssembly modelAssembly) {
        MaidCapability cap = this.maid.getCapability(MaidCapabilityProvider.MAID_CAP);
        return new ModelInfoScreen(modelScreen, Objects.requireNonNullElse(cap != null ? cap.getModelAssembly() : null, modelAssembly));
    }

    @Override
    public void renderModelPreview(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.enableScissor(this.guiLeft + 5, this.guiTop + 200, this.guiLeft + 130, this.guiTop + 371);
        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, this.guiLeft + 67, this.guiTop + 190, 70, 0, 0, (float)((this.guiLeft + 67) - mouseX), (float)(((this.guiTop + 180) - 95) - mouseY), partialTick, this.maid);
        guiGraphics.disableScissor();
        MaidCapability cap = this.maid.getCapability(MaidCapabilityProvider.MAID_CAP);
        if (cap != null) {
            List<FormattedCharSequence> listSplit = this.font.split(FormattedText.of(ClientModelManager.getModelContext(cap.getModelId()).map(it -> {
                Metadata metadata2 = it.getModelData().getExtraInfo();
                if (metadata2 != null) {
                    return ModelMetadataPresenter.getLocalizedModelString(it, "metadata.name", metadata2.getName());
                }
                return StringPool.EMPTY;
            }).filter(charSequence -> {
                return StringUtils.isNoneBlank(charSequence);
            }).orElse(FileTypeUtil.getNameWithoutArchiveExtension(cap.getModelId()))), 125);
            int lineY = this.guiTop + 205;
            for (FormattedCharSequence formattedCharSequence : listSplit) {
                guiGraphics.drawString(this.font, formattedCharSequence, this.guiLeft + ((135 - this.font.width(formattedCharSequence)) / 2), lineY, 15986656);
                lineY += 10;
            }
        }
    }
}