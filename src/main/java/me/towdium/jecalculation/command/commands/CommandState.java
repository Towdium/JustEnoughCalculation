package me.towdium.jecalculation.command.commands;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.command.SubCommand;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@ParametersAreNonnullByDefault
public class CommandState implements SubCommand {
    @Override
    public String getName() {
        return "state";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/jec state";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        sender.addChatMessage(new ChatComponentTranslation("command.state.desc",
                                                           Utilities.L18n.format(JustEnoughCalculation.side.toString())));
    }
}
