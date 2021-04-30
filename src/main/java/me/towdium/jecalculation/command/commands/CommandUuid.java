package me.towdium.jecalculation.command.commands;

import me.towdium.jecalculation.command.ISubCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-10-15.
 */
@ParametersAreNonnullByDefault
public class CommandUuid implements ISubCommand {
    @Override
    public String getName() {
        return "uuid";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/jec uuid";
    }


    @Override
    public void execute(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentText(sender.getCommandSenderName()));
    }
}
