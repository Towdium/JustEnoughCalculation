package pers.towdium.just_enough_calculation.event;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pers.towdium.just_enough_calculation.model.ModelFluidContainer;

/**
 * Author: Towdium
 * Date:   2016/7/22.
 */
public class ModelBakeEventHandler {
    @SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event) {
        IBakedModel existingModel = event.getModelRegistry().getObject(ModelFluidContainer.modelResourceLocation);
        if (!(existingModel instanceof ModelFluidContainer)) {
            ModelFluidContainer customModel = new ModelFluidContainer(existingModel, null);
            event.getModelRegistry().putObject(ModelFluidContainer.modelResourceLocation, customModel);
        }
    }
}
