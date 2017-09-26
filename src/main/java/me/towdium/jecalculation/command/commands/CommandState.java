package me.towdium.jecalculation.command.commands;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.command.SubCommand;
import me.towdium.jecalculation.utils.Utilities.L18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@MethodsReturnNonnullByDefault
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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(new TextComponentTranslation("command.state.desc",
                L18n.format(JustEnoughCalculation.side.toString())));
    }
}
