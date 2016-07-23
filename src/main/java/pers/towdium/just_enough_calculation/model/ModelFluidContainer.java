package pers.towdium.just_enough_calculation.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import org.apache.commons.lang3.tuple.Pair;
import pers.towdium.just_enough_calculation.util.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.Utilities;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

import static pers.towdium.just_enough_calculation.util.Utilities.createBakedQuadForFace;

/**
 * Author: Towdium
 * Date:   2016/7/22.
 */

@SuppressWarnings("NullableProblems")
public class ModelFluidContainer implements IPerspectiveAwareModel {
    public static final ModelResourceLocation modelResourceLocation = new ModelResourceLocation("je_calculation:itemFluidContainer", "inventory");

    IBakedModel originalModel;
    TextureAtlasSprite particle;

    public ModelFluidContainer(IBakedModel originalModel, TextureAtlasSprite particle) {
        this.originalModel = originalModel;
        this.particle = particle;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> buffer = new ArrayList<>(originalModel.getQuads(state, side, rand));
        if (particle != null) {
            buffer.add(createBakedQuadForFace(0.5f, 5 / 8.0f, 0.5f, 5 / 8.0f, 0.001f - 15 / 32.0f, 0, particle, EnumFacing.SOUTH));
            buffer.add(createBakedQuadForFace(0.5f, 5 / 8.0f, 0.5f, 5 / 8.0f, 0.001f - 15 / 32.0f, 0, particle, EnumFacing.NORTH));
        }
        return buffer;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new ModelFluidContainerOverride();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return originalModel.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        //noinspection deprecation
        return originalModel.getItemCameraTransforms();
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transform;
        try {
            Class c = Class.forName("net.minecraftforge.client.model.ItemLayerModel$BakedItemModel");
            //noinspection unchecked
            transform = ((ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation>) Utilities.getField(c, originalModel, "transforms"));
        } catch (ReflectiveOperationException | RuntimeException e) {
            e.printStackTrace();
            return ((IPerspectiveAwareModel) originalModel).handlePerspective(cameraTransformType);
        }
        return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, transform, cameraTransformType);
    }

    static class ModelFluidContainerOverride extends ItemOverrideList {

        public ModelFluidContainerOverride() {
            super(ImmutableList.of());
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
            Fluid fluid = ItemStackHelper.NBT.getFluid(stack);
            if (fluid != null)
                return new ModelFluidContainer(((ModelFluidContainer) originalModel).originalModel, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getStill().toString()));
            else
                return originalModel;
        }
    }
}
