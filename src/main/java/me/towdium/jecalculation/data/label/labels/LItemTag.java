package me.towdium.jecalculation.data.label.labels;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-10.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LItemTag extends LTag<Item> {
    public static final String IDENTIFIER = "itemTag";

    public LItemTag(ResourceLocation name) {
        super(name);
    }

    public LItemTag(ResourceLocation name, long amount) {
        super(name, amount);
    }

    public LItemTag(LTag<Item> lt) {
        super(lt);
    }

    public LItemTag(CompoundNBT nbt) {
        super(nbt);
    }

    @Override
    protected void drawLabel(JecaGui gui) {
        Object o = getRepresentation();
        if (o instanceof ItemStack) gui.drawItemStack(0, 0, (ItemStack) o, false);
        gui.drawResource(Resource.LBL_FRAME, 0, 0);
    }

    @Override
    public LTag copy() {
        return new LItemTag(this);
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Context<Item> getContext() {
        return Context.ITEM;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getDisplayName() {
        return Utilities.I18n.get("label.item_tag.name", name);
    }
}
