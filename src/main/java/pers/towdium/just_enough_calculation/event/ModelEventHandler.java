package pers.towdium.just_enough_calculation.event;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pers.towdium.just_enough_calculation.model.ModelFluidContainer;

/**
 * Author: Towdium
 * Date:   2016/7/22.
 */
public class ModelEventHandler {
    @SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event) {
        IBakedModel existingModel = event.getModelRegistry().getObject(ModelFluidContainer.modelResourceLocation);
        if (!(existingModel instanceof ModelFluidContainer)) {
            ModelFluidContainer customModel = new ModelFluidContainer(existingModel, null, 0);
            event.getModelRegistry().putObject(ModelFluidContainer.modelResourceLocation, customModel);
        }
    }

    @SubscribeEvent
    public void onTextureLoad(TextureStitchEvent event) {
        event.getMap().registerSprite(ModelFluidContainer.liquidResourceLocation);
        //event.getMap().registerSprite(ModelFluidContainer.coverResourceLocation);
    }
}
