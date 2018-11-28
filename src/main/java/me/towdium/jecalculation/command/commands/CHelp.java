package me.towdium.jecalculation.command.commands;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.command.Commands;
import me.towdium.jecalculation.command.ISubCommand;
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
public class CHelp implements ISubCommand {
    public static final String NAME = "help";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/jeca [help]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        sender.sendMessage(new TextComponentTranslation(getKey("list")));
        Commands.commands.values().stream().sorted(Comparator.comparing(ISubCommand::getName))
                .forEachOrdered(c -> sender.sendMessage(new TextComponentString(c.getUsage(sender))));
    }
}
