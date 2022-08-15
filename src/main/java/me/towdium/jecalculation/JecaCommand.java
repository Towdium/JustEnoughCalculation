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
                        .executes((c) -> JecaGui.openGuiMath(null, 0)))
                .then(Commands.literal("use_old_label_buttons")
                        .executes(c -> {
                            boolean inverted = !JecaConfig.useOldLabelButtons.get();
                            JecaConfig.useOldLabelButtons.set(inverted);
                            JecaConfig.useOldLabelButtons.save();
                            String key = "jecalculation.command.use_old_label_buttons." + inverted;
                            c.getSource().sendSuccess(new TranslatableComponent(key), false);
                            return Command.SINGLE_SUCCESS;
                        })
                );
        dispatcher.register(lab);
    }
}
