package me.towdium.jecalculation.data.label.labels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import me.towdium.jecalculation.Tags;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@ParametersAreNonnullByDefault
public class LPlaceholder extends ILabel.Impl {
    public static final String KEY_NAME = "name";
    public static final String IDENTIFIER = "placeholder";
    public static boolean state = true; // true for client, false for server
    static Utilities.Recent<LPlaceholder> recentClient = new Utilities.Recent<>(100);
    static Utilities.Recent<LPlaceholder> recentServer = new Utilities.Recent<>(100);

    String name;

    static Utilities.Recent<LPlaceholder> getActive() {
        return state ? recentClient : recentServer;
    }

    public LPlaceholder(NBTTagCompound tag) {
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

    public static void onLogOut() {
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
    public NBTTagCompound toNbt() {
        NBTTagCompound nbt = super.toNbt();
        nbt.setString(KEY_NAME, name);
        return nbt;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawLabel(JecaGui gui) {
        gui.drawResource(Resource.LBL_UNIV_B, 0, 0);
        gui.drawResource(Resource.LBL_UNIV_F, 0, 0, (name.hashCode() * 0x131723) & 0xFFFFFF);
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
    @SideOnly(Side.CLIENT)
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
    @SideOnly(Side.CLIENT)
    public void getToolTip(List<String> existing, boolean detailed) {
        super.getToolTip(existing, detailed);
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + Tags.MODNAME);
    }

    public static boolean merge(ILabel a, ILabel b) {
        if (a instanceof LPlaceholder && b instanceof LPlaceholder) {
            LPlaceholder lpA = (LPlaceholder) a;
            LPlaceholder lpB = (LPlaceholder) b;
            return lpA.name.equals(lpB.name);
        } else return false;
    }

    public static class Converter {
        @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
        static List<Pair<Pattern, Function<Matcher, LPlaceholder>>> converters = Arrays.asList(new Pair<>(
                Pattern.compile("\\[\\[Gas: mekanism:(.+)], (\\d+)]"),
                i -> new LPlaceholder("Gas - " + capitalize(i.group(1)), Integer.parseInt(i.group(2)))));

        public static String capitalize(String s) {
            String[] arr = s.replace('_', ' ').split(" ");
            StringBuilder sb = new StringBuilder();

            for (String value : arr) {
                sb.append(Character.toUpperCase(value.charAt(0)));
                sb.append(value.substring(1)).append(" ");
            }
            return sb.toString().trim();
        }

        public static LPlaceholder from(Object o) {
            String s = o.toString();
            return converters.stream()
                    .map(i -> {
                        Matcher m = i.one.matcher(s);
                        return m.matches() ? i.two.apply(m) : null;
                    })
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseGet(() -> new LPlaceholder(s, 1));
        }
    }
}
