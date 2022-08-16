package me.towdium.jecalculation;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.towdium.jecalculation.gui.JecaGui;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;

import static me.towdium.jecalculation.utils.Utilities.getPlayer;

public class JecaCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        LiteralArgumentBuilder<CommandSourceStack> lab = Commands.literal("jeca")
                .executes((c) -> {
                    String key = "jecalculation.chat.help";
                    getPlayer().displayClientMessage(new TranslatableComponent(key), false);
                    return 0;
                })
                .then(Commands.literal("craft")
                        .executes((c) -> JecaGui.openGuiCraft(null, 0)))
                .then(Commands.literal("math")
                        .executes((c) -> JecaGui.openGuiMath(null, 0)));
        dispatcher.register(lab);
    }
}
