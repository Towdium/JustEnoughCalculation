package me.towdium.jecalculation.event;

import me.towdium.jecalculation.model.ModelFluidContainer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraftforge.client.event.ModelBakeEvent;

/**
 * Author: Towdium
 * Date:   2016/7/22.
 */
public class ModelEventHandler {
    //@SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event) {
        IBakedModel existingModel = event.getModelRegistry().getObject(ModelFluidContainer.modelResourceLocation);
        if (!(existingModel instanceof ModelFluidContainer)) {
            ModelFluidContainer customModel = new ModelFluidContainer(existingModel, null, 0);
            event.getModelRegistry().putObject(ModelFluidContainer.modelResourceLocation, customModel);
        }
    }
}
