package me.towdium.jecalculation.command.commands;

import me.towdium.jecalculation.command.Commands;
import me.towdium.jecalculation.command.ISubCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
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
    public void execute(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentTranslation(getKey("list")));
        Commands.commands.values().stream().sorted(Comparator.comparing(ISubCommand::getName))
                         .forEachOrdered(c -> sender.addChatMessage(new ChatComponentText(c.getUsage(sender))));
    }
}
