package me.towdium.jecalculation.command.commands;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.command.Commands;
import me.towdium.jecalculation.command.SubCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CommandHelp implements SubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/jec [help]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 0) {
            sender.sendMessage(new TextComponentTranslation("command.common.unexpected_arg", String.join(" ", args)));
            return;
        }

        sender.sendMessage(new TextComponentTranslation("command.help.list"));
        Commands.commands.values().stream().sorted(Comparator.comparing(SubCommand::getName))
                .forEachOrdered(c -> sender.sendMessage(new TextComponentString(c.getUsage(sender))));
    }
}
