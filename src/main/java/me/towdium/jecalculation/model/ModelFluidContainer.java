package me.towdium.jecalculation.model;

import com.google.common.collect.ImmutableList;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.util.helpers.ItemStackHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Towdium
 * Date:   2016/7/22.
 */

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ModelFluidContainer implements IBakedModel {
    public static final ModelResourceLocation modelResourceLocation = new ModelResourceLocation("jecalculation:item_fluid_container", "inventory");
    public static final ResourceLocation liquidResourceLocation = new ResourceLocation("jecalculation:items/item_fluid_container__liquid");
    //public static final ResourceLocation coverResourceLocation = new ResourceLocation("jecalculation:items/itemFluidContainer_cover");

    int color;
    IBakedModel originalModel;
    TextureAtlasSprite particle;
    List<BakedQuad> buffer;

    public ModelFluidContainer(IBakedModel originalModel, @Nullable TextureAtlasSprite particle, int color) {
        this.originalModel = originalModel;
        this.particle = particle;
        this.color = color;
        if (particle != null) {
            buffer = new ArrayList<>(2);
            TextureAtlasSprite fluid = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(liquidResourceLocation.toString());
            buffer.addAll(ItemTextureQuadConverter.convertTexture(DefaultVertexFormats.ITEM, TRSRTransformation.identity(), fluid, particle, 7.498f / 16f, EnumFacing.NORTH, color));
            buffer.addAll(ItemTextureQuadConverter.convertTexture(DefaultVertexFormats.ITEM, TRSRTransformation.identity(), fluid, particle, 8.502f / 16f, EnumFacing.SOUTH, color));
        } else
            buffer = new ArrayList<>();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
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

    static class ModelFluidContainerOverride extends ItemOverrideList {

        static Map<String, IBakedModel> buffer = new HashMap<>();

        public ModelFluidContainerOverride() {
            super(ImmutableList.of());
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, @Nullable ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
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
