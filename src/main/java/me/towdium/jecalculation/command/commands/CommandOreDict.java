package me.towdium.jecalculation.command.commands;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.command.ISubCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

/**
 * Author: towdium
 * Date:   17-9-11.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CommandOreDict implements ISubCommand {
    @Override
    public String getName() {
        return "ore";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/jec ore";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            Arrays.stream(OreDictionary.getOreNames()).forEach(s -> sender.sendMessage(new TextComponentString(s)));
        } // TODO error handling
    }
}
