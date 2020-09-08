package me.towdium.jecalculation.data.label.labels;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(Dist.CLIENT)
public class LPlaceholder extends ILabel.Impl {
    public static final String KEY_NAME = "name";
    public static final String IDENTIFIER = "placeholder";
    public static boolean state = true;  // true for client, false for common
    static Utilities.Recent<LPlaceholder> recentClient = new Utilities.Recent<>(100);
    static Utilities.Recent<LPlaceholder> recentServer = new Utilities.Recent<>(100);

    String name;

    static Utilities.Recent<LPlaceholder> getActive() {
        return state ? recentClient : recentServer;
    }

    public LPlaceholder(CompoundNBT tag) {
        super(tag);
        name = tag.getString(KEY_NAME);
        getActive().push(new LPlaceholder(name, 1, true), false);
    }

    public LPlaceholder(String name, long amount) {
        this(name, amount, false);
    }

    public LPlaceholder(String name, long amount, boolean silent) {
        super(amount, false);
        this.name = name;
        if (!silent) getActive().push(new LPlaceholder(name, 1, true), false);
    }

    public LPlaceholder(LPlaceholder label) {
        super(label);
        name = label.name;
    }

    @SubscribeEvent
    public static void onLogOut(ClientPlayerNetworkEvent.LoggedOutEvent e) {
        recentServer.clear();
        state = true;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    public static List<ILabel> getRecent() {
        return getActive().toList().stream().map(LPlaceholder::copy).collect(Collectors.toList());
    }

    @Override
    public CompoundNBT toNbt() {
        CompoundNBT nbt = super.toNbt();
        nbt.putString(KEY_NAME, name);
        return nbt;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawLabel(MatrixStack matrixStack, JecaGui gui) {
        gui.drawResource(matrixStack, Resource.LBL_UNIV_B, 0, 0);
        gui.drawResource(matrixStack, Resource.LBL_UNIV_F, 0, 0, (name.hashCode() * 0x131723) & 0xFFFFFF);
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ super.hashCode();
    }

    @Nullable
    @Override
    public Object getRepresentation() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return Utilities.I18n.get("label.placeholder.name", name);
    }

    @Override
    public boolean matches(Object l) {
        return l instanceof LPlaceholder && name.equals(((LPlaceholder) l).name) && super.matches(l);
    }

    @Override
    public LPlaceholder copy() {
        return new LPlaceholder(this);
    }

    @Override
    public void getToolTip(List<String> existing, boolean detailed) {
        super.getToolTip(existing, detailed);
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + JustEnoughCalculation.MODNAME);
    }

    public static boolean merge(ILabel a, ILabel b) {
        if (a instanceof LPlaceholder && b instanceof LPlaceholder) {
            LPlaceholder lpA = (LPlaceholder) a;
            LPlaceholder lpB = (LPlaceholder) b;
            return lpA.name.equals(lpB.name);
        } else return false;
    }
}
