package me.towdium.jecalculation.command.commands;

import me.towdium.jecalculation.command.Commands;
import me.towdium.jecalculation.command.SubCommand;
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
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 0) {
            sender.addChatMessage(new ChatComponentTranslation("command.unexpected_arg", String.join(" ", args)));
            return;
        }

        sender.addChatMessage(new ChatComponentTranslation("command.help.list"));
        Commands.commands.values().stream().sorted(Comparator.comparing(SubCommand::getName))
                         .forEachOrdered(c -> sender.addChatMessage(new ChatComponentText(c.getUsage(sender))));
    }
}
