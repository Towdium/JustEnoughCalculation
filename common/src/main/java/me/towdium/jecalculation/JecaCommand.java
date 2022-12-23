package me.towdium.jecalculation;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.towdium.jecalculation.gui.JecaGui;
import net.minecraft.network.chat.Component;

import static me.towdium.jecalculation.utils.Utilities.getPlayer;

public class JecaCommand {
    public static <T> void register(CommandDispatcher<T> dispatcher) {
        LiteralArgumentBuilder<T> lab = LiteralArgumentBuilder.<T>literal("jeca")
                .executes((c) -> {
                    String key = "jecalculation.chat.help";
                    getPlayer().displayClientMessage(Component.translatable(key), false);
                    return 0;
                })
                .then(LiteralArgumentBuilder.<T>literal("craft")
                        .executes((c) -> JecaGui.openGuiCraft(null, 0)))
                .then(LiteralArgumentBuilder.<T>literal("math")
                        .executes((c) -> JecaGui.openGuiMath(null, 0)));
        dispatcher.register(lab);
    }
}
