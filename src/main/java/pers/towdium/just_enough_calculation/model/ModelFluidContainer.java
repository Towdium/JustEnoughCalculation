package pers.towdium.just_enough_calculation.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import org.apache.commons.lang3.tuple.Pair;
import pers.towdium.just_enough_calculation.util.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.Utilities;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Towdium
 * Date:   2016/7/22.
 */

@SuppressWarnings("NullableProblems")
public class ModelFluidContainer implements IPerspectiveAwareModel {
    //public static final ModelResourceLocation varientResourceLocation = new ModelResourceLocation("je_calculation:dynbucket", "inventory");
    public static final ModelResourceLocation modelResourceLocation = new ModelResourceLocation("je_calculation:itemFluidContainer", "inventory");
    public static final ResourceLocation liquidResourceLocation = new ResourceLocation("je_calculation:items/itemFluidContainer_liquid");
    public static final ResourceLocation coverResourceLocation = new ResourceLocation("je_calculation:items/itemFluidContainer_cover");

    int color;
    IBakedModel originalModel;
    TextureAtlasSprite particle;
    ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transform;
    List<BakedQuad> buffer;


    public ModelFluidContainer(IBakedModel originalModel, TextureAtlasSprite particle, int color) {
        this.originalModel = originalModel;
        this.particle = particle;
        this.color = color;
        try {
            Class c = Class.forName("net.minecraftforge.client.model.ItemLayerModel$BakedItemModel");
            //noinspection unchecked
            transform = ((ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation>) Utilities.getField(c, originalModel, "transforms"));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (buffer == null && particle != null) {
            buffer = new ArrayList<>(2);
            TextureAtlasSprite fluid = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(liquidResourceLocation.toString());
            //TextureAtlasSprite cover = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(coverResourceLocation.toString());
            buffer.addAll(ItemTextureQuadConverter.convertTexture(DefaultVertexFormats.ITEM, TRSRTransformation.identity(), fluid, particle, 7.498f / 16f, EnumFacing.NORTH, color));
            buffer.addAll(ItemTextureQuadConverter.convertTexture(DefaultVertexFormats.ITEM, TRSRTransformation.identity(), fluid, particle, 8.502f / 16f, EnumFacing.SOUTH, color));
            //buffer.add(ItemTextureQuadConverter.genQuad(DefaultVertexFormats.ITEM, TRSRTransformation.identity(), 0, 0, 16, 16, 7.496f / 16f, cover, EnumFacing.NORTH, 0xffffffff));
            //buffer.add(ItemTextureQuadConverter.genQuad(DefaultVertexFormats.ITEM, TRSRTransformation.identity(), 0, 0, 16, 16, 8.504f / 16f, cover, EnumFacing.SOUTH, 0xffffffff));
        } else if (buffer == null)
            buffer = new ArrayList<>();
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        builder.addAll(originalModel.getQuads(state, side, rand));
        builder.addAll(buffer);
        return builder.build();
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
        return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, transform, cameraTransformType);
    }

    static class ModelFluidContainerOverride extends ItemOverrideList {

        static Map<String, IBakedModel> buffer = new HashMap<>();

        public ModelFluidContainerOverride() {
            super(ImmutableList.of());
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
            Fluid fluid = ItemStackHelper.NBT.getFluid(stack);
            if (fluid != null && buffer.containsKey(fluid.getName()))
                return buffer.get(fluid.getName());
            else if (fluid != null) {
                buffer.put(fluid.getName(),
                        new ModelFluidContainer(((ModelFluidContainer) originalModel).originalModel,
                                Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getStill().toString()), fluid.getColor()));
                return buffer.get(fluid.getName());
            } else
                return originalModel;
        }
    }
}
