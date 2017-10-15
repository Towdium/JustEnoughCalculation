package me.towdium.jecalculation.command.commands;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.command.SubCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-10-15.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CommandUuid implements SubCommand {
    @Override
    public String getName() {
        return "uuid";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/jec uuid";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        Entity e = sender.getCommandSenderEntity();
        if (e != null) sender.sendMessage(new TextComponentString(e.getUniqueID().toString()));
    }
}
