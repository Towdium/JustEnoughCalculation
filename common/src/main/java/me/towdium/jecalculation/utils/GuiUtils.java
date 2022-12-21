package me.towdium.jecalculation.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

//Thanks to Forge
@Environment(EnvType.CLIENT)
public class GuiUtils {
    public static void drawContinuousTexturedBox(PoseStack poseStack, ResourceLocation res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, res);
        drawContinuousTexturedBox(poseStack, x, y, u, v, width, height, textureWidth, textureHeight, topBorder, bottomBorder, leftBorder, rightBorder, zLevel);
    }

    public static void drawContinuousTexturedBox(PoseStack poseStack, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int fillerWidth = textureWidth - leftBorder - rightBorder;
        int fillerHeight = textureHeight - topBorder - bottomBorder;
        int canvasWidth = width - leftBorder - rightBorder;
        int canvasHeight = height - topBorder - bottomBorder;
        int xPasses = canvasWidth / fillerWidth;
        int remainderWidth = canvasWidth % fillerWidth;
        int yPasses = canvasHeight / fillerHeight;
        int remainderHeight = canvasHeight % fillerHeight;
        drawTexturedModalRect(poseStack, x, y, u, v, leftBorder, topBorder, zLevel);
        drawTexturedModalRect(poseStack, x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder, zLevel);
        drawTexturedModalRect(poseStack, x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder, zLevel);
        drawTexturedModalRect(poseStack, x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder, zLevel);

        int i;
        for (i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); ++i) {
            drawTexturedModalRect(poseStack, x + leftBorder + i * fillerWidth, y, u + leftBorder, v, i == xPasses ? remainderWidth : fillerWidth, topBorder, zLevel);
            drawTexturedModalRect(poseStack, x + leftBorder + i * fillerWidth, y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, i == xPasses ? remainderWidth : fillerWidth, bottomBorder, zLevel);

            for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); ++j) {
                drawTexturedModalRect(poseStack, x + leftBorder + i * fillerWidth, y + topBorder + j * fillerHeight, u + leftBorder, v + topBorder, i == xPasses ? remainderWidth : fillerWidth, j == yPasses ? remainderHeight : fillerHeight, zLevel);
            }
        }

        for (i = 0; i < yPasses + (remainderHeight > 0 ? 1 : 0); ++i) {
            drawTexturedModalRect(poseStack, x, y + topBorder + i * fillerHeight, u, v + topBorder, leftBorder, i == yPasses ? remainderHeight : fillerHeight, zLevel);
            drawTexturedModalRect(poseStack, x + leftBorder + canvasWidth, y + topBorder + i * fillerHeight, u + leftBorder + fillerWidth, v + topBorder, rightBorder, i == yPasses ? remainderHeight : fillerHeight, zLevel);
        }

    }

    public static void drawTexturedModalRect(PoseStack poseStack, int x, int y, int u, int v, int width, int height, float zLevel) {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder wr = tessellator.getBuilder();
        wr.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix = poseStack.last().pose();
        wr.vertex(matrix, (float) x, (float) (y + height), zLevel).uv((float) u * 0.00390625F, (float) (v + height) * 0.00390625F).endVertex();
        wr.vertex(matrix, (float) (x + width), (float) (y + height), zLevel).uv((float) (u + width) * 0.00390625F, (float) (v + height) * 0.00390625F).endVertex();
        wr.vertex(matrix, (float) (x + width), (float) y, zLevel).uv((float) (u + width) * 0.00390625F, (float) v * 0.00390625F).endVertex();
        wr.vertex(matrix, (float) x, (float) y, zLevel).uv((float) u * 0.00390625F, (float) v * 0.00390625F).endVertex();
        tessellator.end();
    }

}
